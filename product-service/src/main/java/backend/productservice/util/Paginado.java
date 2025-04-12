package backend.productservice.util;

public record Paginado(Integer page, Integer size, String orderBy) {
}
