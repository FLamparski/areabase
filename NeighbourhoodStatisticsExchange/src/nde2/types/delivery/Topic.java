package nde2.types.delivery;

import nde2.types.NDE2Result;
import nde2.types.discovery.DataSetFamily;

/**
 * A column in a {@link Dataset}. To get values that belong to this column from
 * a data set, use {@link Dataset#getItems(Topic)}
 * 
 * @author filip
 * 
 */
@Deprecated
public class Topic extends NDE2Result {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Fun fact -- after declaring these fields, I added all these methods
	// without writing any code.
	// Shift-Alt-S is awesome.
	private int topicId;
	private int topicCode;
	private int identifier;
	private String creator;
	private String description;
	private String title;
	private String coinageUnit;

	/**
	 * @param topicId
	 * @param topicCode
	 * @param creator
	 * @param description
	 * @param title
	 * @param coinageUnit
	 *            the unit in which this value is presented. Count, Percentage,
	 *            etc.
	 */
	public Topic(int topicId, int topicCode, int identifier, String creator,
			String description, String title, String coinageUnit) {
		super(VALID_FOR_DAYS); // Default Delivery timekeeping thing
		this.topicId = topicId;
		this.topicCode = topicCode;
		this.identifier = identifier;
		this.creator = creator;
		this.description = description;
		this.title = title;
		this.coinageUnit = coinageUnit;
	}

	/**
	 * <b>Do not use</b>: If you need a primary-key ID of this Topic, use
	 * {@link Topic#getTopicCode()} instead. This is only useful when ordering
	 * topics during the creation of a {@link Dataset}.
	 * 
	 * @return ID of the topic as spat out by the server, but normalised and
	 *         with a zero-based index.
	 */
	public int getTopicId() {
		return topicId;
	}

	/**
	 * @return the topicCode
	 */
	public int getTopicCode() {
		return topicCode;
	}

	/**
	 * @return the identifier -- which is also the id of the
	 *         {@link DataSetFamily} this Topic is in.
	 */
	public int getIdentifier() {
		return identifier;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the the unit in which this value is presented. Count, Percentage,
	 *         etc.
	 */
	public String getCoinageUnit() {
		return coinageUnit;
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
		result = prime * result
				+ ((coinageUnit == null) ? 0 : coinageUnit.hashCode());
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + identifier;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + topicCode;
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
		if (!(obj instanceof Topic))
			return false;
		Topic other = (Topic) obj;
		if (coinageUnit == null) {
			if (other.coinageUnit != null)
				return false;
		} else if (!coinageUnit.equals(other.coinageUnit))
			return false;
		if (creator == null) {
			if (other.creator != null)
				return false;
		} else if (!creator.equals(other.creator))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (identifier != other.identifier)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (topicCode != other.topicCode)
			return false;
		return true;
	}

}
