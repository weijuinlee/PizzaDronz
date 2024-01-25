# PizzaDronz
Coursework for Informatics Large Practical Project and Software Testing 

## Pizza Delivery Service using Drones
PizzaDronz is a drone-based delivery service designed to transport pizzas from various restaurants directly to the top of Appleton Tower. The operational characteristics of the service include:

- The drone is capable of either hover or direct movements spanning 0.00015 degrees along one of the 16 compass directions. Notably, the drone must hover for a single move when picking up an order at a restaurant and delivering it at Appleton Tower.

- The service operates based on specific dates. It utilizes the base URL of a REST server to access essential data, which includes details about restaurants, geographical constraints affecting drone flight paths, and the list of orders for the specified date. Each order includes details such as the order date, customer information, card details, the total cost, and the names of the pizzas.

- The system is designed to validate all the orders for the given date. It selects which orders are valid. Additionally, it generates the drone's flight path for the entire day, considering any restrictions on movement.

## Use
To run this program, execute the following command in the directory where the repository is cloned:

```bash
java -jar target/PizzaDronz-1.0-SNAPSHOT.jar 2023-11-11 https://ilp-rest.azurewebsites.net randow_word
```

## Results
Executing the command as detailed above will generate three files:

- deliveries-2023-11-11.json: This file contains a JSON array of the orders for the specified date, detailing the outcomes of each (e.g., delivered, valid but not delivered, invalid with reasons).
  
- flightpath-2023-11-11.json: This file contains a JSON array that describes each move of the drone on the specified date.
  
- drone-2023-01-25.geojson: This file provides a GeoJSON visualization of the drone's flight path, which can be rendered on platforms like geojson.io.
