package kitchensim;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Concurrent implements ConcurrencyBehavior {

    private ExecutorService courierLauncher;

    @Override
    public void enableCouriers(int numberOfCouriers) {
        courierLauncher = Executors.newFixedThreadPool(numberOfCouriers);
    }

    @Override
    public void launchCourier(Kitchen k, Order o) {
        courierLauncher.submit(CourierDefault.create(k, o.getId(),
                ShelfDefault.ShelfType.valueOf(o.getTemp().toUpperCase())));
    }

    @Override
    public void shutdown() {
        courierLauncher.shutdown();
    }
}
