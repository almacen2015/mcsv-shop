package backend.saleservice.exceptions.advice;

import backend.saleservice.exceptions.VentaException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    public static Set<String> errors = Set.of(
            VentaException.PRODUCT_ID_INVALID,
            VentaException.QUANTITY_INVALID,
            VentaException.CLIENT_ID_INVALID,
            VentaException.DETAILS_INVALID,
            VentaException.PRODUCT_NOT_FOUND,
            VentaException.PRODUCT_REPEATED,
            VentaException.PAGE_NUMBER_INVALID,
            VentaException.SIZE_NUMBER_INVALID,
            VentaException.SORT_NAME_INVALID,
            VentaException.QUANTITY_GREATER_THAN_STOCK
    );

    @ExceptionHandler(VentaException.class)
    public ResponseEntity<String> handleClienteException(VentaException e) {
        log.error(e.getMessage(), e);

        HttpStatus status = errors.contains(e.getMessage()) ?
                HttpStatus.BAD_REQUEST :
                HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(status).body(e.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignException(FeignException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
