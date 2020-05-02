package kitchensim;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// Shelves that have different temperatures don't have different behaviors other than a slight difference with
// the overflow shelf. If different behaviors are required, then we can extend the Shelf interface for these
// new behaviors.
public class ShelfDefault implements Shelf {

    public enum ShelfType {
        HOT, COLD, FROZEN, OVERFLOW
    }

    private Map<String, Order> shelf = new ConcurrentHashMap<>();
    private int capacity;
    private int decayModifier;
    private ShelfType type;

    public ShelfDefault(int capacity, ShelfType type) {
        this.capacity = capacity;
        if (type != ShelfType.OVERFLOW) {
            this.decayModifier = 1;
        } else {
            this.decayModifier = 2;
        }
        this.type = type;
    }

    @Override
    public void putOrder(@NotNull Order order) {
        shelf.put(order.getId(), order);
        printShelfContentsToConsole();
    }

    // It is possible an order decayed while on the shelf
    @Override
    public Order pullOrder(@NotNull String orderId) {
        Order o = shelf.get(orderId);
        if (o == null) {
            return null;
        } else {
            shelf.remove(orderId);
            printShelfContentsToConsole();
            return o;
        }
    }

    @Override
    public boolean hasRoomForOrder() {
        boolean hasRoom = false;
        if (shelf.size() < capacity) {
            hasRoom = true;
        }
        return hasRoom;
    }

    // Side-effect, removes order from shelf
    @Override
    public Order returnOrderOfTempType(@NotNull String temp) {
        Collection<Order> onShelf = shelf.values();
        List<Order> result = onShelf.stream()
                .filter(o -> o.getTemp().equals(temp))
                .limit(2)
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            return null;
        } else {
            return pullOrder(result.get(0).getId());
        }
    }

    @Override
    public void discardAny() {
        List<String> keys = new ArrayList<>(shelf.keySet());
        Random r = new Random();
        String id = keys.get(r.nextInt(keys.size()));
        System.out.println("Shelf: order " + id + " discarded");
        printShelfContentsToConsole();
        shelf.remove(id);
    }

    @Override
    public void ageOrders() {
        float value;
        for (Map.Entry<String, Order> entry : shelf.entrySet()) {

            value = calculateTimeToLive(entry.getValue());
            if (value <= 0F) {
                shelf.remove(entry.getKey());
                System.out.println("Shelf: order " + entry.getValue().getId() + " decayed");
                printShelfContentsToConsole();
            }
        }
    }

    // This is public for testing purposes
    public int calculateTimeToLive(Order o) {
        long orderAge = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - o.getCreationTime());
        return Math.round((o.getShelfLife()
                - (o.getDecayRate() * orderAge * decayModifier)));
    }

    private void printShelfContentsToConsole() {
        System.out.println(type.toString() + " shelf Contents *****");
        shelf.entrySet().stream()
                .forEach(entry -> System.out.println("Order: "
                        + entry.getValue().getId() + " has a remaining time to live of  "
                        + calculateTimeToLive(entry.getValue())));
        System.out.println("*****");
    }

}
