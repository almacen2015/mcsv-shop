package backend.saleservice.models.dtos.response;

public record DetailSaleResponseDto(Integer productId,
                                    Integer quantity,
                                    Double unitPrice,
                                    Double subTotal) {
}
