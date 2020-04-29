package kitchensim;

import java.util.Optional;

/**
 * Shelf is designed to be a component or attribute of Kitchen. Shelves are contained by the Kitchen, and the
 * Kitchen uses them to store and organize orders.
 */
public interface Shelf {

    /**
     * Used by the Kitchen to put an order on the appropriate shelf
     * @param order
     */
    void putOrder(Order order);


    Order pullOrder(String orderId);

    void discardBadOrder(String orderId);

    /**
     * Kitchen uses this method to determine if a Shelf has room for the order
     * @return
     */
    boolean hasRoomForOrder();

    /**
     * This method is used by the Kitchen to support the scenario when the appropriate temperature shelf is
     * full and when the overflow shelf is full (see specification).
     * @param temp
     * @return
     */
    Optional<Order> returnOrderOfTempType(String temp);

    /**
     * This method is used by the Kitchen to support the scenario hen the appropriate temperature shelf is
     * full and when the overflow shelf is full (see specification).
     */
    void discardAny();
}
