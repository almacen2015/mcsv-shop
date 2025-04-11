package backend.clientservice.exceptions;

public class ClienteException extends RuntimeException {
    public static final String ID_INVALID = "Invalid ID";
    public static final String INVALID_NAME = "Name cannot be null or empty";
    public static final String INVALID_LAST_NAME = "Last name cannot be null or empty";
    public static final String INVALID_DOCUMENT_NUMBER = "Document number cannot be null or empty";
    public static final String INVALID_DOCUMENT_TYPE = "Invalid document type";
    public static final String INVALID_BIRTH_DATE = "Invalid birth date";
    public static final String CLIENT_NOT_FOUND = "Client not found";
    public static final String DOCUMENT_NUMBER_EXISTS = "The document number exists";

    public ClienteException(String message) {
        super(message);
    }
}
