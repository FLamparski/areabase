package nde2.errors;

public class InvalidParameterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String parameterName;

	/**
	 * 
	 */
	public InvalidParameterException() {
		super();
		parameterName = "(unspecified)";
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidParameterException(String message, Throwable cause) {
		super(message, cause);
		parameterName = "(unspecified)";
	}

	/**
	 * @param message
	 */
	public InvalidParameterException(String message) {
		super(message);
		parameterName = "(unspecified)";
	}

	/**
	 * @param cause
	 */
	public InvalidParameterException(Throwable cause) {
		super(cause);
		parameterName = "(unspecified)";
	}

	public InvalidParameterException(String parameterName, String message) {
		super(message);
		this.parameterName = parameterName;
	}

	public InvalidParameterException(String parameterName, String message,
			Throwable cause) {
		super(message, cause);
		this.parameterName = parameterName;
	}

	/**
	 * @return the parameterName
	 */
	public String getParameterName() {
		return parameterName;
	}

}
