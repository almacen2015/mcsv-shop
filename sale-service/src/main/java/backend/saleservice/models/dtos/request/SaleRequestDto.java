package backend.saleservice.models.dtos.request;

import java.util.List;

public record SaleRequestDto(Integer clientId,
                             List<DetailSaleRequestDto> details) {
}
