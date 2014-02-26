package nde2.pull.types;

import java.io.Serializable;

public class DataSetItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Topic topic;
	private Boundary boundary;
	private Period period;
	private float value;

	public DataSetItem() {
	}

	/**
	 * @return the topic
	 */
	public Topic getTopic() {
		return topic;
	}

	/**
	 * @param topic
	 *            the topic to set
	 */
	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	/**
	 * @return the boundary
	 */
	public Boundary getBoundary() {
		return boundary;
	}

	/**
	 * @param boundary
	 *            the boundary to set
	 */
	public void setBoundary(Boundary boundary) {
		this.boundary = boundary;
	}

	/**
	 * @return the period
	 */
	public Period getPeriod() {
		return period;
	}

	/**
	 * @param period
	 *            the period to set
	 */
	public void setPeriod(Period period) {
		this.period = period;
	}

	/**
	 * @return the value
	 */
	public float getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(float value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((boundary == null) ? 0 : boundary.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((topic == null) ? 0 : topic.hashCode());
		result = prime * result + Float.floatToIntBits(value);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
		if (obj == null) {
            return false;
        }
		if (!(obj instanceof DataSetItem)) {
            return false;
        }
		DataSetItem other = (DataSetItem) obj;
		if (boundary == null) {
			if (other.boundary != null) {
                return false;
            }
		} else if (!boundary.equals(other.boundary)) {
            return false;
        }
		if (period == null) {
			if (other.period != null) {
                return false;
            }
		} else if (!period.equals(other.period)) {
            return false;
        }
		if (topic == null) {
			if (other.topic != null) {
                return false;
            }
		} else if (!topic.equals(other.topic)) {
            return false;
        }
		if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value)) {
            return false;
        }
		return true;
	}
}
