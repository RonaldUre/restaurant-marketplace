// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/shared/infrastructure/web/exception/GlobalExceptionHandler.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.exception;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.ForbiddenOperationException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.RestaurantNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.SlugAlreadyInUseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Global HTTP exception mapper.
 *
 * Responsibilities:
 * - Translate application/domain/infra exceptions to consistent HTTP error
 * responses.
 * - Never leak internal details (stacktraces, SQL, etc.) to clients.
 * - Provide request path and (optional) traceId for observability.
 *
 * Error format is intentionally simple and stable across modules.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // ---------- Application-specific (Restaurant module) ----------

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRestaurantNotFound(RestaurantNotFoundException ex,
            HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
    }

    @ExceptionHandler(SlugAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleSlugAlreadyInUse(SlugAlreadyInUseException ex,
            HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req, null);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenOperation(ForbiddenOperationException ex,
            HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), req, null);
    }

    // ---------- Validation & binding ----------

    /** Bean validation for @Valid @RequestBody. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpServletRequest req) {
        List<ValidationError> errors = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.add(new ValidationError(fe.getField(), safeMsg(fe.getDefaultMessage())));
        }
        return build(HttpStatus.BAD_REQUEST, "Validation failed for request body", req, errors);
    }

    /**
     * Validation for method parameters (@RequestParam, @PathVariable)
     * with @Validated.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
            HttpServletRequest req) {
        List<ValidationError> errors = new ArrayList<>();
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            String field = v.getPropertyPath() == null ? null : v.getPropertyPath().toString();
            errors.add(new ValidationError(field, safeMsg(v.getMessage())));
        }
        return build(HttpStatus.BAD_REQUEST, "Constraint violation", req, errors);
    }

    /** Binding errors for @ModelAttribute or general binding failures. */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex,
            HttpServletRequest req) {
        List<ValidationError> errors = new ArrayList<>();
        for (FieldError fe : ex.getFieldErrors()) {
            errors.add(new ValidationError(fe.getField(), safeMsg(fe.getDefaultMessage())));
        }
        return build(HttpStatus.BAD_REQUEST, "Binding failed", req, errors);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex,
            HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Missing request parameter: " + ex.getParameterName(), req, null);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException ex,
            HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Missing request header: " + ex.getHeaderName(), req, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
            HttpServletRequest req) {
        String name = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String msg = "Parameter '" + name + "' has invalid value. Expected type: " + requiredType;
        return build(HttpStatus.BAD_REQUEST, msg, req, null);
    }

    // ---------- HTTP protocol & parsing ----------

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
            HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Malformed JSON request", req, null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException ex,
            HttpServletRequest req) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type", req, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpServletRequest req) {
        HttpHeaders headers = new HttpHeaders();
        if (ex.getSupportedHttpMethods() != null) {
            for (HttpMethod m : ex.getSupportedHttpMethods()) {
                headers.add(HttpHeaders.ALLOW, m.name());
            }
        }
        return new ResponseEntity<>(
                errorBody(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed", req, null),
                headers,
                HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandler(NoHandlerFoundException ex,
            HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "Endpoint not found", req, null);
    }

    // ---------- Data layer ----------

    /**
     * Generic DB constraint violations (e.g., unique indexes) not previously
     * caught.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
            HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "Data integrity violation", req, null);
    }

    // ---------- Domain fallbacks ----------

    /** Domain guardrails surfaced as IllegalArgumentException ⇒ 400 (bad input). */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
            HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, safeMsg(ex.getMessage()), req, null);
    }

    /**
     * IllegalStateException often represents business state conflicts ⇒ 409.
     * (e.g., trying to open a suspended restaurant if domain throws such guard)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex,
            HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, safeMsg(ex.getMessage()), req, null);
    }

    // ---------- Last resort ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        // Do not leak internal details. Log server-side with stacktrace.
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", req, null);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimistic(ObjectOptimisticLockingFailureException ex,
            HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "Concurrent modification", req, null);
    }

    // ---------- Helpers ----------

    private ResponseEntity<ErrorResponse> build(HttpStatus status,
            String message,
            HttpServletRequest req,
            @Nullable List<ValidationError> errors) {
        return new ResponseEntity<>(errorBody(status, message, req, errors), status);
    }

    private ErrorResponse errorBody(HttpStatus status,
            String message,
            HttpServletRequest req,
            @Nullable List<ValidationError> errors) {
        String traceId = MDC.get("traceId"); // if you put traceId into MDC earlier (filter/interceptor)
        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                safeMsg(message),
                req != null ? req.getRequestURI() : null,
                Instant.now().toString(),
                traceId,
                errors == null ? List.of() : errors);
    }

    private String safeMsg(@Nullable String msg) {
        if (msg == null)
            return null;
        // Strip newlines or sensitive info if necessary (keep client payloads tidy)
        return msg.replaceAll("[\\r\\n]+", " ").trim();
    }

    // ---------- Error payloads ----------

    public record ErrorResponse(
            int status,
            String error,
            String message,
            String path,
            String timestamp,
            String traceId,
            List<ValidationError> errors) {
    }

    public record ValidationError(
            @Nullable String field,
            String message) {
    }
}