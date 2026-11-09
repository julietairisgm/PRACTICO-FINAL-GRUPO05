package ar.edu.unju.fi.poo.exceptions;

public class CuponInvalidoException extends RuntimeException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CuponInvalidoException(String mensaje) {
        super(mensaje);
    }
}
