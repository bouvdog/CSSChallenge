package kitchensim;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * This class creates an instance of the source of the orders and starts it and the kitchen
 * The kitchen and order source communicate via blocking queue.
 */
public class ACloudKitchen {
    public static void main(String[] args) {
        BlockingQueue<Order> queue = new ArrayBlockingQueue<>(10);
        try {
            ExecutorService service = newFixedThreadPool(2);
            OrderSourceJSON source = OrderSourceJSON.create(queue);
            ConcurrencyBehavior couriers = new Concurrent();
            KitchenDefault kitchen = KitchenDefault.create(queue, couriers);
            service.submit(source);
            service.submit(kitchen);
            service.shutdown();
        } catch (Exception e) {
            System.out.println("There was a problem reading the orders file: " + e.getMessage());
        }
    }
}