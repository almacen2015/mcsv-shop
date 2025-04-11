package backend.saleservice.client;

import backend.saleservice.models.dtos.response.ProductoDtoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "mcsv-productos", url = "${product.service.url}")
public interface ProductClient {
    @GetMapping("/{id}")
    ProductoDtoResponse getProduct(@PathVariable Integer id);
}
