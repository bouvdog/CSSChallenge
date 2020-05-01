package kitchensim;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This class removes an order, if available, from the shelves in the Kitchen.
 */
public class CourierDefault implements Courier, Runnable {

    private Kitchen kitchen;
    private String orderId;
    private ShelfDefault.ShelfType shelf;

    public static CourierDefault create(Kitchen k, String orderId, ShelfDefault.ShelfType shelf) {
        System.out.println("Courier: for order id: " + orderId + " created");
        return new CourierDefault(k, orderId, shelf);
    }

    private CourierDefault(Kitchen k, String orderId, ShelfDefault.ShelfType shelf) {
        this.kitchen = k;
        this.orderId = orderId;
        this.shelf = shelf;
    }

    @Override
    public void run() {
        fetchOrder();
        System.out.println("Courier exiting");
    }

    @Override
    public void fetchOrder() {
        try {
            // Wait two to six seconds before fetching order
            int randomNum = ThreadLocalRandom.current().nextInt(2, 6 + 1);
            Thread.sleep(randomNum * 1000);
            Order o = kitchen.getOrder(orderId, shelf);
            System.out.println("Courier: picked up order: " + orderId + orderStatus(o));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private String orderStatus(Order o) {
        if (o == null) {
            return " was NOT picked up";
        } else {
            return " was picked up ";
        }
    }


}
