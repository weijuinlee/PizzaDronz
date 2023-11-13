package uk.ac.ed.inf;

import static org.junit.Assert.*;
import org.junit.Test;

public class AppTest
{
    @Test
    public void validateArgumentsNullTest() {
        assertFalse(App.argsValidator(null));
    }

    @Test
    public void lessThanNumberOfArgumentsRequiredTest() {
        String[] args = new String[2];
        args[0] = "2023-09-09";
        args[1] = "https://ilp-rest.azurewebsites.net";
        assertFalse(App.argsValidator(args));
    }

    @Test
    public void moreThanNumberOfArgumentsRequiredTest() {
        String[] args = new String[4];
        args[0] = "2023-09-09";
        args[1] = "https://ilp-rest.azurewebsites.net";
        args[2] = "cabbage";
        args[3] = "cabbage";
        assertFalse(App.argsValidator(args));
    }

    @Test
    public void exactNumberOfArgumentsRequiredTest() {
        String[] args = new String[3];
        args[0] = "2023-09-09";
        args[1] = "https://ilp-rest.azurewebsites.net";
        args[2] = "cabbage";
        assertTrue(App.argsValidator(args));
    }

    @Test
    public void invalidDateTest() {
        String[] args = new String[3];
        args[0] = "2023-13-12";
        args[1] = "https://ilp-rest.azurewebsites.net";
        args[2] = "cabbage";
        assertFalse(App.argsValidator(args));
    }

    @Test
    public void invalidDateFormatTest() {
        String[] args = new String[3];
        args[0] = "2023/09/09";
        args[1] = "https://ilp-rest.azurewebsites.net";
        args[2] = "cabbage";
        assertFalse(App.argsValidator(args));
    }

    @Test
    public void invalidMalformedUrlTest() {
        String[] args = new String[3];
        args[0] = "2023-11-21";
        args[1] = "htts://ilp-rest.azurewebsites.net";
        args[2] = "cabbage";
        assertFalse(App.argsValidator(args));
    }

    @Test
    public void invalidUriSyntaxTest() {
        String[] args = new String[3];
        args[0] = "2023-11-21";
        args[1] = "https://ilp-rest.azurewebsites.net w3r4fregf3e";
        args[2] = "cabbage";
        assertFalse(App.argsValidator(args));
    }




}