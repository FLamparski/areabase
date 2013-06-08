package nde2.types.discovery;

import nde2.types.MeasurementUnit;
import nde2.types.NDE2Result;
import nde2.types.StatisticalUnit;

public class VariableFamily extends NDE2Result {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int variableFamilyId;
	private DateRange[] dateRanges;
	private String name;
	private StatisticalUnit statisticalUnit;
	private MeasurementUnit measurementUnit;

	public VariableFamily(int id, String name, DateRange[] dateRanges,
			StatisticalUnit sunit, MeasurementUnit munit) {
		variableFamilyId = id;
		this.dateRanges = dateRanges;
		statisticalUnit = sunit;
		measurementUnit = munit;
	}

	/**
	 * @return the variableFamilyId
	 */
	public int getVariableFamilyId() {
		return variableFamilyId;
	}

	/**
	 * @return the dateRanges
	 */
	public DateRange[] getDateRanges() {
		return dateRanges;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the statisticalUnit
	 */
	public StatisticalUnit getStatisticalUnit() {
		return statisticalUnit;
	}

	/**
	 * @return the measurementUnit
	 */
	public MeasurementUnit getMeasurementUnit() {
		return measurementUnit;
	}
}
