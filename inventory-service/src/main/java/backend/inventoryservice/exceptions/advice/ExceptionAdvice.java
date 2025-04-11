package backend.inventoryservice.exceptions.advice;

import backend.inventoryservice.exceptions.MovimientoException;
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

    private static final Set<String> ERRORES_VALIDACION = Set.of(
            MovimientoException.INVALID_AMOUNT,
            MovimientoException.INVALID_TYPE_MOVEMENT,
            MovimientoException.INVALID_PRODUCT,
            MovimientoException.INVALID_ID
    );

    @ExceptionHandler(MovimientoException.class)
    public ResponseEntity<?> exceptionHandler(MovimientoException e) {
        log.error(e.getMessage());
        HttpStatus status = ERRORES_VALIDACION.contains(e.getMessage()) ?
                HttpStatus.BAD_REQUEST :
                HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(e.getMessage(), status);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignException(FeignException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
