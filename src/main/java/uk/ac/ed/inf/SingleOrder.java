package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.Order;

/**
 * Represents an order with details such as order number, status, validation code, and cost.
 *
 *  @author B209981
 */
class SingleOrder {
    private final String orderNo;             // Unique identifier for the order
    private final String orderStatus;         // Current status of the order
    private final String orderValidationCode; // Code indicating the result of order validation
    private final int costInPence;            // Total cost of the order in pence

    /**
     * Constructs a new Order instance based on another Order object.
     *
     * @param order The order object to copy data from.
     */
    public SingleOrder(Order order) {
        this.orderNo = order.getOrderNo();
        this.orderStatus = String.valueOf(order.getOrderStatus());
        this.orderValidationCode = String.valueOf(order.getOrderValidationCode());
        this.costInPence = order.getPriceTotalInPence();
    }

}

