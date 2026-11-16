package ar.edu.unju.fi.poo.exceptions;

public class CuotaAnteriorImpagaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CuotaAnteriorImpagaException(String mensaje) {
        super(mensaje);
    }
}
