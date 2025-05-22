package backend.productservice.exceptions.advice;

import backend.exception.UtilException;
import backend.productservice.exceptions.ProductException;
import backend.utils.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Set;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    private static final Set<String> VALIDATION_ERRORS = Set.of(
            ProductException.PRODUCT_NAME_EMPTY,
            ProductException.PRODUCT_DESCRIPTION_EMPTY,
            ProductException.PRODUCT_PRICE_INVALID,
            ProductException.AMOUNT_INVALID,
            ProductException.INVALID_STOCK,
            ProductException.PRODUCT_NOT_FOUND,
            UtilException.PAGE_NUMBER_INVALID,
            UtilException.SIZE_NUMBER_INVALID,
            UtilException.SORT_NAME_INVALID,
            UtilException.INVALID_ID_PRODUCT
    );

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<ErrorResponse> handleProductoException(ProductException e, HttpServletRequest request) {
        return handleCustomException(e, request);
    }

    @ExceptionHandler(UtilException.class)
    public ResponseEntity<ErrorResponse> handleUtilException(UtilException e, HttpServletRequest request) {
        return handleCustomException(e, request);
    }

    private ResponseEntity<ErrorResponse> handleCustomException(RuntimeException e, HttpServletRequest request) {
        final String method = request.getMethod();
        final String uri = request.getRequestURI();
        final String queryString = request.getQueryString();

        log.error("Error en endpoint {} {}, queryString={}, mensaje={}, stacktrace=",
                method, uri, queryString, e.getMessage(), e);

        HttpStatus status = VALIDATION_ERRORS.contains(e.getMessage())
                ? HttpStatus.BAD_REQUEST
                : HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse response = new ErrorResponse(status.value(), e.getMessage());

        return ResponseEntity.status(status).body(response);
    }
}
