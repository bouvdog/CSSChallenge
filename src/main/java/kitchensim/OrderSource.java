package kitchensim;

public interface OrderSource {

    /**
     * Supplies the 'next' order upon request from the Kitchen.
     * @return
     */
    Order getNextOrder();
}
