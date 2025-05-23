package backend.utils;

import backend.exception.UtilException;

public class Utils {

    public static boolean isNotPositive(Integer value) {
        return value == null || value <= 0;
    }

    public static void validateIdProduct(Integer id) {
        if (isNotPositive(id)) {
            throw new UtilException(UtilException.INVALID_ID_PRODUCT);
        }
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static boolean isNotValidDni(String value) {
        if (value.length() != 8) {
            return true;
        }

        return value.matches(".*[a-zA-Z]+.*");
    }
}
