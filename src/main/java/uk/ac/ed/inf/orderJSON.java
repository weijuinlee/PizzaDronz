package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

// A class that matches the JSON format for the spec
class shortOrder{

    public shortOrder(Order order){
        String orderNo = order.getOrderNo();
        String orderStatus = order.getOrderStatus().toString();
        String orderValidationCode = order.getOrderValidationCode().toString();
        int costInPence = order.getPriceTotalInPence();
    }
}

public class orderJSON {
    public static void main(List<Order> orders,String date){

        //Define the path for the new JSON file
        String deliveriesFileName = "deliveries-"+date+".json";

        List<shortOrder> shortOrderList = new ArrayList<>();

        // For each order, create a new shortOrder instance to be added to the JSON
        for (Order order: orders) {
            shortOrderList.add(new shortOrder(order));
        }

        // Build the new JSON file using the shortOrder class and write to relevant file
        try (Writer writer = new FileWriter("resultfiles/"+deliveriesFileName)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(shortOrderList, writer);
            System.out.println("Delivery file written");
        } catch (IOException e) {
            System.err.println("Unable to write deliveries");
        }

    }


}