package kitchensim;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

// Shelves that have different temperatures don't have different behaviors other than a slight difference with
// the overflow shelf. If different behaviors are required, then we can extend the Shelf interface for these
// new behaviors.
public class ShelfDefault implements Shelf {

    private ConcurrentMap<String, Order> shelf = new ConcurrentHashMap<>();
    private int capacity;
    private int decayModifier;

    public ShelfDefault(int capacity, int decayModifier) {
        this.capacity = capacity;
        this.decayModifier = decayModifier;
    }

    @Override
    public void putOrder(Order order) {
        shelf.put(order.getId(), order);
    }

    // It is possible an order decayed while on the shelf
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

    @Override
    public void ageOrders(Map<String, Long> shelfLife) {
        for (Map.Entry<String,Order> entry : shelf.entrySet()) {
            long orderAge = (System.currentTimeMillis() - shelfLife.get(entry.getKey())) / 1000;
            float value = (entry.getValue().getShelfLife()
                    - (entry.getValue().getDecayRate()
                    * orderAge * decayModifier));
            if (value <= 0F) {
                shelf.remove(entry.getKey());
                System.out.println("Shelf: order " + entry.getValue().getId() + " decayed");
            } else {
                System.out.println("Shelf: order " + entry.getValue().getId()
                        + " has remaining shelf life of " + value);
            }
        }
    }

}
