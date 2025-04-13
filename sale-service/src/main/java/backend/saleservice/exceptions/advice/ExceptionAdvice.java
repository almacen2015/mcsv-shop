package backend.saleservice.exceptions.advice;

import backend.saleservice.exceptions.SaleException;
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
            SaleException.PRODUCT_ID_INVALID,
            SaleException.QUANTITY_INVALID,
            SaleException.CLIENT_ID_INVALID,
            SaleException.DETAILS_INVALID,
            SaleException.PRODUCT_NOT_FOUND,
            SaleException.PRODUCT_REPEATED,
            SaleException.PAGE_NUMBER_INVALID,
            SaleException.SIZE_NUMBER_INVALID,
            SaleException.SORT_NAME_INVALID,
            SaleException.QUANTITY_GREATER_THAN_STOCK
    );

    @ExceptionHandler(SaleException.class)
    public ResponseEntity<String> handleClienteException(SaleException e) {
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
