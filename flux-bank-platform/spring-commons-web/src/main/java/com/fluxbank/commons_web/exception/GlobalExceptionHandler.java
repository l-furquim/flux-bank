package com.fluxbank.commons_web.exception;

import com.fluxbank.commons_core.exception.FluxException;
import com.fluxbank.commons_web.domain.ErrorDetails;
import com.fluxbank.starter_context.domain.FluxContext;
import com.fluxbank.starter_context.service.FluxContextAccessor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final FluxContextAccessor contextAccessor;

    public GlobalExceptionHandler(FluxContextAccessor contextAccessor) {
        this.contextAccessor = contextAccessor;
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomainException(
            DomainException ex,
            HttpServletRequest request
    ) {
        ErrorDetails details = ex.toErrorDetails(correlationId());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(ex.getStatus(), details.detail());
        problem.setType(URI.create(details.type()));
        problem.setTitle(details.title());
        problem.setInstance(URI.create(request.getRequestURI()));
        applyContext(problem);

        if (ex.getStatus().is5xxServerError()) {
            log.error("Domain error: {}", ex.getMessage(), ex);
        } else {
            log.warn("Domain error: {}", ex.getMessage());
        }

        return ResponseEntity.status(ex.getStatus()).body(problem);
    }

    @ExceptionHandler(FluxException.class)
    public ResponseEntity<ProblemDetail> handleFluxException(
            FluxException ex,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_CONTENT,
                ex.getMessage()
        );
        problem.setType(URI.create(DomainException.ERROR_BASE_URI + ex.code()));
        problem.setTitle(ex.code());
        problem.setInstance(URI.create(request.getRequestURI()));
        applyContext(problem);
        log.warn("Business error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Um ou mais campos são inválidos"
        );
        problem.setType(URI.create(DomainException.ERROR_BASE_URI + "validation-error"));
        problem.setTitle("validation-error");
        problem.setInstance(URI.create(request.getRequestURI()));
        applyContext(problem);

        problem.setProperty("invalid_fields", ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> java.util.Map.of(
                        "field", fe.getField(),
                        "message", fe.getDefaultMessage() == null ? "inválido" : fe.getDefaultMessage()
                ))
                .toList());

        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(
            Exception ex,
            HttpServletRequest request
    ) throws Exception {
        if (ex instanceof ErrorResponse) {
            throw ex;
        }

        log.error("Unexpected error", ex);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno inesperado"
        );
        problem.setType(URI.create(DomainException.ERROR_BASE_URI + "internal-error"));
        problem.setTitle("internal-error");
        problem.setInstance(URI.create(request.getRequestURI()));
        applyContext(problem);

        return ResponseEntity.internalServerError().body(problem);
    }

    private String correlationId() {
        return contextAccessor.getOptional()
                .map(context -> context.correlationId()._asString())
                .orElse(null);
    }

    private void applyContext(ProblemDetail problem) {
        contextAccessor.getOptional().ifPresent(context -> copyContext(problem, context));
    }

    private static void copyContext(ProblemDetail problem, FluxContext context) {
        problem.setProperty("correlation_id", context.correlationId()._asString());
        problem.setProperty("request_id", context.requestId()._asString());
        if (context.traceId() != null && !context.traceId().isBlank()) {
            problem.setProperty("trace_id", context.traceId());
        }
    }
}
