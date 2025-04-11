package backend.saleservice.client;

import backend.saleservice.models.dtos.request.MovimientoDtoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "mcsv-movimientos", url = "${movement.service.url}")
public interface MovementClient {
    @PostMapping
    MovimientoDtoRequest createMovimientoDto(MovimientoDtoRequest movimientoDtoRequest);
}
