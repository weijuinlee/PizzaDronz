package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class for the pizzaDronz, this is the entry point of the application.
 */
public class App
{
    public static void main( String[] args )
    {
        System.out.println("[Info]: Main Application started");
        if (!argsValidator(args)) {
            return;
        }

        String date = args[0];
        String url = args[1];

        if (!url.endsWith("/")) {
            url += "/";
        }

        boolean clientIsAlive = new Client(url).isAlive();

        if (clientIsAlive){
            Client client = new Client(url);

            // Get responses from REST service.
            System.out.println("[Info]: Accessing ILP REST Service");

            Order[] orderList = client.orders(date);
//            System.out.println(orderList.length);
//            System.out.println(orderList[0].getPriceTotalInPence());


            Restaurant[] restaurantList = client.restaurants();
//            System.out.println(restaurantList.length);
//            System.out.println(restaurantList.getClass());

        }
    }

    /**
     * Validates the cli input arguments that comprises a valid date in YYYY-MM-DD, an url address and any word
     * @param args cli input arguments
     * @return true if arguments are valid and false if invalid
     */
    public static boolean argsValidator(String[] args) {

        if (args == null) {
            System.err.println("[Error]: Invalid input of null, please input date, url and any word to initialise the " +
                    "random-number generator.");
            return false;
        } else if (args.length != 3) {
            System.err.println("[Error]: Invalid number of inputs in arguments, please input date, url and any word to initialise the " +
                    "random-number generator.");
            return false;
        }
        // parsing arguments into date and url
        String date = args[0];
        String url = args[1];

        try {
            // Verify the date format
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(date, format);

        } catch (DateTimeParseException e) {
            System.err.println("[Error]: Please provide a valid date in YYYY-MM-DD format.");
            return false;
        }
        try {
            // Verify if url malformed and has wrong URI syntax
            URL obj = new URL(url);
            obj.toURI();
            return true;
        } catch (MalformedURLException e) {
            System.err.println("[Error]: Please provide a URL not malformed.");
            return false;
        } catch (URISyntaxException e) {
            System.err.println("[Error]: Please provide a URL with a valid URI Syntax.");
            return false;
        }
    }
}
