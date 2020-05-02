package kitchensim;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class TestShelf {

    @Test
    public void testShelfLife() {
        Shelf s = new ShelfDefault(10, ShelfDefault.ShelfType.COLD);

        // Putting essentially an expired order on the shelf
        Order o = createAOrder();
        o.setCreationTime(System.currentTimeMillis()-20000);
        s.putOrder(o);
        s.ageOrders();
        o = s.pullOrder(o.getId());
        assertNull(o);

        // Puts an order that should still have some time to live
        o = createAOrder();
        o.setCreationTime(System.currentTimeMillis() - 5000);
        s.putOrder(o);
        s.ageOrders();
        Order someOrder = s.pullOrder(o.getId());
        assertNotNull(someOrder);
    }

    private Order createAOrder() {
        Order o = new Order();
        o.setId("690b85f7-8c7d-4337-bd02-04e04454c826");
        o.setName("Yogurt");
        o.setDecayRate(0.37F);
        o.setShelfLife(5);
        o.setTemp("cold");
        return o;
    }

    @Test
    public void testReturnOrderOfTempType() throws Exception {
        byte[] jsonOrders = Files.readAllBytes(Paths.get("orders.json"));

        ObjectMapper mapper = new ObjectMapper();
        Order[] orders = mapper.readValue(jsonOrders, Order[].class);

        Shelf shelf = new ShelfDefault(10, ShelfDefault.ShelfType.COLD);
        Order o;
        for (int i = 0; i < 10; i++ ) {
            o = orders[i];
            o.setCreationTime(System.currentTimeMillis());
            shelf.putOrder(orders[i]);
        }

        // Shelf is now full
        assertFalse(shelf.hasRoomForOrder());
        o  = shelf.returnOrderOfTempType("cold");
        assertEquals("cold", o.getTemp());

        // Shelf has room for an order now
        assertTrue(shelf.hasRoomForOrder());

        // Shelf should return an empty Optional if no order of the requested temp type is present on the shelf
        o = createAOrder();
        o.setTemp("hot");
        o.setCreationTime(System.currentTimeMillis());

        shelf = new ShelfDefault(1, ShelfDefault.ShelfType.COLD);
        shelf.putOrder(o);
        o = shelf.returnOrderOfTempType("cold");
        assertTrue(o == null);
    }

    @Test
    public void testTimeToLife() {
        ShelfDefault shelf = new ShelfDefault(1, ShelfDefault.ShelfType.COLD);
        Order o = createAOrder();
        o.setCreationTime(System.currentTimeMillis());
        o.setShelfLife(300);
        shelf.putOrder(o);
        int value = shelf.calculateTimeToLive(o);
        assertEquals(300, value);

        // Decay rate affects the time to live. In this case the decay rate is 0.37
        o.setCreationTime(System.currentTimeMillis()-800000);
        value = shelf.calculateTimeToLive(o);
        assertEquals(4, value);

    }
}
