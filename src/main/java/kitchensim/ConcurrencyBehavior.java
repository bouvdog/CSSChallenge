package kitchensim;

/**
 * This exists to enable testing of the logic in Kitchen without having to deal with
 * couriers. This is a template method pattern (little bit of strategy too).
 */
public interface ConcurrencyBehavior {

    void enableCouriers(int numberOfCouriers);

    void launchCourier(Kitchen k, Order o);

    void shutdown();

}
