package backend.inventoryservice.client;

import backend.dto.response.ProductDtoResponse;
import backend.utils.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "${product.service.name}")
public interface ProductClient {

    @GetMapping(ApiConstants.PRODUCT_BASE + "{id}")
    ProductDtoResponse getProduct(@PathVariable Integer id);

    @PutMapping(ApiConstants.PRODUCT_BASE + "stock/{id}/{cantidad}/{tipoMovimiento}")
    void updateStock(@PathVariable Integer id, @PathVariable Integer cantidad, @PathVariable String tipoMovimiento);
}
