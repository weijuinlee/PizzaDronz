package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the creation of a JSON file for a list of orders.
 */
public class Delivery {

    /**
     * Serializes a list of orders into a JSON file.
     *
     * @param orders The list of orders to serialize.
     * @param date   The date associated with these orders, used in the filename.
     */
    public static void writeToFile(List<Order> orders, String date) {
        String deliveriesFileName = "deliveries-" + date + ".json";
        List<SingleOrder> singleOrderList = new ArrayList<>();

        // Convert each Order object to a SingleOrder object
        for (Order order : orders) {
            singleOrderList.add(new SingleOrder(order));
        }

        // Write the SingleOrder list to a JSON file
        try (FileWriter writer = new FileWriter("resultfiles/" + deliveriesFileName)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(singleOrderList, writer);
            System.out.println("[Info]: Delivery file written to " + deliveriesFileName + ".");
        } catch (IOException e) {
            System.err.println("[Error]: Unable to write deliveries to " + deliveriesFileName + " - " + e.getMessage() + ".");
        }
    }

}
