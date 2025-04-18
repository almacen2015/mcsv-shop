package backend.saleservice.client;

import backend.saleservice.models.dtos.request.MovimientoDtoRequest;
import backend.saleservice.models.dtos.response.MovimientoDtoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "mcsv-movimientos", url = "${movement.service.url}")
public interface MovementClient {
    @PostMapping
    MovimientoDtoResponse createMovimientoDto(MovimientoDtoRequest movimientoDtoRequest);
}
