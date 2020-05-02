package kitchensim;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.OptionalInt;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class TestKitchen {

    @Test
    public void testPropertiesForCapacity() throws Exception {
        Properties props = new Properties();
        props.put("hot", "20");
        OptionalInt opt = Kitchen.checkProperty("hot", props);
        int i = opt.getAsInt();
        assertEquals(20, i);

        props.put("hot", -1);
        opt = Kitchen.checkProperty("hot", props);
        assertTrue(opt.isEmpty());

        props.put("cold", "");
        opt = Kitchen.checkProperty("cold", props);
        assertTrue(opt.isEmpty());

        props.put("cold", "abdcefg");
        opt = Kitchen.checkProperty("cold", props);
        assertTrue(opt.isEmpty());
    }

    private Order createAOrder() {
        Order o = new Order();
        o.setId("690b85f7-8c7d-4337-bd02-04e04454c826");
        o.setName("Yogurt");
        o.setDecayRate(0.37F);
        o.setShelfLife(300);
        o.setTemp("cold");
        return o;
    }

    private Order createAOrder(String orderId) {
        Order o = new Order();
        o.setId(orderId);
        o.setName("Yogurt");
        o.setDecayRate(0.37F);
        o.setShelfLife(300);
        o.setTemp("cold");
        return o;
    }

    // Note: this test is highly coupled to the System.out.println() messages that the application generates.
    @Test
    public void testPutOrderOnShelf() {
        ConcurrencyBehavior cb = new NotConcurrent();
        KitchenDefault kitchen = KitchenDefault.create(null, cb);

        // verify there is no order on the shelf
        Order o = createAOrder();
        o.setCreationTime(System.currentTimeMillis());

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        System.setOut(new PrintStream(outContent));
        Order yogurt = kitchen.getOrder(o.getId(), ShelfDefault.ShelfType.COLD);
        assertNull(yogurt);

        String expected = "Kitchen: put order: " + o.getId() + " on "
                + ShelfDefault.ShelfType.valueOf(o.getTemp().toUpperCase()) + " shelf";
        kitchen.putOrderOnShelf(o);
        String[] result = outContent.toString().split("\n");
        assertEquals(expected, result[0].trim());

        // Test putting order on overflow. We already have an order on the COLD shelf. We add nine more to fill up
        // the shelf, then ensure that the next order goes to the overflow.
        for (int i = 0; i < 9; i++) {
            o = createAOrder(String.valueOf(i));
            o.setCreationTime(System.currentTimeMillis());
            kitchen.putOrder(o);
        }

        outContent.reset();

        // Cold shelf is now full, put next order on Overflow
        o = createAOrder("11");
        o.setCreationTime(System.currentTimeMillis());
        kitchen.putOrder(o);
        expected = "Kitchen: put order: " + o.getId() + " on OVERFLOW shelf";
        result = outContent.toString().split("\n");
        assertEquals(expected, result[0].trim());

        System.setOut(originalOut);
    }

    @Test
    public void testFindMoveableOrder() {
        ConcurrencyBehavior cb = new NotConcurrent();
        KitchenDefault kitchen = KitchenDefault.create(null, cb);

        // verify there is no order on the shelf
        Order o = createAOrder();
        o.setCreationTime(System.currentTimeMillis());

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        System.setOut(new PrintStream(outContent));
        Order yogurt = kitchen.getOrder(o.getId(), ShelfDefault.ShelfType.COLD);
        assertNull(yogurt);

        // Fill up the COLD shelf, Fill up the Overflow shelf
        for (int i = 0; i <= 25; i++) {
            o = createAOrder(String.valueOf(i));
            o.setCreationTime(System.currentTimeMillis());
            kitchen.putOrder(o);
        }

        o = createAOrder();
        o.setCreationTime(System.currentTimeMillis());

        outContent.reset();

        // At this point, COLD and OVERFLOW should be full, adding another order should result in an order getting
        // discarded.
        o = createAOrder("oneTooMany");
        o.setCreationTime(System.currentTimeMillis());
        kitchen.putOrder(o);
        String[] result = outContent.toString().split("\n");
        String expected = "discarded";
        assertTrue(result[0].trim().contains(expected));

        // I do not have a unit test for if the overflow shelf is full, an existing order of your choosing on the
        // overflow should be moved to an allowable shelf with room and the new order is placed on the overflow shelf.

        System.setOut(originalOut);

    }
}
