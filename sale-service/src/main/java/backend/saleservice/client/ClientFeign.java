package backend.saleservice.client;

import backend.dto.response.ClientDtoResponse;
import backend.utils.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${client.service.name}")
public interface ClientFeign {
    @GetMapping(ApiConstants.CLIENT_BASE + "{id}")
    ClientDtoResponse getClient(@PathVariable Long id);
}
