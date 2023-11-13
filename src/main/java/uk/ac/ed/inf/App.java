package uk.ac.ed.inf;

/**
 * Main class for the pizzaDronz, this is the entry point of the application.
 */
public class App
{
    public static void main( String[] args )
    {

        if (args.length < 2) {
            System.err.println("Invalid number of arguments in input, please input date and then url.");
        } else {

            // parsing arguments into date and url
            String date = args[0];
            String url = args[1];

            System.out.println(date);
            System.out.println(url);
        }
    }
}
