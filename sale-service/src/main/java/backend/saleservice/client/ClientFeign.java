package backend.saleservice.client;

import backend.saleservice.models.dtos.response.ClientResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "mcsv-clientes", url = "${client.service.url}")
public interface ClientFeign {
    @GetMapping("/{id}")
    ClientResponseDTO getClient(@PathVariable Long id);
}
