package nde2.pull.methodcalls.delivery;
/** !license-block 
    This file is part of Areabase.

    Areabase is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Areabase is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Areabase.  If not, see <http://www.gnu.org/licenses/>.

    Areabase (C) 2013-2014 Filip Wieland <filiplamparski@gmail.com>
*/
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nde2.errors.InvalidParameterException;
import nde2.errors.NDE2Exception;
import nde2.helpers.DateFormat;
import nde2.pull.types.Area;
import nde2.pull.types.Boundary;
import nde2.pull.types.DataSetFamily;
import nde2.pull.types.DataSetItem;
import nde2.pull.types.Dataset;
import nde2.pull.types.DateRange;
import nde2.pull.types.Period;
import nde2.pull.types.Topic;

/**
 * Fetches the dataset tables from ONS.
 *
 * <p>
 *     Generally, you'll need to set dataset families and areas
 *     to get the data for, and optionally a date range.
 * </p>
 *
 * @author filip
 */
public class GetTables extends DeliveryMethodCall {
	private final static String METHOD_NAME = "getTables";

	protected Set<Area> areas;
	protected Set<DataSetFamily> datasetFamilies;
	protected DateRange dateRange;

	private Map<Integer, Topic> dstopics;
	private Map<Integer, Boundary> dsboundaries;
	private Map<Integer, Period> dsperiods;

	public GetTables() {
		areas = new HashSet<Area>();
		datasetFamilies = new HashSet<DataSetFamily>();
		dateRange = null;
	}

	/**
	 * Accepts a single area. This really is just sugar, as forAreas(Area...)
	 * can do the same thing.
	 * 
	 * @param area
	 *            The area to find data for.
	 * @return The modified GetTables object for chaining instructions.
	 */
	public GetTables forArea(Area area) {
		this.areas.add(area);
		return this;
	}

	/**
	 * Accepts either an array, or a variable-length list of arguments.
	 * 
	 * @param areas
	 *            The areas to find data for.
	 * @return The modified GetTables object for chaining instructions.
	 */
	public GetTables forAreas(Area... areas) {
		List<Area> args = Arrays.asList(areas);
		this.areas.addAll(args);
		return this;
	}

	/**
	 * Accepts a collection, such as a set (hint hint).
	 * 
	 * @param areas
	 *            The areas to find data for.
	 * @return The modified GetTables object for chaining instructions.
	 */
	public GetTables forAreas(Collection<Area> areas) {
		this.areas.addAll(areas);
		return this;
	}

	/**
	 * Single dataset, sugar really.
	 * 
	 * @param family
	 *            The dataset family to search in.
	 * @return Modified GetTables for chaining instructions.
	 */
	public GetTables inFamily(DataSetFamily family) {
		this.datasetFamilies.add(family);
		return this;
	}

	/**
	 * Accepts an array of families, or a variable argument list.
	 * 
	 * @param families
	 *            The dataset families to search in.
	 * @return Modified GetTables for chaining instructions.
	 */
	public GetTables inFamilies(DataSetFamily... families) {
		List<DataSetFamily> args = Arrays.asList(families);
		this.datasetFamilies.addAll(args);
		return this;
	}

	/**
	 * Accepts a collection of families
	 * 
	 * @param families
	 *            The dataset families to search in.
	 * @return Modified GetTables for chaining instructions.
	 */
	public GetTables inFamilies(Collection<DataSetFamily> families) {
		this.datasetFamilies.addAll(families);
		return this;
	}

	/**
	 * 
	 * @param range
	 *            Date range to limit the results by.
	 * @return Modified GetTables for chaining instructions.
	 */
	public GetTables inDateRange(DateRange range) {
		this.dateRange = range;
		return this;
	}

    /**
     * Execute the method call, supplying the method name
     * @param params a dictionary of parameter names and their values
     * @return an {@link org.xmlpull.v1.XmlPullParser}.
     * @throws IOException
     * @throws XmlPullParserException
     */
	@Override
	protected XmlPullParser execute(Map<String, String> params)
			throws IOException, XmlPullParserException {
		return doCall(METHOD_NAME, params);
	}

