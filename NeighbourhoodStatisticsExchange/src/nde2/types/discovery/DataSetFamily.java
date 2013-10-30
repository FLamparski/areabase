package nde2.types.discovery;

import nde2.types.NDE2Result;

/**
 * Represents a dataset family, as returned by NDE2 web service.
 * 
 * @author filip
 * 
 */
@Deprecated
public class DataSetFamily extends NDE2Result {

	private static final long serialVersionUID = 1L;

	private DateRange[] dateRanges;
	private int familyId;
	private String name;

	/**
	 * @param dateRanges
	 * @param familyId
	 * @param name
	 */
	public DataSetFamily(DateRange[] dateRanges, int familyId, String name) {
		this.dateRanges = dateRanges;
		this.familyId = familyId;
		this.name = name;
	}

	/**
	 * @return Date ranges covered by this dataset family.
	 */
	public DateRange[] getDateRanges() {
		return dateRanges;
	}

	/**
	 * @return NDE's internal ID of this family
	 */
	public int getFamilyId() {
		return familyId;
	}

	/**
	 * @return Proper name of this family.
	 */
	public String getName() {
		return name;
	}
}
