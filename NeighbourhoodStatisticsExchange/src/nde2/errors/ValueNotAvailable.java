package nde2.errors;

import nde2.types.delivery.DataSetItem;

/**
 * This is not an exception. ValueNotAvailable is thrown whenever a value is
 * literally not available, as it was not returned by the server. In reality, it
 * is thrown when a {@link DataSetItem#getValue()} value is equal to
 * {@link Integer#MIN_VALUE}, which seems like a sensible choice for 32-bit or
 * higher systems.
 * <p>
 * <b>Handling:</b> Display a "N/A" inside the offending value's cell, or skip
 * this value when comparing things.
 * 
 * @author filip
 * 
 */
public class ValueNotAvailable extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3176477718744936579L;

	public ValueNotAvailable() {
		super();
	}

	public ValueNotAvailable(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ValueNotAvailable(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public ValueNotAvailable(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
