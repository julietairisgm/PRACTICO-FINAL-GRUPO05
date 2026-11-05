package ar.edu.unju.fi.poo.exceptions;

public class VentaInvalidaException extends RuntimeException {

    public VentaInvalidaException(String mensaje) {
        super(mensaje);
    }
}