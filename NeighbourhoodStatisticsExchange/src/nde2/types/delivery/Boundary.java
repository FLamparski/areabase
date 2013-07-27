package nde2.types.delivery;

import nde2.types.NDE2Result;

public class Boundary extends NDE2Result {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String boundaryCode;
	private String envelope;
	private String creator;
	private int identifier;
	private int id;
	private String title;

	/**
	 * @param boundaryCode
	 * @param envelope
	 * @param creator
	 * @param identifier
	 * @param title
	 */
	public Boundary(String boundaryCode, String envelope, String creator,
			int identifier, int id, String title) {
		super(VALID_FOR_DAYS);
		this.boundaryCode = boundaryCode;
		this.envelope = envelope;
		this.creator = creator;
		this.identifier = identifier;
		this.id = id;
		this.title = title;
	}

	/**
	 * @return the boundaryCode
	 */
	public String getBoundaryCode() {
		return boundaryCode;
	}

	/**
	 * @return the envelope
	 */
	public String getEnvelope() {
		return envelope;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @return the identifier, aka the AreaId of the area that this boundary
	 *         envelopes.
	 */
	public int getIdentifier() {
		return identifier;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
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
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result
				+ ((envelope == null) ? 0 : envelope.hashCode());
		result = prime * result + identifier;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		if (!(obj instanceof Boundary))
			return false;
		Boundary other = (Boundary) obj;
		if (creator == null) {
			if (other.creator != null)
				return false;
		} else if (!creator.equals(other.creator))
			return false;
		if (envelope == null) {
			if (other.envelope != null)
				return false;
		} else if (!envelope.equals(other.envelope))
			return false;
		if (identifier != other.identifier)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

}
