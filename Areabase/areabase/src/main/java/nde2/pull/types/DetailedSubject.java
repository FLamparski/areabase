package nde2.pull.types;

import java.io.IOException;

import nde2.errors.NDE2Exception;

import org.xmlpull.v1.XmlPullParserException;

/**
 * A more detailed representation of a subject. Has *two* descriptions!
 * 
 * @author filip
 * @see {@link Subject}
 */
public class DetailedSubject extends Subject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String description;
	private String moreDescription;

	/**
	 * @param name
	 * @param id
	 * @param description
	 * @param moreDescription
	 */
	public DetailedSubject(String name, int id, String description,
			String moreDescription) {
		super(name, id);
		this.description = description;
		this.moreDescription = moreDescription;
	}

	public DetailedSubject(Subject basic, String description,
			String moreDescription) {
		super(basic);
		this.description = description;
		this.moreDescription = moreDescription;
	}

	public DetailedSubject() {
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the moreDescription
	 */
	public String getMoreDescription() {
		return moreDescription;
	}

	/**
	 * @param moreDescription
	 *            the moreDescription to set
	 */
	public void setMoreDescription(String moreDescription) {
		this.moreDescription = moreDescription;
	}

	/**
	 * @return Itself. It's already a DetailedSubject after all.
	 */
	@Override
	public DetailedSubject getDetailed() throws IOException,
			XmlPullParserException, NDE2Exception {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((moreDescription == null) ? 0 : moreDescription.hashCode());
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
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof DetailedSubject))
			return false;
		DetailedSubject other = (DetailedSubject) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (moreDescription == null) {
			if (other.moreDescription != null)
				return false;
		} else if (!moreDescription.equals(other.moreDescription))
			return false;
		return true;
	}
}