    /**
     * Takes the parameters for the method call and puts them into a dictionary
     * @return a dictionary of parameter names and their values
     * @throws InvalidParameterException
     */
	@Override
	protected Map<String, String> collectParams()
			throws InvalidParameterException {
		Map<String, String> params = new HashMap<String, String>();
		collectAreasToParams(params);
		collectDatasetFamiliesToParams(params);
		collectDateRangeToParams(params);
		return params;
	}

    /**
     * Collects areas into the parameter dictionary
     * @param params a dictionary of parameter names and their values
     */
	protected void collectAreasToParams(Map<String, String> params) {
		if (areas.size() > 0) {
			StringBuilder commaSeparatedAreas = new StringBuilder();
			for (Area a : areas) {
				commaSeparatedAreas.append(a.getAreaId()).append(",");
			}
			// Delete the trailing comma.
			commaSeparatedAreas.deleteCharAt(commaSeparatedAreas.length() - 1);
			params.put("Areas", commaSeparatedAreas.toString());
		} else {
			throw new InvalidParameterException("Areas",
					"Please specify at least one Area.");
		}
	}

    /**
     * Collects the date range into the parameter dictionary
     * @param params a dictionary of parameter names and their values
     */
	protected void collectDateRangeToParams(Map<String, String> params) {
		if (dateRange != null) {
			StringBuilder p = new StringBuilder();
			p.append(DateFormat.toNDEDate(dateRange.getStartDate()))
					.append(":")
					.append(DateFormat.toNDEDate(dateRange.getEndDate()));
			params.put("TimePeriod", p.toString());
		}
	}

    /**
     * Collects the dataset families into the parameter dictionary
     * @param params a dictionary of parameter names and their values
     */
	protected void collectDatasetFamiliesToParams(Map<String, String> params) {
		if (datasetFamilies.size() > 0) {
			StringBuilder commaSeparatedDatasets = new StringBuilder();
			for (DataSetFamily d : datasetFamilies) {
				commaSeparatedDatasets.append(d.getFamilyId()).append(",");
			}
			// Delete trailing comma
			commaSeparatedDatasets
					.deleteCharAt(commaSeparatedDatasets.length() - 1);
			params.put("Datasets", commaSeparatedDatasets.toString());
		} else {
			throw new InvalidParameterException("Datasets",
					"Please specify at least one Dataset Family");
		}
	}

    /**
     * Pulls the datasets
     * @return A set of datasets for the specified request.
     * @throws InvalidParameterException
     * @throws IOException
     * @throws XmlPullParserException
     * @throws NDE2Exception
     */
	public Set<Dataset> execute() throws InvalidParameterException,
			IOException, XmlPullParserException, NDE2Exception {
		XmlPullParser xpp = execute(collectParams());

		return processDataCubeResponseElement(xpp);
	}

