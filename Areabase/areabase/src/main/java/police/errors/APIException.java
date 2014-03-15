package police.errors;

/**
 * An exception that can be thrown in case of a REST-ful service error
 */
public class APIException extends Exception {

	private int httpCode = 0;

	/**
	 * 
	 */
	private static final long serialVersionUID = -705215153574849785L;

	/**
	 * 
	 */
	public APIException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message the message attached to this exception
	 * @param cause a throwable cause (usually another exception)
	 */
	public APIException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message the message attached to this exception
	 */
	public APIException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public APIException(String message, int httpCode) {
		super(message);
		this.httpCode = httpCode;
	}

	/**
	 * @param cause a throwable cause (usually another exception)
	 */
	public APIException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the http code returned by the service
	 */
	public int getHttpCode() {
		return httpCode;
	}

	/**
	 * @param httpCode
	 *            the http code returned by the service
	 */
	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}

}
