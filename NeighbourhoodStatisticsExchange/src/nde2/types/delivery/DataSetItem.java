package nde2.types.delivery;

import nde2.errors.ValueNotAvailable;
import nde2.types.NDE2Result;

/**
 * A data set item. This represents a value within a {@link Dataset} ->
 * {@link Topic}, and defines its Topic, Boundary and Period.
 * 
 * @author filip
 * 
 */
public class DataSetItem extends NDE2Result {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Topic topic;
	private Boundary boundary;
	private Period period;
	private int value;

	/**
	 * @param topic
	 * @param boundary
	 * @param period
	 * @param value
	 */
	public DataSetItem(Topic topic, Boundary boundary, Period period, int value) {
		super(VALID_FOR_DAYS);
		this.topic = topic;
		this.boundary = boundary;
		this.period = period;
		this.value = value;
	}

	/**
	 * @return the topic
	 */
	public Topic getTopic() {
		return topic;
	}

	/**
	 * @return the boundary
	 */
	public Boundary getBoundary() {
		return boundary;
	}

	/**
	 * @return the period
	 */
	public Period getPeriod() {
		return period;
	}

	/**
	 * @return the value
	 * @throws ValueNotAvailable
	 */
	public int getValue() throws ValueNotAvailable {
		if (value > Integer.MIN_VALUE)
			return value;
		else
			throw new ValueNotAvailable();
	}

}
