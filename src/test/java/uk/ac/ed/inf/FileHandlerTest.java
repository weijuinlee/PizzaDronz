package uk.ac.ed.inf;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.*;

/**
 * Integration test for File handling
 *
 *  @author B209981
 */
public class FileHandlerTest {
    private final Path resultFilesPath = Paths.get("resultfiles/");

    @Before
    public void setUp() throws IOException {
        // Clean up before each test
        Files.deleteIfExists(resultFilesPath);
    }

    @After
    public void tearDown() throws IOException {
        // Clean up after each test
        Files.deleteIfExists(resultFilesPath);
    }

    @Test
    public void whenDirectoryDoesNotExistThenItIsCreated() {
        // Act
        FileHandler.resultFiles();

        // Assert
        assertTrue("Directory should be created if it does not exist", Files.exists(resultFilesPath));
    }

    @Test
    public void whenDirectoryExistsThenOperationIsSuccessful() throws IOException {
        // Arrange
        Files.createDirectory(resultFilesPath);

        // Act
        FileHandler.resultFiles();

        // Assert
        assertTrue("Operation should be successful if the directory already exists", Files.exists(resultFilesPath));
    }
}
