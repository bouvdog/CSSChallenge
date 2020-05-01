package kitchensim;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.OptionalInt;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

/**
 * Static factory method and interface allows for different data sources to be used.
 * <p>
 * An source that pushes orders at a fixed rate to the Kitchen is the same as having the Kitchen
 * poll for an order at a fixed rate.
 */
public class OrderSourceJSON implements Runnable {

    private Order[] orders = null;
    private BlockingQueue<Order> queue;

    // Default order rate is twice a second.
    // Expressed as milliseconds
    private long orderRate = 500;


    public static OrderSourceJSON create(BlockingQueue<Order> q) throws Exception {
        OrderSourceJSON source = new OrderSourceJSON(q);
        source.getOrders();
        return source;
    }

    // If there is a problem with the properties file, we will just use defaults
    private OrderSourceJSON(BlockingQueue<Order> q) {
        try {
            Properties prop = new Properties();
            prop.load(new FileInputStream("./challenge.properties"));
            OptionalInt oi = Kitchen.checkProperty("ordersPerSecond", prop);
            oi.ifPresent(value -> orderRate = 1000 / value);
        } catch (Exception e) {
            System.out.println("Could not find or read properties file. Using defaults. " + e.getMessage());
        }
        queue = q;
    }

    // TODO: put orders file name in properties file
    // There are at least three exceptions that can be throw from this method. None are recoverable by the
    // the system.
    private void getOrders() throws Exception {
        byte[] jsonOrders = Files.readAllBytes(Paths.get("./orders.json"));
        ObjectMapper mapper = new ObjectMapper();
        orders = mapper.readValue(jsonOrders, Order[].class);
    }

    @Override
    public void run() {
        try {
            Order o;
            for (Order order : orders) {
                Thread.sleep(orderRate);
                o = order;
                o.setCreationTime(System.currentTimeMillis());
                queue.put(o);
                System.out.println("OrderSourceJSON: is sending order " + o.getId() + " to Kitchen");
            }

            // An order with no data is a signal that we are done
            Order empty = new Order();
            empty.setId("done");
            queue.put(empty);
        } catch (Exception ignored) {
        }
        System.out.println("OrderSourceJSON: all orders submitted");
    }
}
