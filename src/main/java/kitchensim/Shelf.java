package kitchensim;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Shelf is designed to be a component or attribute of Kitchen. Shelves are contained by the Kitchen, and the
 * Kitchen uses them to store and organize orders.
 */
public interface Shelf {

    /**
     * Used by the Kitchen to put an order on the appropriate shelf
     *
     * @param order a 'new' order from the kitchen
     */
    void putOrder(@NotNull Order order);

    /**
     * Used by the Kitchen to take an order off a shelf
     *
     * @param orderId the key to find the order to pull off the shelf
     * @return the order that matches the key or null if the order decayed or was discarded
     */
    @Nullable Order pullOrder(@NotNull String orderId);

    /**
     * Kitchen uses this method to determine if a Shelf has room for the order
     *
     * @return
     */
    boolean hasRoomForOrder();

    /**
     * This method is used by the Kitchen to support the scenario when the appropriate temperature shelf is
     * full and when the overflow shelf is full (see specification).
     *
     * @param temp the shelf's temperature
     * @return an Optional that will contain an order from 'that' shelf or null if the order
     * couldn't be found.
     */
    Optional<Order> returnOrderOfTempType(@NotNull String temp);

    /**
     * This method is used by the Kitchen to support the scenario hen the appropriate temperature shelf is
     * full and when the overflow shelf is full (see specification).
     */
    void discardAny();

    /**
     * This method checks the shelf for orders that have exceeded their shelf life
     * It will remove orders that have decayed.
     */
    void ageOrders();


}
