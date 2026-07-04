package com.cj7.ubicate.infrastructure.web;

import com.cj7.ubicate.domain.exception.DatosInvalidosException;
import com.cj7.ubicate.domain.exception.LocalDuplicadoException;
import com.cj7.ubicate.infrastructure.web.generated.model.ErrorResponse;
import com.cj7.ubicate.infrastructure.web.generated.model.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Traduce las excepciones de validación y de negocio a las respuestas del contrato:
 * 422 para campos obligatorios faltantes o inválidos, 409 para duplicados.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<ValidationErrorResponse> manejarDatosInvalidos(DatosInvalidosException ex) {
        return unprocessable("Faltan campos obligatorios o hay datos inválidos para publicar el perfil.",
                ex.getCampos());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> manejarValidacionBean(MethodArgumentNotValidException ex) {
        List<String> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(org.springframework.validation.FieldError::getField)
                .distinct()
                .toList();
        return unprocessable("Faltan campos obligatorios o hay datos inválidos para publicar el perfil.", campos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationErrorResponse> manejarCuerpoInvalido(HttpMessageNotReadableException ex) {
        return unprocessable("Cuerpo de la solicitud inválido o con valores no permitidos.", List.of());
    }

    @ExceptionHandler(LocalDuplicadoException.class)
    public ResponseEntity<ErrorResponse> manejarDuplicado(LocalDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    private ResponseEntity<ValidationErrorResponse> unprocessable(String mensaje, List<String> campos) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ValidationErrorResponse(mensaje, campos));
    }
}
