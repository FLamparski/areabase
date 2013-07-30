package nde2.types.delivery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nde2.types.NDE2Result;
import nde2.types.discovery.DataSetFamiliy;

/**
 * This class represents a dataset returned by the delivery service. It contains
 * topics, values and boundaries relevant to the dataset.
 * 
 * @author filip
 * 
 */
public class Dataset extends NDE2Result {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int datasetCode;
	private String creator;
	private String description;
	private String subjectCategory;
	private String title;
	private Map<Integer, Topic> topics;
	private Map<Integer, Period> periods;
	private Map<Integer, Boundary> boundaries;
	private List<DataSetItem> items;

	/**
	 * @param datasetCode
	 *            Internal identifier of the dataset in question. This will be
	 *            the same as a familyId of a {@link DataSetFamiliy}
	 * @param creator
	 *            Creator of the data set. Use to do attributions for data.
	 * @param description
	 *            Description of the data set. May be empty.
	 * @param subjectCategory
	 *            Subject category of the data set. May be empty.
	 * @param title
	 *            Title of the data set.
	 * @param topics
	 *            A list of topics covered by the data set.
	 * @param periods
	 *            A list of periods covered by the data set.
	 * @param boundaries
	 *            A list of geographical boundaries covered by the data set.
	 * @param items
	 *            A list of values in the data set.
	 */
	public Dataset(int datasetCode, String creator, String description,
			String subjectCategory, String title, Map<Integer, Topic> topics,
			Map<Integer, Period> periods, Map<Integer, Boundary> boundaries,
			List<DataSetItem> items) {
		super(VALID_FOR_DAYS); // Keep datasets valid for a week. It
								// may be a heavy download,
		// and if I can figure out a way to store them in cache
		// instead of re-fetching, this could save a lot of time and
		// offline troubles.
		this.datasetCode = datasetCode;
		this.creator = creator;
		this.description = description;
		this.subjectCategory = subjectCategory;
		this.title = title;
		this.topics = topics;
		this.periods = periods;
		this.boundaries = boundaries;
		this.items = items;
	}

	/**
	 * @return the internal identifier of the dataset in question. This will be
	 *         the same as a familyId of a {@link DataSetFamiliy}
	 */
	public int getDatasetCode() {
		return datasetCode;
	}

	/**
	 * @return the Creator of the data set. Use to do attributions for data.
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @return the Description of the data set. May be empty.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the Subject category of the data set. May be empty.
	 */
	public String getSubjectCategory() {
		return subjectCategory;
	}

	/**
	 * @return the Title of the data set.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the list of topics covered by the data set.
	 */
	public Map<Integer, Topic> getTopics() {
		return topics;
	}

	/**
	 * @return the list of periods covered by the data set.
	 */
	public Map<Integer, Period> getPeriods() {
		return periods;
	}

	/**
	 * @return the list of geographical boundaries covered by the data set.
	 */
	public Map<Integer, Boundary> getBoundaries() {
		return boundaries;
	}

	/**
	 * @return the list of values in the data set.
	 */
	public List<DataSetItem> getItems() {
		return items;
	}

	/**
	 * Find values for a specific topic
	 * 
	 * @param topic
	 *            The topic to look for
	 * @return A list of values that match the specified topic
	 */
	public List<DataSetItem> getItems(Topic topic) {
		ArrayList<DataSetItem> validItems = new ArrayList<DataSetItem>();
		for (DataSetItem item : items) {
			if (item.getTopic().equals(topic)) {
				validItems.add(item);
			}
		}
		return validItems;
	}

	/**
	 * Find values for a specific period
	 * 
	 * @param period
	 *            The period to look for
	 * @return A list of values that come from the specified time period
	 */
	public List<DataSetItem> getItems(Period period) {
		ArrayList<DataSetItem> validItems = new ArrayList<DataSetItem>();
		for (DataSetItem item : items) {
			if (item.getPeriod().equals(period)) {
				validItems.add(item);
			}
		}
		return validItems;
	}

	/**
	 * Find values that fall within a specified boundary
	 * 
	 * @param boundary
	 *            The geographical boundary to get data from witin
	 * @return A list of values from within that boundary
	 */
	public List<DataSetItem> getItems(Boundary boundary) {
		ArrayList<DataSetItem> validItems = new ArrayList<DataSetItem>();
		for (DataSetItem item : items) {
			if (item.getBoundary().equals(boundary)) {
				validItems.add(item);
			}
		}
		return validItems;
	}
}
