package nde2.types.delivery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nde2.types.NDE2Result;

public class Period extends NDE2Result {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Date startDate;
	private Date endDate;
	private int periodCode;

	/**
	 * 
	 * @param startDateStr
	 *            String representation of the starting date (yyyy-MM-dd)
	 * @param endDateStr
	 *            String representation of the ending date (yyyy-MM-dd)
	 * @throws ParseException
	 *             If one or both of the date strings don't match the pattern
	 */
	public Period(String startDateStr, String endDateStr, int periodCode)
			throws ParseException {
		super(VALID_FOR_DAYS);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		startDate = dateFormat.parse(startDateStr);
		endDate = dateFormat.parse(endDateStr);
		this.periodCode = periodCode;
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

	/**
	 * <b>Do not use</b>: This is only useful when ordering topics during the
	 * creation of a {@link Dataset}.
	 * 
	 * @return ID of this particular time period as spat out by the server.
	 */
	public int getPeriodCode() {
		return periodCode;
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Period))
			return false;
		Period other = (Period) obj;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

}
