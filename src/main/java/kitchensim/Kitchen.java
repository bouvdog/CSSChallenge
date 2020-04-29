package kitchensim;

import java.util.OptionalInt;
import java.util.Properties;

public interface Kitchen {

    /**
     * Searches the appropriate temperature shelf and the overflow shelf for the order. It is possible that
     * the order has decayed or been discarded. This method is intended for use by the Couriers, which 'pick up'
     * the order and thereby remove it from the system.
     *
     * @param orderId
     * @param tempShelf
     */
    Order getOrder(String orderId, String tempShelf);

    /**
     * This method kicks off the process. It runs until the order component is out of orders
     */
    void startCooking();


    /**
     * This method checks that the values in the properties object are valid. These values are overrides
     * for defaults and defaults are used if the values in the properties file do not meet expectations.
     *
     * @param property
     * @param prop
     * @return
     */
    // TODO: upper bound on capacity?
    static OptionalInt checkProperty(String property, Properties prop) {
        int intValue = -1;
        try {
            String value = prop.getProperty(property);
            if (value != null) {
                intValue = Integer.parseInt(value);
            }
        } catch (NumberFormatException ignored) {
            // use default value for the shelf capacity
        }
        if (intValue > 0) {
            return OptionalInt.of(intValue);
        } else {
            return OptionalInt.empty();
        }
    }

}
