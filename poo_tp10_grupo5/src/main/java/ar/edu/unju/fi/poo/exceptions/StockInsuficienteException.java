package ar.edu.unju.fi.poo.exceptions;

public class StockInsuficienteException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}