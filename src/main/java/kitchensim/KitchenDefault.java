package kitchensim;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the central class in this system.
 * It polls for orders from an order source
 * It places orders on the appropriate shelves
 * It spawns a Courier to pick up the order
 */
public class KitchenDefault implements Kitchen, Runnable {

    private int hotCapacity = 10;
    private int coldCapacity = 10;
    private int frozenCapacity = 10;
    private int overFlowCapacity = 15;

    private Map<ShelfDefault.ShelfType, Shelf> shelves = new HashMap<>();

    private BlockingQueue<Order> queue;

    private final ExecutorService courierLauncher = Executors.newFixedThreadPool(2);

    public static KitchenDefault create(BlockingQueue<Order> queue) {
        System.out.println("Kitchen: Started");
        return new KitchenDefault(queue);
    }

    private KitchenDefault(BlockingQueue<Order> queue) {
        this.queue = queue;
        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream("./challenge.properties"));
            OptionalInt oi = Kitchen.checkProperty("hotCapacity", prop);
            oi.ifPresent(value -> hotCapacity = value);

            oi = Kitchen.checkProperty("coldCapacity", prop);
            oi.ifPresent(value -> coldCapacity = value);

            oi = Kitchen.checkProperty("frozenCapacity", prop);
            oi.ifPresent(value -> frozenCapacity = value);

            oi = Kitchen.checkProperty("overFlowCapacity", prop);
            oi.ifPresent(value -> overFlowCapacity = value);

            shelves.put(ShelfDefault.ShelfType.HOT, new ShelfDefault(hotCapacity, ShelfDefault.ShelfType.HOT));
            shelves.put(ShelfDefault.ShelfType.COLD, new ShelfDefault(coldCapacity, ShelfDefault.ShelfType.COLD));
            shelves.put(ShelfDefault.ShelfType.FROZEN,
                    new ShelfDefault(frozenCapacity, ShelfDefault.ShelfType.FROZEN));
            shelves.put(ShelfDefault.ShelfType.OVERFLOW,
                    new ShelfDefault(overFlowCapacity, ShelfDefault.ShelfType.OVERFLOW));

        } catch (Exception e) {
            System.out.println("Could not find or read properties file. Using defaults.");
        }
    }

    // This method and the one below it (findMoveableOrder) could be replaced by a State pattern.
    // However, I didn't think the increased bulk associated with the State pattern was justified. If
    // it was likely that the shelf logic would change, then the State pattern would make that easier and
    // would be justifed.
    public void putOrder(Order o) {
        ShelfDefault.ShelfType type = ShelfDefault.ShelfType.valueOf(o.getTemp().toUpperCase());
        Shelf shelf = shelves.get(type);
        if (shelf.hasRoomForOrder()) {
            System.out.println("Kitchen: put order: " + o.getId() + " on " + type + " shelf");
            shelf.putOrder(o);
        } else {
            System.out.println("Kitchen: put order: " + o.getId() + " on OVERFLOW shelf");
            shelf = shelves.get(ShelfDefault.ShelfType.OVERFLOW);
            if (shelf.hasRoomForOrder()) {
                shelf.putOrder(o);
            } else {
                findMoveableOrder(o);
            }
        }
        shelf.ageOrders();
        courierLauncher.submit(CourierDefault.create(this, o.getId(),
                ShelfDefault.ShelfType.valueOf(o.getTemp().toUpperCase())));
    }

    // If the overflow shelf is full, an existing order of your choosing on the
    // overflow should be moved to an allowable shelf with room. If no such move is possible, a random order
    // from the overflow shelf should be discarded as waste (and will not be available for a courier pickup).
    private void findMoveableOrder(Order toOverflowShelf) {
        Shelf overflow = shelves.get(ShelfDefault.ShelfType.OVERFLOW);
        boolean thereIsNoRoom = true;
        for (ShelfDefault.ShelfType type : ShelfDefault.ShelfType.values()) {
            Shelf shelf = shelves.get(type);
            if (shelf.hasRoomForOrder()) {
                Optional<Order> toBeMoved = overflow.returnOrderOfTempType(type.toString().toLowerCase());
                toBeMoved.ifPresent(fromOverflowShelf -> shelf.putOrder(fromOverflowShelf));
                overflow.putOrder(toOverflowShelf);
                thereIsNoRoom = false;

                Order o = toBeMoved.orElse(new Order());
                System.out.println("Kitchen: moved order " + o.getId()
                        + " from Overflow to " + o.getTemp());
                System.out.println("Kitchen: placed order " + toOverflowShelf.getId()
                        + " on " + toOverflowShelf.getTemp() + " shelf");
                break;
            }
        }
        if (thereIsNoRoom) {
            overflow.discardAny();
            overflow.putOrder(toOverflowShelf);
        }
    }

    private boolean orderNotOnShelf(Order o) {
        return o == null;
    }

    // check the appropriate shelf and possibly the overflow shelf for the order
    @Override
    public synchronized Order getOrder(@NotNull String orderId, @NotNull ShelfDefault.ShelfType type) {
        Order o;
        Shelf shelf = shelves.get(type);
        o = shelf.pullOrder(orderId);
        if (orderNotOnShelf(o)) {
            shelf = shelves.get(ShelfDefault.ShelfType.OVERFLOW);
            o = shelf.pullOrder(orderId);
        }
        return o;
    }

    @Override
    public void run() {
        startCooking();
    }

    @Override
    public void startCooking() {
        try {
            Order o;
            while (true) {
                o = queue.take();
                if (o.getId().equals("done")) {
                    break;
                }
                putOrder(o);
            }
        } catch (InterruptedException ignored) {
        }
        courierLauncher.shutdown();
        System.out.println("Kitchen: done cooking");
    }
}
