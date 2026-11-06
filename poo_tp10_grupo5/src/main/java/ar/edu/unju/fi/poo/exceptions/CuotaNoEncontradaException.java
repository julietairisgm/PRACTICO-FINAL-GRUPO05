package ar.edu.unju.fi.poo.exceptions;

public class CuotaNoEncontradaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CuotaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}