package ar.edu.unju.fi.poo.exceptions;

public class ClienteNoEncontradoException extends RuntimeException {
    
	private static final long serialVersionUID = 1L;

	public ClienteNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}