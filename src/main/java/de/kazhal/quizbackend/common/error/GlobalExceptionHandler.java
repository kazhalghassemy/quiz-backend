package de.kazhal.quizbackend.common.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.List;
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    // ---------- eigene fachliche Fehler ----------
    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Nicht gefunden", ex.getCode(), ex.getMessage());
    }
    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex) {
        return problem(HttpStatus.CONFLICT, "Konflikt", ex.getCode(), ex.getMessage());
    }
    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequest(BadRequestException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Ungültige Anfrage", ex.getCode(), ex.getMessage());
    }
    // ---------- technische Fehler, die bis zum Client durchkommen ----------
    @ExceptionHandler(PropertyReferenceException.class)
    public ProblemDetail handleInvalidSort(PropertyReferenceException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Ungültige Anfrage", "INVALID_SORT",
                "Nach '" + ex.getPropertyName() + "' kann nicht sortiert werden");
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Datenbank-Regel verletzt: {}", ex.getMostSpecificCause().getMessage());
        return problem(HttpStatus.CONFLICT, "Konflikt", "DATA_CONFLICT",
                "Die Änderung verstößt gegen eine Regel der Datenbank");
    }
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unerwarteter Fehler", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Interner Fehler", "INTERNAL_ERROR",
                "Ein unerwarteter Fehler ist aufgetreten");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex) {
        if (ex instanceof BadCredentialsException) {
            return problem(HttpStatus.UNAUTHORIZED, "Anmeldung fehlgeschlagen", "INVALID_CREDENTIALS",
                    "E-Mail oder Passwort ist falsch");
        }
        return problem(HttpStatus.UNAUTHORIZED, "Nicht angemeldet", "UNAUTHORIZED",
                "Für diese Anfrage ist eine Anmeldung nötig");
    }
    // -----
    // ----- Spring-MVC-Fehler anpassen (aus ResponseEntityExceptionHandler) ----------
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
            WebRequest request) {
        List<FieldErrorResponse> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldErrorResponse(fe.getField(), fe.getDefaultMessage()))
                .toList();
        ProblemDetail body = problem(status, "Ungültige Eingabe", "VALIDATION_FAILED",
                errors.size() == 1 ? "1 Feld ist ungültig" : errors.size() + " Felder sind ungültig");
                body.setProperty("errors", errors);
        return handleExceptionInternal(ex, body, headers, status, request);
    }
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status,
            WebRequest request) {
        ProblemDetail body = problem(status, "Ungültige Anfrage", "INVALID_REQUEST_BODY",
                "Der Request-Body ist kein gültiges JSON oder enthält unbekannte Werte");
        return handleExceptionInternal(ex, body, headers, status, request);
    }
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest
                    request) {
        String parameter = ex instanceof MethodArgumentTypeMismatchException m ? m.getName() :
                ex.getPropertyName();
        ProblemDetail body = problem(status, "Ungültige Anfrage", "INVALID_PARAMETER",
                "Ungültiger Wert '" + ex.getValue() + "' für Parameter '" + parameter + "'");
        return handleExceptionInternal(ex, body, headers, status, request);
    }
    // ---------- Hilfsmethode ----------
    private static ProblemDetail problem(HttpStatusCode status, String title, String code, String
            detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setProperty("code", code);
        return pd;
    }

}