package ar.edu.unju.fi.poo.exceptions;

public class VentaYaSaldadaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public VentaYaSaldadaException(String mensaje) {
        super(mensaje);
    }
}
