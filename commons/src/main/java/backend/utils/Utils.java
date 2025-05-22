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

    public static void validatePagination(Integer page, Integer size, String orderBy) {
        if (isNotPositive(page)) {
            throw new UtilException(UtilException.PAGE_NUMBER_INVALID);
        }

        if (isNotPositive(size)) {
            throw new UtilException(UtilException.SIZE_NUMBER_INVALID);
        }

        if (isBlank(orderBy)) {
            throw new UtilException(UtilException.SORT_NAME_INVALID);
        }
    }
}
