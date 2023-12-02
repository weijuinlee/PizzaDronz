package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.time.LocalDate;

/**
 * Client to perform HTTP GET requests and deserialize responses.
 *
 * @author B209981
 */
public record Client(String url) {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();

    /**
     * Checks if the ILP REST Service is alive.
     *
     * @return true if the service responds, false otherwise.
     */
    public boolean isAlive() {
        String endpoint = "isAlive";
        String response = sendGetRequest(endpoint);

        if (response == null) {
            return false;
        }

        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, boolean.class);
    }

    /**
     * Retrieves orders for a specific date.
     *
     * @param date The date for which to retrieve orders.
     * @return Array of orders for the given date, or null if the request fails.
     */
    public Order[] orders(String date) {
        String endpoint = "orders/" + date;
        String response = sendGetRequest(endpoint);

        if (response == null) {
            return null;
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .create();
        return gson.fromJson(response, Order[].class);
    }

    /**
     * Retrieves the list of restaurants.
     *
     * @return Array of restaurants, or null if the request fails.
     */
    public Restaurant[] restaurants() {
        String endpoint = "restaurants";
        return sendGetRequest(endpoint, Restaurant[].class);
    }

    /**
     * Sends a GET request to the specified endpoint and returns the response as a String.
     *
     * @param endpoint The endpoint to which the GET request is sent.
     * @return The response body as a String, or null in case of an error.
     */
    private String sendGetRequest(String endpoint) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.url + endpoint))
                .build();

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            System.err.println("[Error]: Unable to make a GET request to " + endpoint + " - " + e.getMessage() + ".");
            return null;
        }
    }

    /**
     * Sends a GET request to the specified endpoint and deserializes the JSON response into the specified type.
     *
     * @param endpoint The endpoint to which the GET request is sent.
     * @param type The class of the type to deserialize into.
     * @return The deserialized object, or null in case of an error.
     */
    private <T> T sendGetRequest(String endpoint, Class<T> type) {
        String response = sendGetRequest(endpoint);

        if (response == null) {
            return null;
        }

        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, type);
    }

    /**
     * Retrieves the central area information.
     *
     * @return NamedRegion object representing the central area, or null if the request fails.
     */
    public NamedRegion centralArea() {
        return sendGetRequest("centralArea", NamedRegion.class);
    }

    /**
     * Retrieves the no-fly zones.
     *
     * @return Array of NamedRegion objects representing no-fly zones, or null if the request fails.
     */
    public NamedRegion[] noFlyZones() {
        return sendGetRequest("noFlyZones", NamedRegion[].class);
    }

}
