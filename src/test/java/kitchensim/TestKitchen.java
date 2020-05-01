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
}
