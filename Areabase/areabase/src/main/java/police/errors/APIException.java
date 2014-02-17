package police.errors;

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
	 * @param message
	 * @param cause
	 */
	public APIException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
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
	 * @param cause
	 */
	public APIException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the httpCode
	 */
	public int getHttpCode() {
		return httpCode;
	}

	/**
	 * @param httpCode
	 *            the httpCode to set
	 */
	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}

}
