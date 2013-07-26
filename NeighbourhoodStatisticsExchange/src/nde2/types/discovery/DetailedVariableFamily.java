package nde2.types.discovery;

import nde2.types.MeasurementUnit;
import nde2.types.StatisticalUnit;

public class DetailedVariableFamily extends VariableFamily {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String description;
	private Periodicity periodicity;

	/**
	 * @param id
	 * @param name
	 * @param dateRanges
	 * @param sunit
	 * @param munit
	 */
	public DetailedVariableFamily(int id, String name, DateRange[] dateRanges,
			StatisticalUnit sunit, MeasurementUnit munit, String desc,
			Periodicity periodicity) {
		super(id, name, dateRanges, sunit, munit);
		this.description = desc;
		this.periodicity = periodicity;
	}

	public DetailedVariableFamily(VariableFamily baseFamily, String desc,
			Periodicity periodicity) {
		super(baseFamily);
		this.description = desc;
		this.periodicity = periodicity;
	}

	/**
	 * @return the description of this variable family
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the periodicity (i.e. frequency of updates) of this variable
	 *         family
	 */
	public Periodicity getPeriodicity() {
		return periodicity;
	}

}
