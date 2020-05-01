package kitchensim;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;
import java.util.Properties;

/**
 * This is the central class in this system.
 * It receives orders from the order source
 * It places orders on the appropriate shelves
 * It spawns a Courier to pick up the order
 */
public interface Kitchen {

    /**
     * Searches the appropriate temperature shelf and the overflow shelf for the order. It is possible that
     * the order has decayed or been discarded. This method is intended for use by the Couriers, which 'pick up'
     * the order and thereby remove it from the system.
     *
     * @param orderId       used as a key to find the desired order
     * @param type          used optimize the search for the order
     * @return              the sought for order or null
     */
    @Nullable Order getOrder(@NotNull String orderId, @NotNull ShelfDefault.ShelfType type);

    /**
     * This method starts the system. It runs until the order component is out of orders
     */
    void startCooking();


    /**
     * This method checks that the values in the properties object are valid.
     * Defaults are used if the values in the properties file do not meet expectations.
     *
     * @param property      the property key to be checked
     * @param prop          the property object
     * @return              an optional that contains the parsed integer or null
     */
    // TODO: upper bound on capacity?
    static OptionalInt checkProperty(@NotNull String property, @NotNull Properties prop) {
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
