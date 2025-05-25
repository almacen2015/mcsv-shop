package backend.saleservice.client;

import backend.dto.response.ProductDtoResponse;
import backend.utils.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${product.service.name}")
public interface ProductClient {
    @GetMapping(ApiConstants.PRODUCT_BASE + "{id}")
    ProductDtoResponse getProduct(@PathVariable Integer id);
}
