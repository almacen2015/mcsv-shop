package backend.productoservice.exceptions.advice;

import backend.productoservice.exceptions.ProductoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Set;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    private static final Set<String> ERRORES_VALIDACION = Set.of(
            ProductoException.PRODUCT_NAME_EMPTY,
            ProductoException.PRODUCT_DESCRIPTION_EMPTY,
            ProductoException.PRODUCT_PRICE_INVALID,
            ProductoException.AMOUNT_INVALID,
            ProductoException.INVALID_STOCK,
            ProductoException.PRODUCT_NOT_FOUND,
            ProductoException.PAGE_NUMBER_INVALID,
            ProductoException.SIZE_NUMBER_INVALID,
            ProductoException.SORT_NAME_INVALID
    );

    @ExceptionHandler(ProductoException.class)
    public ResponseEntity<?> handleProductoException(ProductoException e) {
        log.error(e.getMessage(), e);
        HttpStatus status = ERRORES_VALIDACION.contains(e.getMessage()) ?
                HttpStatus.BAD_REQUEST :
                HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(status).body(e.getMessage());
    }
}
