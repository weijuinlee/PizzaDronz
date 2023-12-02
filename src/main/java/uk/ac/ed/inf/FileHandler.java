package uk.ac.ed.inf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles file operations such as creating directories for result files.
 */
public class FileHandler {

    /**
     * Creates a directory for storing result files if it does not already exist.
     */
    public static void resultFiles() {
        Path resultFilesPath = Paths.get("resultfiles/");

        try {
            // Attempt to create the directory
            Files.createDirectory(resultFilesPath);
            System.out.println("[Info]: resultfiles directory created.");
        } catch (IOException e) {
            // Check if the directory already exists
            if (Files.exists(resultFilesPath)) {
                System.out.println("[Info]: resultfiles directory already exists.");
            } else {
                // Log other IO exceptions
                System.err.println("[Error]: Unable to create resultfiles directory - " + e.getMessage() + ".");
            }
        }
    }
}
