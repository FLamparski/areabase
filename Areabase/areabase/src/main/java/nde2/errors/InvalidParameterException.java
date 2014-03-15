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
	 * @param message explanation for the exception
	 * @param cause another throwable that caused this exception
	 */
	public InvalidParameterException(String message, Throwable cause) {
		super(message, cause);
		parameterName = "(unspecified)";
	}

	/**
	 * @param message explanation for the exception
	 */
	public InvalidParameterException(String message) {
		super(message);
		parameterName = "(unspecified)";
	}

	/**
	 * @param cause another throwable that caused this exception
	 */
	public InvalidParameterException(Throwable cause) {
		super(cause);
		parameterName = "(unspecified)";
	}

    /**
     *
     * @param parameterName the parameter that was set to something invalid
     * @param message explanation for the exception
     */
	public InvalidParameterException(String parameterName, String message) {
		super(message);
		this.parameterName = parameterName;
	}

    /**
     *
     * @param parameterName the parameter that was set to something invalid
     * @param message explanation for the exception
     * @param cause another throwable that caused this exception
     */
	public InvalidParameterException(String parameterName, String message,
			Throwable cause) {
		super(message, cause);
		this.parameterName = parameterName;
	}

	/**
	 * @return the invalid parameter's name
	 */
	public String getParameterName() {
		return parameterName;
	}

}
