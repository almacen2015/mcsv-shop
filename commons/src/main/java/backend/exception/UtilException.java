package backend.exception;

public class UtilException extends RuntimeException {
    public static final String INVALID_ID_PRODUCT = "Id product invalid";
    public static final String PAGE_NUMBER_INVALID = "Page number is invalid";
    public static final String SIZE_NUMBER_INVALID = "Size number is invalid";
    public static final String SORT_NAME_INVALID = "Sort name is invalid";

    public UtilException(String message) {
        super(message);
    }
}
