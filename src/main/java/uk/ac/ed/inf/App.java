package uk.ac.ed.inf;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Main class for the pizzaDronz, this is the entry point of the application.
 */
public class App
{
    public static void main( String[] args )
    {

        if (!argsValidator(args)) {
            return;
        }

        // parsing arguments into date and url
        String date = args[0];
        String url = args[1];

        System.out.println(date);
        System.out.println(url);
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
