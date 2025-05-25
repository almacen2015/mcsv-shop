package backend.saleservice.client;

import backend.dto.response.MovementDtoResponse;
import backend.utils.ApiConstants;
import backend.dto.request.MovementDtoRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "${inventory.service.name}")
public interface InventoryClient {
    @PostMapping(ApiConstants.INVENTORY_BASE)
    MovementDtoResponse createMovimientoDto(MovementDtoRequest movimientoDtoRequest);
}
