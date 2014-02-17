package nde2.pull.types;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents a dataset family, as returned by the NDE2 web service.
 * 
 * @author filip
 * 
 */
public class DataSetFamily implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DateRange[] dateRanges;
	private int familyId;
	private String name;

	public DataSetFamily() {
	}

	/**
	 * @return the date ranges of this dataset family
	 */
	public DateRange[] getDateRanges() {
		return dateRanges;
	}

	/**
	 * @param dateRanges
	 *            the dateRanges to set
	 */
	public void setDateRanges(DateRange[] dateRanges) {
		this.dateRanges = dateRanges;
	}

	/**
	 * @return the id of this dataset family
	 */
	public int getFamilyId() {
		return familyId;
	}

	/**
	 * @param familyId
	 *            the familyId to set
	 */
	public void setFamilyId(int familyId) {
		this.familyId = familyId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
		result = prime * result + Arrays.hashCode(dateRanges);
		result = prime * result + familyId;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (!(obj instanceof DataSetFamily))
			return false;
		DataSetFamily other = (DataSetFamily) obj;
		if (!Arrays.equals(dateRanges, other.dateRanges))
			return false;
		if (familyId != other.familyId)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
