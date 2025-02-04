package com.example.Rider.exception;

import com.example.Rider.dto.response.common.Response;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        BindingResult bindingResult = exception.getBindingResult();
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Response<Map<String, String>> response = new Response<>(
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                false,
                "Enter valid Information!",
                errors
        );

        handleExceptionLog(exception);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public final Response<String> handleUserNotFoundException(RecordNotFoundException ex) {
        String message = ex.getLocalizedMessage();
        handleExceptionLog(ex);

        return new Response<>(getStatus(HttpStatus.NOT_FOUND), false, message, message);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public final Response<String> handleUserNotFoundException(UsernameNotFoundException exception) {
        String message = exception.getLocalizedMessage();
        handleExceptionLog(exception);

        return new Response<>(getStatus(HttpStatus.NOT_FOUND), false, message, message);
    }

    @ExceptionHandler(MissingHeaderInfoException.class)
    public final Response<String> handleInvalidTraceIdException(MissingHeaderInfoException ex) {
        String message = ex.getLocalizedMessage();
        handleExceptionLog(ex);

        return new Response<>(getStatus(HttpStatus.UNAUTHORIZED), false, message, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final Response<String> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getMessage();
        handleExceptionLog(ex);

        return new Response<>(getStatus(HttpStatus.BAD_REQUEST), false, message, message);
    }

    @ExceptionHandler(CustomDataIntegrityViolationException.class)
    public final Response<String> dataIntegrityViolationException(CustomDataIntegrityViolationException ex) {
        String message = ex.getLocalizedMessage();
        handleExceptionLog(ex);

        return new Response<>(getStatus(HttpStatus.NOT_FOUND), false, message, message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final Response<String> handleAccessDeniedException(AccessDeniedException exception) {
        String message = exception.getLocalizedMessage();
        handleExceptionLog(exception);

        return new Response<>(getStatus(HttpStatus.FORBIDDEN),
                false,
                "You are not authorize to access this!",
                "You are not authorized to access this!"
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final Response<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        String message = exception.getLocalizedMessage();
        handleExceptionLog(exception);

        return new Response<>(getStatus(HttpStatus.BAD_REQUEST), false, message, message);
    }

    @ExceptionHandler(IllegalHeaderException.class)
    public final Response<String> handleIllegalHeaderException(IllegalHeaderException ex) {
        String message = ex.getLocalizedMessage();
        handleExceptionLog(ex);

        return new Response<>(getStatus(HttpStatus.UNAUTHORIZED), false, message, message);
    }

    @ExceptionHandler(RuntimeException.class)
    public final Response<String> handleRuntimeException(RuntimeException exception) {
        String message = exception.getLocalizedMessage();
        handleExceptionLog(exception);

        return new Response<>(getStatus(HttpStatus.INTERNAL_SERVER_ERROR), false, message, message);
    }

    private String getStatus(HttpStatus status) {
        return String.valueOf(status.value());
    }

    private void handleExceptionLog(Exception exception) {
        boolean isLogPrint = false;
        String className = exception.getClass().getName();
        for (StackTraceElement stackTrace : exception.getStackTrace()) {
            if (stackTrace.getClassName().startsWith("com.camera")) {
                LOGGER.error(
                        "{}: Occurred in: {}, Method: {}, line: {}, Message: {}",
                        className,
                        stackTrace.getClassName(),
                        stackTrace.getMethodName(),
                        stackTrace.getLineNumber(),
                        exception.getMessage()
                );
                isLogPrint = true;
                break;
            }
        }
        if (!isLogPrint) {
            StackTraceElement stackTrace = exception.getStackTrace()[0];
            LOGGER.error(
                    "{}: Occurred in: {}, Method: {}, line: {}, Message: {}",
                    className,
                    stackTrace.getClassName(),
                    stackTrace.getMethodName(),
                    stackTrace.getLineNumber(),
                    exception.getMessage()
            );
        }
    }
}