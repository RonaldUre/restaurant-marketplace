package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.exception;

import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * Global HTTP exception mapper (agnóstico de módulos).
 * - Traduce validaciones, binding, protocolo HTTP, capa de datos y fallbacks
 * genéricos.
 * - No conoce excepciones de negocio de módulos específicos (esas van en sus
 * handlers locales).
 * - Formato de error unificado: ApiError (code, message, timestamp).
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // ---------- Validation & binding ----------

    /** Bean validation para @Valid @RequestBody. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String joined = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + safeMsg(fe.getDefaultMessage()))
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest()
                .body(ApiError.of("VALIDATION_ERROR", joined.isBlank() ? "Validation failed" : joined));
    }

    /** Validation para @RequestParam/@PathVariable con @Validated. */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        String joined = ex.getConstraintViolations()
                .stream()
                .map(cv -> (cv.getPropertyPath() != null ? cv.getPropertyPath() : "")
                        + ": " + safeMsg(cv.getMessage()))
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest()
                .body(ApiError.of("CONSTRAINT_VIOLATION", joined.isBlank() ? "Constraint violation" : joined));
    }

    /** Binding errors para @ModelAttribute o binding general. */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiError> handleBindException(BindException ex) {
        String joined = ex.getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + safeMsg(fe.getDefaultMessage()))
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest()
                .body(ApiError.of("BINDING_ERROR", joined.isBlank() ? "Binding failed" : joined));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest()
                .body(ApiError.of("MISSING_PARAMETER", "Missing request parameter: " + ex.getParameterName()));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiError> handleMissingHeader(MissingRequestHeaderException ex) {
        return ResponseEntity.badRequest()
                .body(ApiError.of("MISSING_HEADER", "Missing request header: " + ex.getHeaderName()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String msg = "Parameter '" + ex.getName() + "' has invalid value. Expected type: " + requiredType;
        return ResponseEntity.badRequest().body(ApiError.of("TYPE_MISMATCH", msg));
    }

    // ---------- HTTP protocol & parsing ----------

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(ApiError.of("MALFORMED_JSON", "Malformed JSON request"));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiError.of("UNSUPPORTED_MEDIA_TYPE", "Unsupported media type"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        HttpHeaders headers = new HttpHeaders();
        if (ex.getSupportedHttpMethods() != null) {
            for (HttpMethod m : ex.getSupportedHttpMethods()) {
                headers.add(HttpHeaders.ALLOW, m.name());
            }
        }
        return new ResponseEntity<>(
                ApiError.of("METHOD_NOT_ALLOWED", "Method not allowed"),
                headers,
                HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNoHandler(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("ENDPOINT_NOT_FOUND", "Endpoint not found"));
    }

    // ---------- Data layer ----------

    /** Violaciones genéricas de integridad que no atrapó un handler de módulo. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("DATA_INTEGRITY_VIOLATION", "Data integrity violation"));
    }

    // ---------- Domain fallbacks ----------

    /** Entradas inválidas a nivel de dominio ⇒ 400. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(ApiError.of("BAD_INPUT", safeMsg(ex.getMessage())));
    }

    /** Conflictos de estado de negocio ⇒ 409. */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("BUSINESS_CONFLICT", safeMsg(ex.getMessage())));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> handleOptimistic(ObjectOptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("CONCURRENT_MODIFICATION", "Concurrent modification"));
    }

    // ---------- Last resort ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        // Log detallado en server; respuesta estable al cliente.
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.of("UNEXPECTED_ERROR", "Unexpected error"));
    }

    // Conflictos de concurrencia (optimistic lock) => 409
    @ExceptionHandler({ OptimisticLockingFailureException.class, OptimisticLockException.class })
    public ResponseEntity<ApiError> handleOptimistic(Object ex) {
        String message = (ex instanceof Exception e && e.getMessage() != null)
                ? e.getMessage()
                : "Concurrent update detected. Please retry.";
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("INVENTORY_CONFLICT_RETRY", message));
    }

    // ---------- Helpers ----------

    private String safeMsg(@Nullable String msg) {
        if (msg == null)
            return null;
        return msg.replaceAll("[\\r\\n]+", " ").trim();
    }
}
