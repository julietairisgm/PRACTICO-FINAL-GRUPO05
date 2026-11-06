package ar.edu.unju.fi.poo.exceptions;

public class VentaFinanciadaInvalidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public VentaFinanciadaInvalidaException(String mensaje) {
        super(mensaje);
    }
}