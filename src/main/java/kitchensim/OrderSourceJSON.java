package kitchensim;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Static factory method and interface allows for different data sources to be used.
 * <p>
 * An source that pushes orders at a fixed rate to the Kitchen is the same as having the Kitchen
 * poll for an order at a fixed rate.
 */
public class OrderSourceJSON implements OrderSource {

    private Order[] orders = null;
    private int index;

    public static OrderSourceJSON create() throws Exception {
        OrderSourceJSON source = new OrderSourceJSON();
        source.getOrders();
        return source;
    }

    private OrderSourceJSON() { }

    // TODO: put orders file name in properties file
    // There are at least three exceptions that can be throw from this method. None are recoverable by the
    // the system.
    private void getOrders() throws Exception {
        byte[] jsonOrders = Files.readAllBytes(Paths.get("orders.json"));
        ObjectMapper mapper = new ObjectMapper();
        orders = mapper.readValue(jsonOrders, Order[].class);
        System.out.println("OrderSourceJSON: ***** " + orders.length + " orders in array *****");
    }

    @Override
    public Order getNextOrder() {
        Order o = orders[index++];
        System.out.println("OrderSourceJSON: " + index +" sending order " + o.getId() + " to Kitchen");
        return o;
    }


}
