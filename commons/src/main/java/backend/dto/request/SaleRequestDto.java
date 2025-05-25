package backend.dto.request;

import java.util.List;

public record SaleRequestDto(Integer clientId,
                             List<DetailSaleRequestDto> details) {
}
