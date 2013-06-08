package nde2.types.discovery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nde2.types.NDE2Result;

/**
 * Represents a date range
 * 
 * @author filip
 * 
 */
public class DateRange extends NDE2Result {

	private static final long serialVersionUID = 1L;

	// 2008-09-01T00:00:00.000+01:00
	private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";

	private Date startDate;
	private Date endDate;

	/**
	 * @param startDate
	 *            Starting date of this date range
	 * @param endDate
	 *            ending date of this date range
	 */
	public DateRange(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public DateRange(String startDateString, String endDateString)
			throws ParseException {
		String _startDateString = startDateString.substring(0,
				startDateString.length() - 5);
		_startDateString += startDateString.substring(
				startDateString.length() - 5, startDateString.length())
				.replace(":", "");

		String _endDateString = endDateString.substring(0,
				endDateString.length() - 5);
		_endDateString += endDateString.substring(endDateString.length() - 5,
				endDateString.length()).replace(":", "");

		startDate = new SimpleDateFormat(DATETIME_PATTERN)
				.parse(_startDateString);
		endDate = new SimpleDateFormat(DATETIME_PATTERN).parse(_endDateString);
	}

	/**
	 * @return the Starting date of this date range
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @return the ending date of this date range
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @return The duration of this time range
	 */
	public Date getDuration() {
		return new Date(endDate.getTime() - startDate.getTime());
	}

}
