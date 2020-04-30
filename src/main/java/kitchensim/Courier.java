package kitchensim;

public interface Courier {

    /**
     * Removes an order from one of the shelves in the kitchen, assuming it didn't decay or wasn't discarded
     * A Courier object will contain the necessary information (orderId and shelf type) to retrieve the
     * order.
     */
    void fetchOrder();
}
