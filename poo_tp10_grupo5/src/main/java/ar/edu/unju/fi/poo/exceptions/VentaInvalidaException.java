package ar.edu.unju.fi.poo.exceptions;

public class VentaInvalidaException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VentaInvalidaException(String mensaje) {
        super(mensaje);
    }
}