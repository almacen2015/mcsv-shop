package backend.pageable;

import backend.exception.UtilException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static backend.utils.Utils.isBlank;
import static backend.utils.Utils.isNotPositive;

public class PageableUtils {

    public static PageRequest constructPageable(Paginado paginado) {
        return PageRequest.of(paginado.page() - 1, paginado.size(), Sort.by(paginado.orderBy()).descending());
    }

    public static void validatePagination(Paginado paginado) {
        if (isNotPositive(paginado.page())) {
            throw new UtilException(UtilException.PAGE_NUMBER_INVALID);
        }

        if (isNotPositive(paginado.size())) {
            throw new UtilException(UtilException.SIZE_NUMBER_INVALID);
        }

        if (isBlank(paginado.orderBy())) {
            throw new UtilException(UtilException.SORT_NAME_INVALID);
        }
    }
}
