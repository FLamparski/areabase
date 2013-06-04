package nde2.types;

import java.io.Serializable;
import java.util.Date;

/**
 * Base class for all types resulting from calls to NDE2 Discovery service. Here
 * to declare that the results are {@link Serializable}, as well as provide
 * methods of controlling the lifetime of a result.
 * 
 * @author filip
 * 
 */
public abstract class NDE2Result implements Serializable {

	/**
	 * This is to keep the serialiser happy.
	 */
	protected static final long serialVersionUID = 1L;

	private Date dateGenerated;
	private Date dateExpires;

	public NDE2Result(int daysToExpire) {
		dateGenerated = new Date();
		dateExpires = new Date(dateGenerated.getTime()
				+ (daysToExpire * 24 * 60 * 60 * 1000L));
	}

	public NDE2Result() {
		dateGenerated = new Date();
		// Never expire by default
		dateExpires = new Date(0L);
	}

	/**
	 * 
	 * @return Whether or not the result has expired.
	 */
	public boolean isExpired() {
		if (dateExpires.getTime() == 0L) {
			// This is for objects that never expire
			return false;
		} else {
			return (dateExpires.getTime() < (new Date().getTime()));
		}
	}

	/**
	 * 
	 * @return The date and time this object was generated on.
	 */
	public Date getGenerationDate() {
		return dateGenerated;
	}

	/**
	 * 
	 * @return The use-by date of this object. If it is 0, then the object may
	 *         live indefinitely.
	 */
	public Date getExpirationDate() {
		return dateExpires;
	}

}
