package backend.saleservice.client;

import backend.saleservice.models.dtos.response.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${product.service.name}")
public interface ProductClient {
    @GetMapping("/{id}")
    ProductResponseDto getProduct(@PathVariable Integer id);
}
