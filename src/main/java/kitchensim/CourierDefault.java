package kitchensim;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This class removes an order, if available, from the shelves in the Kitchen.
 */
public class CourierDefault implements Courier, Runnable {

    private Kitchen kitchen;
    private String orderId;
    private String shelf;

    public static CourierDefault create(Kitchen k, String orderId, String shelf) {
        System.out.println("Courier: " + Thread.currentThread().getId()
                + " for order id: " + orderId + " created");
        return new CourierDefault(k, orderId, shelf);
    }

    private CourierDefault(Kitchen k, String orderId, String shelf) {
        this.kitchen = k;
        this.orderId = orderId;
        this.shelf = shelf;
    }

    @Override
    public void run() {
        fetchOrder();
    }

    @Override
    public void fetchOrder() {
        System.out.println("***** Courier: entering fetch order");
        try {
            // Wait two to six seconds before fetching order
            int randomNum = ThreadLocalRandom.current().nextInt(2, 6 + 1);
            Thread.sleep(randomNum * 1000);
            Order o = kitchen.getOrder(orderId, shelf);
            System.out.println("Courier: " + Thread.currentThread().getId()
                    + " Order id: " + orderId + orderStatus(o));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        System.out.println("***** Courier: exiting fetch order");
    }

    private String orderStatus(Order o) {
        if (o == null) {
            return " was NOT picked up";
        } else {
            return " was picked up ";
        }
    }


}
