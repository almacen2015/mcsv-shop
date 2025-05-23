package backend.pageable;

public record Paginado(Integer page, Integer size, String orderBy) {
}
