package nde2.types.discovery;

import nde2.types.NDE2Result;

/**
 * Represents the periodicity, or the update frequency, of a variable.
 * 
 * @author filip
 * 
 */
public class Periodicity extends NDE2Result {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private int id;
	private String description;
	private String moreDescription;

	/**
	 * @param name
	 * @param id
	 * @param description
	 * @param moreDescription
	 */
	public Periodicity(String name, int id, String description,
			String moreDescription) {
		super();
		this.name = name;
		this.id = id;
		this.description = description;
		this.moreDescription = moreDescription;
	}

	/**
	 * @return the name of this periodicity
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the internal periodicity id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return short description of this periodicity
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the longer description of this periodicity - may be null.
	 */
	public String getMoreDescription() {
		return moreDescription;
	}

}
