package kitchensim;

import org.junit.jupiter.api.Test;

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

    @Test
    public void smokeTestForExecutor() throws Exception {
        OrderSource source = OrderSourceJSON.create();
        Order o1 = source.getNextOrder();

        Kitchen kitchen = KitchenDefault.create(OrderSourceJSON.create());

        Order thereYet = kitchen.getOrder(o1.getId(), o1.getTemp());
        assertNull(thereYet);

        kitchen.startCooking();

        Thread.sleep(10000);
        Order deliveredOrder = kitchen.getOrder(o1.getId(), o1.getTemp());
        assertEquals("Banana Split", deliveredOrder.getName());
    }


}