	/**
	 * Extracted to a separate method because {@link GetChildAreaTables} remote
	 * operation returns the same response element.
	 * 
	 * @param xpp the parser object responsible for the datacube element
	 * @return a processed set of datasets in the datacube element
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws NDE2Exception
	 */
	protected Set<Dataset> processDataCubeResponseElement(XmlPullParser xpp)
			throws XmlPullParserException, IOException, NDE2Exception {
		Set<Dataset> datasets = new HashSet<Dataset>();
		String parent_key = null;
		String previous_key = null;
		int depth = 0;
		String key = null;
		String value = null;
		NDE2Exception error = null;
		Dataset ds = null;

		Set<DataSetItem> dsitems = null;
		int event = xpp.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
				previous_key = key;
				if (xpp.getDepth() > depth) {
					parent_key = previous_key;
					depth = xpp.getDepth();
				} else if (xpp.getDepth() < depth) {
					depth = xpp.getDepth();
				}
				key = xpp.getName();
				if (key.equals("Dataset")) {
                    ds = new Dataset();
                }
				if (key.equals("Topics")) {
                    dstopics = processTopics(xpp);
                }
				if (key.equals("Boundaries")) {
                    dsboundaries = processBoundaries(xpp);
                }
				if (key.equals("Periods")) {
                    dsperiods = processPeriods(xpp);
                }
				if (key.equals("DatasetItems")) {
                    dsitems = processItems(xpp);
                }
				if (key.equals("Error")) {
                    error = new NDE2Exception();
                }
				break;
			case XmlPullParser.TEXT:
				value = xpp.getText();
				boolean in_dataset_metadata = parent_key
						.equals("DatasetMetadata");
				if (key.equals("DatasetCode")) {
                    ds.setFamilyId(Integer.parseInt(value));
                }
				if (in_dataset_metadata) {
					if (key.equals("Creator")) {
                        ds.setCreator(value);
                    }
					if (key.equals("Description")) {
                        ds.setDescription(value);
                    }
					if (key.equals("Subject.Category")) {
                        ds.setSubjectCategory(value);
                    }
					if (key.equals("Title")) {
                        ds.setTitle(value);
                    }
				}
				if (key.equals("message")) {
                    error.setNessMessage(value);
                }
				if (key.equals("detail")) {
                    error.setNessDetail(value);
                }
				break;
			case XmlPullParser.END_TAG:
				String n = xpp.getName();
				if (n.equals("Dataset")) {
					ds.setTopics(dstopics);
					ds.setBoundaries(dsboundaries);
					ds.setPeriods(dsperiods);
					ds.setItems(dsitems);
					datasets.add(ds);
				}
				break;
			}
			event = xpp.next();
		}

		if (error != null) {
            throw error;
        }
		return datasets;
	}

    /**
     * Processes dataset items based on the current call state
     * @param xpp the parser object responsible for the datacube element
     * @return A set of dataset items to attach to the dataset
     * @throws XmlPullParserException
     * @throws IOException
     */
	protected Set<DataSetItem> processItems(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		Set<DataSetItem> items = new HashSet<DataSetItem>();
		String key = null;
		String value = null;
		DataSetItem i = null;
		int event = xpp.getEventType();
		boolean in_items = true;
		while (in_items) {
			switch (event) {
			case XmlPullParser.START_TAG:
				key = xpp.getName();
				if (key.equals("DatasetItem")) {
                    i = new DataSetItem();
                }
				break;
			case XmlPullParser.TEXT:
				value = xpp.getText();
				if (key.equals("TopicId")) {
					Topic t = dstopics.get(Integer.parseInt(value));
					i.setTopic(t);
				}
				if (key.equals("BoundaryId")) {
					Boundary b = dsboundaries.get(Integer.parseInt(value));
					i.setBoundary(b);
				}
				if (key.equals("PeriodId")) {
					Period p = dsperiods.get(Integer.parseInt(value));
					i.setPeriod(p);
				}
				if (key.equals("Value")) {
					float v = Float.parseFloat(value);
					i.setValue(v);
				}
				break;
			case XmlPullParser.END_TAG:
				// Done with ONE item
				if (xpp.getName().equals("DatasetItem")) {
                    items.add(i);
                }
				// Done with ALL of the items
				if (xpp.getName().equals("DatasetItems")) {
                    in_items = false;
                }
				break;
			}
			if (in_items) {
                event = xpp.next();
            }
		}
		return items;
	}

    /**
     * Processes the periods
     * @param xpp the parser object responsible for the datacube element
     * @return periods, referenced by their pointer value.
     * @throws XmlPullParserException
     * @throws IOException
     */
	protected Map<Integer, Period> processPeriods(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		Map<Integer, Period> periods = new HashMap<Integer, Period>();
		String key = null;
		String value = null;
		Period p = null;
		int __p = -1;
		int event = xpp.getEventType();
		boolean in_periods = true;
		while (in_periods) {
			switch (event) {
			case XmlPullParser.START_TAG:
				key = xpp.getName();
				if (key.equals("Period")) {
                    p = new Period();
                }
				break;
			case XmlPullParser.TEXT:
				value = xpp.getText();
				if (key.equals("PeriodId")) {
					__p = Integer.parseInt(value);
					p.set__pointer(__p);
				}
				if (key.equals("Date")) {
					p.setPeriodType(Period.SINGLE_DATE);
					Date d = DateFormat.fromNDEDateOnly(value);
					p.setStartDate(d);
					p.setEndDate(d);
				}
				if (key.equals("Start")) {
					p.setPeriodType(Period.DATE_RANGE);
					p.setStartDate(DateFormat.fromNDEDateOnly(value));
				}
				if (key.equals("End")) {
					p.setEndDate(DateFormat.fromNDEDateOnly(value));
				}
				break;
			case XmlPullParser.END_TAG:
				// Done with ONE period
				if (xpp.getName().equals("Period")) {
                    periods.put(__p, p);
                }
				// Done with ALL of the periods
				if (xpp.getName().equals("Periods")) {
                    in_periods = false;
                }
				break;
			}
			if (in_periods) {
                event = xpp.next();
            }
		}
		return periods;
	}

    /**
     * Processes the boundaries
     * @param xpp the parser object responsible for the datacube element
     * @return boundaries, referenced by their pointer value
     * @throws XmlPullParserException
     * @throws IOException
     */
	protected Map<Integer, Boundary> processBoundaries(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		Map<Integer, Boundary> boundaries = new HashMap<Integer, Boundary>();
		String key = null;
		String value = null;
		Boundary b = null;
		int __p = -1;
		int event = xpp.getEventType();
		boolean in_boundaries = true;
		while (in_boundaries) {
			switch (event) {
			case XmlPullParser.START_TAG:
				key = xpp.getName();
				if (key.equals("Boundary")) {
                    b = new Boundary();
                }
				break;
			case XmlPullParser.TEXT:
				value = xpp.getText();
				if (key.equals("BoundaryId")) {
					__p = Integer.parseInt(value);
					b.set__pointer(__p);
				}
				if (key.equals("BoundaryCode")) {
                    b.setBoundaryCode(value);
                }
				if (key.equals("Coverage.Spatial.Location")) {
                    b.setEnvelope(value);
                }
				if (key.equals("Identifier")) {
                    b.setIdentifier(Integer.parseInt(value));
                }
				if (key.equals("Title")) {
                    b.setTitle(value);
                }
				break;
			case XmlPullParser.END_TAG:
				// Done with ONE boundary
				if (xpp.getName().equals("Boundary")) {
                    boundaries.put(__p, b);
                }
				// Done with ALL of the boundaries
				if (xpp.getName().equals("Boundaries")) {
                    in_boundaries = false;
                }
				break;
			}
			if (in_boundaries) {
                event = xpp.next();
            }
		}
		return boundaries;
	}

    /**
     * Processes the topics
     * @param xpp the parser object responsible for the datacube element
     * @return topics, referenced by their pointer value
     * @throws XmlPullParserException
     * @throws IOException
     */
	protected Map<Integer, Topic> processTopics(XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		Map<Integer, Topic> topics = new HashMap<Integer, Topic>();
		String key = null;
		String value = null;
		Topic t = null;
		int __p = -1;
		int event = xpp.getEventType();
		boolean in_topics = true;
		while (in_topics) {
			switch (event) {
			case XmlPullParser.START_TAG:
				key = xpp.getName();
				if (key.equals("Topic")) {
                    t = new Topic();
                }
				break;
			case XmlPullParser.TEXT:
				value = xpp.getText();
				if (key.equals("TopicId")) {
					__p = Integer.parseInt(value);
					t.set__pointer(__p);
				}
				if (key.equals("TopicCode")) {
                    t.setTopicId(Integer.parseInt(value));
                }
				if (key.equals("Creator")) {
                    t.setCreator(value);
                }
				if (key.equals("Description")) {
                    t.setDescription(value);
                }
				if (key.equals("Identifier")) {
                    t.setDatasetFamilyId(Integer.parseInt(value));
                }
				if (key.equals("Title")) {
                    t.setTitle(value);
                }
				if (key.equals("Coinage.Unit")) {
                    t.setCoinageUnit(value);
                }
				break;
			case XmlPullParser.END_TAG:
				// Done with ONE topic, add to list
				if (xpp.getName().equals("Topic")) {
                    topics.put(__p, t);
                }
				// Done with ALL the topics, return
				if (xpp.getName().equals("Topics")) {
                    in_topics = false;
                }
				break;
			}
			/*
			 * Only advance the parser if the end of the list of topics is not
			 * reached.
			 */
			if (in_topics) {
                event = xpp.next();
            }
		}
		return topics;
	}

	@Override
	public String toURLString() {
		return buildURLString(DELIVERY_ENDPOINT, METHOD_NAME, collectParams());
	}
}
