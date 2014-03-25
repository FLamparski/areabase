package nde2.pull.types;
/** !license-block 
    This file is part of Areabase.

    Areabase is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Areabase is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Areabase.  If not, see <http://www.gnu.org/licenses/>.

    Areabase (C) 2013-2014 Filip Wieland <filiplamparski@gmail.com>
*/
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
