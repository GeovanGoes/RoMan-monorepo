package com.roman.interfaces.rest;

import com.roman.domain.exception.DomainException;
import com.roman.domain.exception.CategoriaConsumoNotFoundException;
import com.roman.domain.exception.CompraNotFoundException;
import com.roman.domain.exception.EventoNotFoundException;
import com.roman.domain.exception.EventoParticipanteNotFoundException;
import com.roman.domain.exception.ParticipanteNotFoundException;
import com.roman.domain.exception.ParticipanteJaVinculadoException;
import com.roman.domain.exception.UsernameJaExisteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ParticipanteNotFoundException.class,
            EventoNotFoundException.class,
            CategoriaConsumoNotFoundException.class,
            CompraNotFoundException.class,
            EventoParticipanteNotFoundException.class
    })
    public ProblemDetail handleNotFound(DomainException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({
            UsernameJaExisteException.class,
            ParticipanteJaVinculadoException.class
    })
    public ProblemDetail handleConflict(DomainException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,
                        (existing, replacement) -> existing));
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setDetail("Erro de validação");
        problem.setProperty("errors", errors);
        return problem;
    }
}
