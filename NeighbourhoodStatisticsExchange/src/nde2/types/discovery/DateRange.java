package nde2.types.discovery;

import java.text.ParseException;
import java.util.Date;

import nde2.helpers.DateFormat;
import nde2.types.NDE2Result;

/**
 * Represents a date range
 * 
 * @author filip
 * 
 */
@Deprecated
public class DateRange extends NDE2Result {

	private static final long serialVersionUID = 1L;

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
		startDate = DateFormat.fromNDEDateOnly(startDateString.split("T")[0]);
		endDate = DateFormat.fromNDEDateOnly(endDateString.split("T")[0]);

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
