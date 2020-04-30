package kitchensim;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestSystem {

    // Run this test with coverage enabled to see coverage
    @Test
    public void testSystem() {
        try {
            ExecutorService execute = Executors.newSingleThreadExecutor();
            OrderSource source = OrderSourceJSON.create();
            execute.submit(KitchenDefault.create(source));
            try {
                Thread.sleep(60 * 1000);
            } catch (Exception e)  {

            }
        } catch (Exception e) {
            System.out.println("There was a problem reading the orders file: " + e.getMessage());
        }
    }
}
