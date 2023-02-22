package simulator.model.exception;

public class WrongStrategyException extends Exception {

	public WrongStrategyException() {
	}

	public WrongStrategyException(String message) {
		super(message);
	}

	public WrongStrategyException(Throwable cause) {
		super(cause);
	}

	public WrongStrategyException(String message, Throwable cause) {
		super(message, cause);
	}

	public WrongStrategyException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
