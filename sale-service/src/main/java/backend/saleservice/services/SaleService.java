package backend.saleservice.services;

import backend.pageable.Paginado;
import backend.saleservice.models.dtos.request.SaleRequestDto;
import backend.saleservice.models.dtos.response.SaleResponseDto;
import org.springframework.data.domain.Page;

public interface SaleService {
    Page<SaleResponseDto> getAll(Integer page, Integer size, String orderBy);

    SaleResponseDto add(SaleRequestDto requestDto);

    Page<SaleResponseDto> getSalesByClient(Integer clientId, Paginado paginado);
}
