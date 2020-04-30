package kitchensim;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestShelf {

    @Test
    public void testShelfLife() {
        Map<String, Long> shelfLife = new HashMap<>();
        Order o = new Order();
        o.setId("690b85f7-8c7d-4337-bd02-04e04454c826");
        o.setName("Yogurt");
        o.setDecayRate(0.37F);
        o.setShelfLife(5);
        o.setTemp("cold");
        Shelf s = new ShelfDefault(10,1);

        s.putOrder(o);
        // Putting essentially an expired order on the shelf
        shelfLife.put(o.getId(), System.currentTimeMillis()-20000);

        s.ageOrders(shelfLife);
        o = s.pullOrder(o.getId());
        assertNull(o);

        // Puts an order that should still have life left
        o = new Order();
        o.setId("690b85f7-8c7d-4337-bd02-04e04454c826");
        o.setName("Yogurt");
        o.setDecayRate(0.37F);
        o.setShelfLife(5);
        o.setTemp("cold");
        s.putOrder(o);
        shelfLife.put(o.getId(), System.currentTimeMillis()-5000);
        s.ageOrders(shelfLife);
        o = s.pullOrder(o.getId());
        assertNotNull(o);
    }
}
