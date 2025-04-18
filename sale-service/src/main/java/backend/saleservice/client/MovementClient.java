package backend.saleservice.client;

import backend.saleservice.models.dtos.request.MovementRequestDto;
import backend.saleservice.models.dtos.response.MovementResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "mcsv-movimientos", url = "${movement.service.url}")
public interface MovementClient {
    @PostMapping
    MovementResponseDto createMovimientoDto(MovementRequestDto movimientoDtoRequest);
}
