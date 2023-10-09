package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static uk.ac.ed.inf.ilp.constant.OrderValidationCode.*;

public class OrderValidator implements OrderValidation {

    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {

        String dateString = String.valueOf(orderToValidate.getOrderDate());
        isCreditCardValid(orderToValidate);

        return orderToValidate;
    }

    /**
     * Checks if credit card is valid
     * @param orderToValidate Order to be checked.
     * Sets validation code accordingly
     */
    private static void isCreditCardValid(Order orderToValidate) {

        System.out.println(isCardNumberValid(orderToValidate.getCreditCardInformation().getCreditCardNumber()));

        if (!isCardNumberValid(orderToValidate.getCreditCardInformation().getCreditCardNumber())) {
            orderToValidate.setOrderValidationCode(CARD_NUMBER_INVALID);
        } else if (!isCVVValid(orderToValidate.getCreditCardInformation().getCvv())) {
            orderToValidate.setOrderValidationCode(CVV_INVALID);
        } else if (!isExpiryDateValid(orderToValidate.getCreditCardInformation().getCreditCardExpiry())) {
            orderToValidate.setOrderValidationCode(EXPIRY_DATE_INVALID);
        } else
            orderToValidate.setOrderValidationCode(NO_ERROR);
    }

    /**
     * Checks if cvv number is valid
     * @param expiryDate String to be checked.
     * @return True if string is numeric, false otherwise.
     */
    private static boolean isExpiryDateValid(String expiryDate) {

        // Define the expected credit card date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");
        dateFormat.setLenient(false);

        // Get the current date
        Date currentDate = new Date();

        try {
            // Parse the expiry date
            Date parsedExpiryDate = dateFormat.parse(expiryDate);

            // Check if the parsed date is not in the past
            return !parsedExpiryDate.before(currentDate);

        } catch (ParseException e) {
            // If parsing fails, the date format is invalid
            return false;
        }
    }

    /**
     * Checks if cvv number is valid
     * @param CVV String to be checked.
     * @return True if string is numeric, false otherwise.
     */
    private static boolean isCVVValid(String CVV) {

        // Clean the input by removing non-digit characters
        String cleanedCVV = CVV.replaceAll("\\D", "");

        // Check if the cleaned number has exactly 3 digits
        return cleanedCVV.length() == 3;
    }

    /**
     * Checks if credit card number is valid
     * @param creditCardNumber String to be checked.
     * @return True if string is numeric, false otherwise.
     */
    private static boolean isCardNumberValid(String creditCardNumber) {

        // Clean the input by removing non-digit characters
        String cleanedNumber = creditCardNumber.replaceAll("\\D", "");

        // Check if the cleaned number has exactly 16 digits
        if (cleanedNumber.length() == 16) {

            // Check if the cleaned number is a valid credit card number using the Luhn algorithm
            return isValidLuhn(cleanedNumber);

        } else {
            return false;
        }
    }

    /**
     * Luhn algorithm for validating credit card number. The implementation is taken from the source below.
     * https://www.geeksforgeeks.org/luhn-algorithm/
     * @return True if valid, otherwise false.
     */
    private static boolean isValidLuhn(String number) {

        int nDigits = number.length();

        int nSum = 0;
        boolean isSecond = false;
        for (int i = nDigits - 1; i >= 0; i--)
        {

            int d = number.charAt(i) - '0';

            if (isSecond)
                d = d * 2;

            // We add two digits to handle
            // cases that make two digits
            // after doubling
            nSum += d / 10;
            nSum += d % 10;

            isSecond = !isSecond;
        }
        return (nSum % 10 == 0);
    }


}

