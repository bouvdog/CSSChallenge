package kitchensim;

public interface OrderSource {

    /**
     * Supplies the 'next' order upon request from the Kitchen.
     * @return      the 'next' order in the set of orders to be processed
     */
    Order getNextOrder();
}
