package kitchensim;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestShelfDefault {

    @Test
    public void givenAFullOverFlowShelf_whenAskedToReturnAnOrder_thenReturnAppropriateOrder() throws Exception {
        byte[] jsonOrders = Files.readAllBytes(Paths.get("orders.json"));

        ObjectMapper mapper = new ObjectMapper();
        Order[] orders = mapper.readValue(jsonOrders, Order[].class);

        Shelf shelf = new ShelfDefault(10,1);
        for (int i = 0; i < 10; i++ ) {
            shelf.putOrder(orders[i]);
        }

        // Shelf is now full
        assertFalse(shelf.hasRoomForOrder());
        Optional<Order> o  = shelf.returnOrderOfTempType("cold");
        assertEquals("cold", o.get().getTemp());

        // Shelf has room for an order now
        assertTrue(shelf.hasRoomForOrder());
    }
}
