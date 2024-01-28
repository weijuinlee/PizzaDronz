package uk.ac.ed.inf;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

public class AppPerformanceTest {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(120); // Set maximum execution time

    @Test
    public void testMainPerformance() {

        // Create mock arguments for the main method
        String[] args = {"2023-11-11", "https://ilp-rest.azurewebsites.net", "anyWord"};

        // Measure the execution time of the main method
        long startTime = System.currentTimeMillis();
        App.main(args);
        long endTime = System.currentTimeMillis();

        // Assert that the main method completes within a reasonable timeframe
        long executionTime = endTime - startTime;
        assert executionTime < 1200000; //less than 2 minutes
    }
}
