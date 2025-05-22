package backend.saleservice.client;

import backend.utils.ApiConstants;
import backend.saleservice.models.dtos.request.MovementRequestDto;
import backend.saleservice.models.dtos.response.MovementResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "${inventory.service.name}")
public interface InventoryClient {
    @PostMapping(ApiConstants.INVENTORY_BASE)
    MovementResponseDto createMovimientoDto(MovementRequestDto movimientoDtoRequest);
}
