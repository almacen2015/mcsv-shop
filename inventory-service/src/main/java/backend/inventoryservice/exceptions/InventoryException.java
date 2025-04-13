package backend.inventoryservice.exceptions;

public class InventoryException extends RuntimeException {
    public static final String INVALID_AMOUNT = "The amount is invalid";
    public static final String INVALID_TYPE_MOVEMENT = "The type of movement is invalid";
    public static final String MOVEMENT_WITHOUT_STOCK = "There is no stock for the product";
    public static final String INVALID_PRODUCT = "The product is invalid";
    public static final String INVALID_ID = "The id is invalid";
    public static final String PAGE_NUMBER_INVALID = "Page number is invalid";
    public static final String SIZE_NUMBER_INVALID = "Size number is invalid";
    public static final String SORT_NAME_INVALID = "Sort name is invalid";

    public InventoryException(String message) {
        super(message);
    }
}
