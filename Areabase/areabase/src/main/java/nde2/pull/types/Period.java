package nde2.pull.types;

import java.io.Serializable;
import java.util.Date;

public class Period implements Serializable {

	public static final char SINGLE_DATE = 's';
	public static final char DATE_RANGE = 'r';

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Date startDate;
	private Date endDate;
	private char periodType;
	private int __delivery_getTables_periodPointer;

	public Period() {
	}

	/**
	 * @return the starting date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @return the ending date
	 */
	public Date getEndDate() {
		return endDate;
	}

	public int get__pointer() {
		return __delivery_getTables_periodPointer;
	}

	public char getPeriodType() {
		return periodType;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @param __delivery_getTables_periodPointer
	 *            the __delivery_getTables_periodPointer to set
	 */
	public void set__pointer(int __delivery_getTables_periodPointer) {
		this.__delivery_getTables_periodPointer = __delivery_getTables_periodPointer;
	}

	public void setPeriodType(char periodType) {
		this.periodType = periodType;
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
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + periodType;
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
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
		if (!(obj instanceof Period)) {
            return false;
        }
		Period other = (Period) obj;
		if (endDate == null) {
			if (other.endDate != null) {
                return false;
            }
		} else if (!endDate.equals(other.endDate)) {
            return false;
        }
		if (periodType != other.periodType) {
            return false;
        }
		if (startDate == null) {
			if (other.startDate != null) {
                return false;
            }
		} else if (!startDate.equals(other.startDate)) {
            return false;
        }
		return true;
	}

}
