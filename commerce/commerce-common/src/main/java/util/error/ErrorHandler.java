package util.error;

import dto.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import util.exception.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("Validation error: {}", e.getMessage(), e);

        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        return new ApiError(
                errors,
                "Validation error",
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        log.error("Validation error: {}", e.getMessage(), e);

        List<String> errors = e.getMessage().lines().toList();

        return new ApiError(
                errors,
                "Validation error",
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleHttpMessageNotFoundException(final NotFoundException e) {
        log.error("Error: {}", e.getMessage(), e);

        List<String> errors = e.getMessage().lines().toList();

        return new ApiError(
                errors,
                "Error",
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleHttpMessageValidationException(final ValidationException e) {
        log.error("Validation error: {}", e.getMessage(), e);

        List<String> errors = e.getMessage().lines().toList();

        return new ApiError(
                errors,
                "Validation error",
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNoSpecifiedProductInWarehouseException(final NoSpecifiedProductInWarehouseException e) {
        log.error("Error: {}", e.getMessage(), e);

        List<String> errors = e.getMessage().lines().toList();

        return new ApiError(
                errors,
                "Product not found",
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleSpecifiedProductAlreadyInWarehouseException(final SpecifiedProductAlreadyInWarehouseException e) {
        log.error("Error: {}", e.getMessage(), e);

        List<String> errors = e.getMessage().lines().toList();

        return new ApiError(
                errors,
                "Product already in Warehouse",
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleProductInShoppingCartLowQuantityInWarehouse(final ProductInShoppingCartLowQuantityInWarehouse e) {
        log.error("Error: {}", e.getMessage(), e);

        List<String> errors = e.getMessage().lines().toList();

        return new ApiError(
                errors,
                "Product not enough in Warehouse",
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleHttpMessageCommonException(final Exception e) {
        log.error("Something went wrong: {}", e.getMessage(), e);

        List<String> errors = e.getMessage().lines().toList();

        return new ApiError(
                errors,
                "Error. Check your data",
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
    }
}
