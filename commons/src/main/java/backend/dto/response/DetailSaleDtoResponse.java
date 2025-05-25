package backend.dto.response;

public record DetailSaleDtoResponse(Integer productId,
                                    Integer quantity,
                                    Double unitPrice,
                                    Double subTotal) {
}
