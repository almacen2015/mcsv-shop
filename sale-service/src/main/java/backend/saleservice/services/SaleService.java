package backend.saleservice.services;

import backend.pageable.Paginado;
import backend.dto.request.SaleDtoRequest;
import backend.dto.response.SaleDtoResponse;
import org.springframework.data.domain.Page;

public interface SaleService {
    Page<SaleDtoResponse> getAll(Integer page, Integer size, String orderBy);

    SaleDtoResponse add(SaleDtoRequest requestDto);

    Page<SaleDtoResponse> getSalesByClient(Integer clientId, Paginado paginado);
}
