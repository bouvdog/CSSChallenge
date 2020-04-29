package kitchensim;

/**
 * This class creates an instance of the source of the orders and launches the kitchen with that reference.
 */
public class ACloudKitchen {

   public static void main(String[] args) {
       try {
           OrderSource source = OrderSourceJSON.create();
           Kitchen kitchen = KitchenDefault.create(source);
           kitchen.startCooking();
       } catch (Exception e) {
           System.out.println("There was a problem reading the orders file: " + e.getMessage());
       }

   }
}