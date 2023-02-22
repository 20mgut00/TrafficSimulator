package simulator.model.exception;

public class WrongObjectException extends Exception {

	public WrongObjectException() {
	}

	public WrongObjectException(String message) {
		super(message);
	}

	public WrongObjectException(Throwable cause) {
		super(cause);
	}

	public WrongObjectException(String message, Throwable cause) {
		super(message, cause);
	}

	public WrongObjectException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
