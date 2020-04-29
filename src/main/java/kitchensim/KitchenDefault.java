package kitchensim;

import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.*;

/**
 * This class  is the central class in this system.
 * It polls for orders from an order source
 * It places orders on the appropriate shelves
 * It spawns a Courier to pick up the order
 */
public class KitchenDefault implements Kitchen {

    private static String OVERFLOW = "overFlow";

    private OrderSource source;

    private List<String> shelfTypes;

    private int hotCapacity = 10;
    private int coldCapacity = 10;
    private int frozenCapacity = 10;
    private int overFlowCapacity = 15;

    private Map<String, Shelf> shelves = new HashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ExecutorService courierLauncher = Executors.newFixedThreadPool(4);

    private static ScheduledFuture<?> scheduledFuture;

    public static KitchenDefault create(OrderSource source) {
        System.out.println("Kitchen: Started");
        return new KitchenDefault(source);
    }

    private KitchenDefault(OrderSource source) {
        this.source = source;
        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream("challenge.properties"));
            OptionalInt oi = Kitchen.checkProperty("hotCapacity", prop);
            oi.ifPresent(value -> hotCapacity = value);

            oi = Kitchen.checkProperty("coldCapacity", prop);
            oi.ifPresent(value -> coldCapacity = value);

            oi = Kitchen.checkProperty("frozenCapacity", prop);
            oi.ifPresent(value -> frozenCapacity = value);

            oi = Kitchen.checkProperty("overFlowCapacity", prop);
            oi.ifPresent(value -> overFlowCapacity = value);

            String HOT = "hot";
            String COLD = "cold";
            String FROZEN = "frozen";
            String OVERFLOW = "overflow";
            shelves.put(HOT, new ShelfDefault(hotCapacity));
            shelves.put(COLD, new ShelfDefault(coldCapacity));
            shelves.put(FROZEN, new ShelfDefault(frozenCapacity));
            shelves.put(OVERFLOW, new ShelfDefault(overFlowCapacity));

            shelfTypes = new ArrayList<>();
            shelfTypes.add(HOT);
            shelfTypes.add(COLD);
            shelfTypes.add(FROZEN);

        } catch (Exception e) {
            System.out.println("Could not find or read properties file. Using defaults.");
        }
    }

    // This method and the one below it (findMoveableOrder) could be replaced by a State pattern.
    // However, I didn't think the increased bulk associated with the State pattern was justified. If
    // it was likely that the shelf logic would change, then the State pattern would make that easier and
    // would be justifed.
    public void putOrder(Order o) {
        Shelf shelf = shelves.get(o.getTemp());
        if (shelf.hasRoomForOrder()) {
            shelf.putOrder(o);
        } else {
            shelf = shelves.get(OVERFLOW);
            if (shelf.hasRoomForOrder()) {
                shelf.putOrder(o);
            } else {
                findMoveableOrder(o);
            }
        }
        System.out.println("Kitchen: put order: " + o.getId() + " on " + o.getTemp() + " shelf");
        courierLauncher.submit(CourierDefault.create(this, o.getId(), o.getTemp()));
    }

    // If the overflow shelf is full, an existing order of your choosing on the
    // overflow should be moved to an allowable shelf with room. If no such move is possible, a random order
    // from the overflow shelf should be discarded as waste (and will not be available for a courier pickup).
    private void findMoveableOrder(Order toOverflowShelf) {
        Shelf overflow = shelves.get(OVERFLOW);
        boolean thereIsNoRoom = true;
        for (String tempType : shelfTypes) {
            Shelf shelf = shelves.get(tempType);
            if (shelf.hasRoomForOrder()) {
                Optional<Order> toBeMoved = overflow.returnOrderOfTempType(tempType);
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
    public synchronized Order getOrder(String orderId, String tempShelf) {
        Order o;
        Shelf shelf = shelves.get(tempShelf);
        o = shelf.pullOrder(orderId);
        if (orderNotOnShelf(o)) {
            shelf = shelves.get(OVERFLOW);
            o = shelf.pullOrder(orderId);
            System.out.println("Kitchen: order " + o.getId() + " pulled from overflow shelf");
        } else {
            System.out.println("Kitchen: order " + o.getId() + " pulled from the "
                    + o.getTemp() + " shelf");
        }
        return o;
    }

    @Override
    public void startCooking() {
        scheduledFuture =
                scheduler
                        .scheduleAtFixedRate(() -> {
                                    Order o = source.getNextOrder();
                                    if (o == null) {
                                        scheduledFuture.cancel(true);
                                    } else {

                                        putOrder(o);
                                    }
                                },
                                2,
                                2,
                                TimeUnit.SECONDS
                        );
    }
}
