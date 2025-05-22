package backend.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageableUtils {

    public static PageRequest constructPageable(Integer page, Integer size, String orderBy) {
        return PageRequest.of(page - 1, size, Sort.by(orderBy).descending());
    }
}
