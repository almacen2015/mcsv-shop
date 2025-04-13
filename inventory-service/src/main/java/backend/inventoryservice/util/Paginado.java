package backend.inventoryservice.util;

public record Paginado(Integer page, Integer size, String orderBy) {
}
