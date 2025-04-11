package backend.inventoryservice.exceptions;

public class MovimientoException extends RuntimeException {
    public static final String INVALID_AMOUNT = "The amount is invalid";
    public static final String INVALID_TYPE_MOVEMENT = "The type of movement is invalid";
    public static final String MOVEMENT_WITHOUT_STOCK = "There is no stock for the product";
    public static final String INVALID_PRODUCT = "The product is invalid";
    public static final String INVALID_ID = "The id is invalid";

    public MovimientoException(String message) {
        super(message);
    }
}
