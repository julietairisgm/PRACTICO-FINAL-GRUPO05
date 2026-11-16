package ar.edu.unju.fi.poo.exceptions;

public class VentaNoEncontradaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public VentaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}