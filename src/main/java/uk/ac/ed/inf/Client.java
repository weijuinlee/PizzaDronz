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
 * Client to perform get request and deserialize response
 * @param url is the base url
 * @author B209981
 */
public record Client(String url) {

    /**
     * Check is ILP REST Service is alive
     * Return the euclidean distance between the positions
     */
    public boolean isAlive() {

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.url + "isAlive")).build();
        HttpResponse<String> response;

        try {

            // Send the request and store the ILP REST Service response
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Use a GsonBuilder to deserialize the json returned
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(response.body(), boolean.class);

        } catch (Exception e) {
            System.err.println("[Error]: ILP REST Service is down");
            return false;
        }
    }

    public Order[] orders(String date) {

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.url + "orders/" + date)).build();
        HttpResponse<String> response;

        try {

            // Send the request and store the ILP REST Service response
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Use a GsonBuilder to deserialize the json returned
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create();
            return gson.fromJson(response.body(), Order[].class);

        } catch (Exception e) {
            System.err.println("[Error]: Unable to make a get request for orders");
            return null;
        }
    }

    public Restaurant[] restaurants() {

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.url + "restaurants")).build();
        HttpResponse<String> response;

        try {

            // Send the restaurant request and store the ILP REST Service response
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Use a GsonBuilder to deserialize the json returned
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(response.body(), Restaurant[].class);

        } catch (Exception e) {
            System.err.println("[Error]: Unable to make a get request for restaurants");
            return null;
        }
    }

    public NamedRegion centralArea() {

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.url + "/centralArea")).build();
        HttpResponse<String> response;

        try {

            // Send the restaurant request and store the ILP REST Service response
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Use a GsonBuilder to deserialize the json returned
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(response.body(), NamedRegion.class);

        } catch (Exception e) {
            System.err.println("[Error]: Unable to make a get request for central area");
            return null;
        }
    }

    public NamedRegion[] noFlyZones() {

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.url + "/noFlyZones")).build();
        HttpResponse<String> response;

        try {

            // Send the restaurant request and store the ILP REST Service response
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Use a GsonBuilder to deserialize the json returned
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(response.body(),NamedRegion[].class);

        } catch (Exception e) {
            System.err.println("[Error]: Unable to make a get request for no fly zones");
            return null;
        }
    }
}