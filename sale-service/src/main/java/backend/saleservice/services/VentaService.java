package backend.saleservice.services;

import backend.saleservice.models.dtos.request.VentaRequestDto;
import backend.saleservice.models.dtos.response.VentaResponseDto;
import backend.saleservice.util.Paginado;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VentaService {
    List<VentaResponseDto> getAll();

    VentaResponseDto add(VentaRequestDto requestDto);

    Page<VentaResponseDto> getSalesByClient(Integer clientId, Paginado paginado);
}
