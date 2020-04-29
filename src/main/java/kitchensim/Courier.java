package kitchensim;

public interface Courier {

    /**
     * Remove an order from one of the shelves in the kitchen, assuming it didn't decay or wasn't discarded
     */
    void fetchOrder();
}
