package uk.ac.ed.inf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler {
    public static void resultFiles(){

        Path resultfiles = Paths.get("resultfiles/");

        try {
            Files.createDirectory(resultfiles);
            System.out.println("[Info]: resultfiles directory created");
        } catch (IOException ignored) {
            System.out.println("[Info]: resultfiles directory exist");
        }
    }
}