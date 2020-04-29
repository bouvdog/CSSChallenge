package kitchensim;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * This class represents a 'shelf' in the kitchen.
 * A shelf holds one temperature type of food (hot, cold, frozen)
 * A shelf has a finite capacity
 */
public class ShelfDefault implements Shelf {

    private ConcurrentMap<String, Order> shelf = new ConcurrentHashMap<>();
    private int capacity;

    public ShelfDefault(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void putOrder(Order order) {
        shelf.put(order.getId(), order);
    }

    // It is possible an order decayed while on the shelf, thus the Optional
    @Override
    public Order pullOrder(String orderId) {
        Order o = shelf.get(orderId);
        shelf.remove(orderId);
        return o;
    }

    @Override
    public void discardBadOrder(String orderId) {

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
    public Optional<Order> returnOrderOfTempType(String temp) {
        Collection<Order> onShelf = shelf.values();
        List<Order> result = onShelf.stream()
                .filter(o -> o.getTemp().equals(temp))
                .limit(2)
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(pullOrder(result.get(0).getId()));
        }
    }

    @Override
    public void discardAny() {
        List<String> keys = new ArrayList<>(shelf.keySet());
        Random r = new Random();
        String id = keys.get(r.nextInt(keys.size()));
        System.out.println("Shelf: order " + id + " discarded");
        shelf.remove(id);
    }
}
