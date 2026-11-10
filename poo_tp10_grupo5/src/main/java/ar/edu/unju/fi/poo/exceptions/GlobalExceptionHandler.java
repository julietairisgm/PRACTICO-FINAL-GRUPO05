package ar.edu.unju.fi.poo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ar.edu.unju.fi.poo.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 NOT FOUND 
    @ExceptionHandler({
        ClienteNoEncontradoException.class, 
        ProductoNoEncontradoException.class, 
        CategoriaNoEncontradaException.class,
        CuotaNoEncontradaException.class
    })
    public ResponseEntity<ErrorResponseDTO> manejarNoEncontrado(Exception ex, HttpServletRequest request) {
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.NOT_FOUND.value(),
            "Recurso No Encontrado",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 400 BAD REQUEST 
    @ExceptionHandler({
        VentaInvalidaException.class, 
        VentaFinanciadaInvalidaException.class, 
        CuponInvalidoException.class,
        IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponseDTO> manejarPeticionInvalida(Exception ex, HttpServletRequest request) {
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Petición Incorrecta",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 409 CONFLICT o 422 UNPROCESSABLE ENTITY 
    @ExceptionHandler({StockInsuficienteException.class, ClienteDuplicadoException.class})
    public ResponseEntity<ErrorResponseDTO> manejarConflictosNegocio(Exception ex, HttpServletRequest request) {
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.CONFLICT.value(),
            "Conflicto de Regla de Negocio",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 500 INTERNAL SERVER ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarErroresGenerales(Exception ex, HttpServletRequest request) {
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error Interno del Servidor",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}