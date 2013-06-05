package nde2.types.discovery;

import nde2.types.NDE2Result;

public class DataSetFamiliy extends NDE2Result {

	private static final long serialVersionUID = 1L;

	private DateRange[] dateRanges;
	private int familyId;
	private String name;

	/**
	 * @param dateRanges
	 * @param familyId
	 * @param name
	 */
	public DataSetFamiliy(DateRange[] dateRanges, int familyId, String name) {
		this.dateRanges = dateRanges;
		this.familyId = familyId;
		this.name = name;
	}

	/**
	 * @return the dateRanges
	 */
	public DateRange[] getDateRanges() {
		return dateRanges;
	}

	/**
	 * @return the familyId
	 */
	public int getFamilyId() {
		return familyId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
