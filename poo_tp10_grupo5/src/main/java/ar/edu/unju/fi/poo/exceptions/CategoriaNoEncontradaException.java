package ar.edu.unju.fi.poo.exceptions;

public class CategoriaNoEncontradaException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CategoriaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}