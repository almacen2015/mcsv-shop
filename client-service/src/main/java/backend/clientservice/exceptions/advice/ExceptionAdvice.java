package backend.clientservice.exceptions.advice;

import backend.clientservice.exceptions.ClienteException;
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
            ClienteException.ID_INVALID,
            ClienteException.INVALID_LAST_NAME,
            ClienteException.INVALID_NAME,
            ClienteException.INVALID_BIRTH_DATE,
            ClienteException.INVALID_DOCUMENT_NUMBER,
            ClienteException.INVALID_DOCUMENT_TYPE,
            ClienteException.CLIENT_NOT_FOUND,
            ClienteException.DOCUMENT_NUMBER_EXISTS
    );

    @ExceptionHandler(ClienteException.class)
    public ResponseEntity<String> handleClienteException(ClienteException e) {
        log.error(e.getMessage(), e);

        HttpStatus status = ERRORES_VALIDACION.contains(e.getMessage()) ?
                HttpStatus.BAD_REQUEST :
                HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(status).body(e.getMessage());
    }
}

