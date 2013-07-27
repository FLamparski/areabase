package nde2.methodcalls.delivery;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nde2.errors.NDE2Exception;
import nde2.types.delivery.Boundary;
import nde2.types.delivery.DataSetItem;
import nde2.types.delivery.Dataset;
import nde2.types.delivery.Period;
import nde2.types.delivery.Topic;
import nde2.types.discovery.Area;
import nde2.types.discovery.DataSetFamiliy;
import nde2.types.discovery.DateRange;
import nde2.types.discovery.VariableFamily;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <i>This method call wraps the NDE2 getTables REST call.</i>
 * 
 * <p>
 * This operation is a simplified version of getDataCube which it encapsulates.
 * As well as making it easier for client applications to construct the request,
 * this simplification is an essential precursor to offering a RESTful version
 * of the service.
 * <p>
 * A list of one or more {@link Area}s must be supplied.
 * <p>
 * All other parameters are optional, but a {@link NullPointerException} will be
 * thrown unless one or both of datasets and variables are supplied. If datasets
 * but not variables are supplied, it is assumed that all the variables are
 * wanted for these datasets.
 * <p>
 * If no time period is given, then the latest data is returned, you get only
 * the variables still in use by the latest version of the dataset, and no
 * discontinued ones.
 * 
 * @author filip
 * 
 */
public class GetTablesMethodCall extends BaseMethodCall {
	private final static String METHOD_NAME = "getTables";
	private List<Area> areas;
	private List<DataSetFamiliy> dsFamilies;
	private List<VariableFamily> variableFamilies;
	private DateRange timePeriod;
	private ArrayList<Dataset> datasets;

	public GetTablesMethodCall addAreas(List<Area> areas) {
		this.areas = areas;
		return this;
	}

	public GetTablesMethodCall addDatasetFamilies(
			List<DataSetFamiliy> dsFamilies) {
		this.dsFamilies = dsFamilies;
		return this;
	}

	public GetTablesMethodCall addVariableFamilies(
			List<VariableFamily> varFamilies) {
		this.variableFamilies = varFamilies;
		return this;
	}

	public GetTablesMethodCall addTimePeriod(DateRange timePeriod) {
		this.timePeriod = timePeriod;
		return this;
	}

	/**
	 * Calls the NDE2 Delivery service, requesting tables for the areas, dataset
	 * families or variable families specified in this
	 * {@link GetTablesMethodCall}.
	 * <p>
	 * <i>Note:</i> This method is fairly long, and it connects to a remote
	 * host. It may take a while. There is nothing I can do to make it less
	 * painful. You should use it asynchronously.
	 * 
	 * @return A list of tables, represented by {@link Dataset Datasets}, that
	 *         contain the requested data.
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws NDE2Exception
	 *             Thrown if the server returns an error-type response
	 * @throws NullPointerException
	 *             Thrown if there are no {@link DataSetFamiliy DataSetFamilies}
	 *             or {@link VariableFamily VariableFamilies} specified. You
	 *             should include at least one {@link DataSetFamiliy}.
	 * @throws ParseException
	 */
	public List<Dataset> getTables() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, NullPointerException, ParseException {
		/*
		 * Validate arguments for the remote method call, and build a parameter
		 * list.
		 */
		Map<String, String> params = new HashMap<String, String>();
		if (areas != null) {
			StringBuilder areaListBuilder = new StringBuilder();
			for (Area area : areas) {
				// Not bothering about trailing commas - the server just ignores
				// them
				areaListBuilder.append(Long.toString(area.getAreaId())).append(
						",");
			}
			params.put("Areas", areaListBuilder.toString());
		} else {
			// MUST have an area here.
			throw new NullPointerException("Must supply at least one Area!");
		}
		if (dsFamilies != null) {
			StringBuilder dsFamilyListBuilder = new StringBuilder();
			for (DataSetFamiliy dsfam : dsFamilies) {
				// Not bothering about trailing commas - the server just ignores
				// them
				dsFamilyListBuilder.append(
						Integer.toString(dsfam.getFamilyId())).append(",");
			}
			params.put("Datasets", dsFamilyListBuilder.toString());
		}
		if (variableFamilies != null) {
			StringBuilder varFamilyListBuilder = new StringBuilder();
			for (VariableFamily varfam : variableFamilies) {
				// Not bothering about trailing commas - the server just ignores
				// them
				varFamilyListBuilder.append(
						Integer.toString(varfam.getVariableFamilyId())).append(
						",");
			}
			params.put("Variables", varFamilyListBuilder.toString());
		} else if (dsFamilies == null) {
			// MUST have at least one VariableFamily or DataSetFamily
			throw new NullPointerException(
					"Must supply at least one DataSetFamily or at least one VariableFamily or both.");
		}
		if (timePeriod != null) {
			SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			StringBuilder timePeriodBuilder = new StringBuilder()
					.append(mDateFormat.format(timePeriod.getStartDate()))
					.append(":")
					.append(mDateFormat.format(timePeriod.getEndDate()));
			params.put("TimePeriod", timePeriodBuilder.toString());
		}

		/*
		 * Send off the request. We are live!
		 */
		Document response = doCall_base(METHOD_NAME, params);
		XPath xpath = XPathFactory.newInstance().newXPath();

		/*
		 * Let's get the result ball rolling. For the reasons that will be made
		 * clear in processDataset, this *has* to be accessible throughout the
		 * object.
		 */
		datasets = new ArrayList<Dataset>();

		/*
		 * The dataset elements returned as part of the response are fairly
		 * complex, and so they will be processed separately by a helper method.
		 * This is just to get them out of the file.
		 */
		NodeList datasetNodeList = (NodeList) xpath.evaluate(
				"//*[local-name() = 'Dataset']", response,
				XPathConstants.NODESET);
		for (int i = 0; i < datasetNodeList.getLength(); i++) {
			datasets.add(processDataset(datasetNodeList.item(i), xpath));
		}

		return datasets;
	}

	/**
	 * Processes a Dataset element from the NDE2 response and returns an
	 * internal representation. This is a long method.
	 * 
	 * @param datasetElement
	 *            The &lt;Dataset> Node to use.
	 * @param xpath
	 *            The XPath to use. GetTablesMethodCall promotes reusable
	 *            XPaths.
	 * @return A {@link Dataset} that represents the dataset from ONS.
	 * @throws XPathExpressionException
	 * @throws ParseException
	 *             If dates in the response were not formatted properly.
	 */
	protected Dataset processDataset(Node datasetElement, XPath xpath)
			throws XPathExpressionException, ParseException {
		/*
		 * Process dataset metadata -- its code, creator, title, etc.
		 */
		int dsCode = (Integer) xpath
				.evaluate(
						"*[local-name() = 'DatasetDetails']/*[local-name() = 'DatasetCode']/number()",
						datasetElement, XPathConstants.NUMBER);
		String dsCreator = (String) xpath
				.evaluate(
						"*[local-name() = 'DatasetDetails']/*[local-name() = 'DatasetMetadata']/*[local-name() = 'Creator']/text()",
						datasetElement, XPathConstants.STRING);
		String dsTitle = (String) xpath
				.evaluate(
						"*[local-name() = 'DatasetDetails']/*[local-name() = 'DatasetMetadata']/*[local-name() = 'Title']/text()",
						datasetElement, XPathConstants.STRING);
		String dsDescription = (String) xpath
				.evaluate(
						"*[local-name() = 'DatasetDetails']/*[local-name() = 'DatasetMetadata']/*[local-name() = 'Description']/text()",
						datasetElement, XPathConstants.STRING);
		String dsSubjectCategory = (String) xpath
				.evaluate(
						"*[local-name() = 'DatasetDetails']/*[local-name() = 'DatasetMetadata']/*[local-name() = 'Subject.Category']/text()",
						datasetElement, XPathConstants.STRING);

		/*
		 * Process dataset topics. For some reason, topic IDs are used
		 * consistently within datasets, so one dataset item won't have a topic
		 * that's from an entirely different dataset, BUT: the topic IDs are
		 * also assigned globally, so if one dataset has topic IDs 1-30, the
		 * next dataset will have topic IDs 31+. Also, topic IDs have a 1-based
		 * index because apparently the thing was coded in fucking Pascal.
		 */

		int topicIdBase = 0;
		if (datasets.size() > 0) {
			Dataset lastDataset = datasets.get(datasets.size() - 1);
			topicIdBase = lastDataset.getTopics().size();
		}

		NodeList topicElements = (NodeList) xpath.evaluate(
				"*[local-name() = 'Topics']/*[local-name() = 'Topic']",
				datasetElement, XPathConstants.NODESET);
		ArrayList<Topic> dsTopics = new ArrayList<Topic>();
		for (int i = 0; i < topicElements.getLength(); i++) {
			dsTopics.add(processTopic(topicElements.item(i), xpath, topicIdBase));
		}

		/*
		 * Process boundaries (many per dataset). This is inconsistent with
		 * topics, as boundary IDs DO reset in new datasets. So if topics were
		 * [Topic 1-13],[Topic 14-24], then boundaries are... [1,2][1,2].
		 */
		NodeList boundaryElements = (NodeList) xpath.evaluate(
				"*[local-name() = 'Boundaries']/*[local-name() = 'Boundary']",
				datasetElement, XPathConstants.NODESET);
		ArrayList<Boundary> dsBoundaries = new ArrayList<Boundary>();
		for (int i = 0; i < boundaryElements.getLength(); i++) {
			dsBoundaries.add(processBoundary(boundaryElements.item(i), xpath));
		}

		/*
		 * Process time periods (usually one per dataset, nay, request, but the
		 * spec makes it look like there could be more so let's play it safe).
		 */
		NodeList periodElements = (NodeList) xpath.evaluate(
				"*[local-name() = 'Periods']/*[local-name() = 'Period']",
				datasetElement, XPathConstants.NODESET);
		ArrayList<Period> dsPeriods = new ArrayList<Period>();
		for (int i = 0; i < periodElements.getLength(); i++) {
			dsPeriods.add(processPeriod(periodElements.item(i), xpath));
		}

		/*
		 * And finally, process the dataset values themselves. This is where the
		 * inconsistency when numbering Topics comes back to bite us.
		 */
		NodeList valueElements = (NodeList) xpath
				.evaluate(
						"*[local-name() = 'DatasetItems']/*[local-name() = 'DatasetItem']",
						datasetElement, XPathConstants.NODESET);
		ArrayList<DataSetItem> dsValues = new ArrayList<DataSetItem>();
		for (int i = 0; i < valueElements.getLength(); i++) {
			dsValues.add(processDatasetItem(valueElements.item(i), xpath,
					topicIdBase, dsTopics, dsBoundaries, dsPeriods));
		}

		/*
		 * The product: a Dataset.
		 */
		return new Dataset(dsCode, dsCreator, dsDescription, dsSubjectCategory,
				dsTitle, dsTopics, dsPeriods, dsBoundaries, dsValues);
	}

	/**
	 * Processes a DataSetItem (value) element and returns it internal
	 * representation
	 * 
	 * @param datasetItemElement
	 *            The &lt;DataSetItem> Node to process
	 * @param xpath
	 *            The XPath to use. GetTablesMethodCall promotes reusable
	 *            XPaths.
	 * @param topicIdBase
	 *            This is to fix the Topic ordering inconsistency
	 * @param dsTopics
	 *            For the Topic reference
	 * @param dsBoundaries
	 *            For the Boundary reference
	 * @param dsPeriods
	 *            For the Period reference
	 * @return A single {@link DataSetItem}.
	 * @throws XPathExpressionException
	 */
	protected DataSetItem processDatasetItem(Node datasetItemElement,
			XPath xpath, int topicIdBase, ArrayList<Topic> dsTopics,
			ArrayList<Boundary> dsBoundaries, ArrayList<Period> dsPeriods)
			throws XPathExpressionException {
		/*
		 * Get all the fields for this value/DataSetItem
		 */
		int topicId = (Integer) xpath.evaluate(
				"*[local-name() = 'TopicId']/number()", datasetItemElement,
				XPathConstants.NUMBER);
		int boundaryId = (Integer) xpath.evaluate(
				"*[local-name() = 'BoundaryId']/number()", datasetItemElement,
				XPathConstants.NUMBER);
		int periodId = (Integer) xpath.evaluate(
				"*[local-name() = 'TopicId']/number()", datasetItemElement,
				XPathConstants.NUMBER);
		int value = (Integer) xpath.evaluate(
				"*[local-name() = 'Value']/number()", datasetItemElement,
				XPathConstants.NUMBER);
		/*
		 * Now, fix field references so that they are zero-based
		 */
		topicId = topicId - topicIdBase - 1;
		boundaryId -= 1;
		periodId -= 1;

		/*
		 * Get the actual objects that these values are referencing
		 */
		Topic refTopic = dsTopics.get(topicId);
		Boundary refBoundary = dsBoundaries.get(boundaryId);
		Period refPeriod = dsPeriods.get(periodId);

		/*
		 * Return the value
		 */
		return new DataSetItem(refTopic, refBoundary, refPeriod, value);
	}

	/**
	 * Processes a Period element within one Dataset and returns its internal
	 * representation.
	 * 
	 * @param periodElement
	 *            The &lt;Period> element to be processed.
	 * @param xpath
	 *            The XPath to use. GetTablesMethodCall promotes reusable
	 *            XPaths.
	 * @return A {@link Period} that represents the NDE2 Period.
	 * @throws XPathExpressionException
	 * @throws ParseException
	 *             This means that the server has returned one of the dates in
	 *             the wrong format.
	 */
	protected Period processPeriod(Node periodElement, XPath xpath)
			throws XPathExpressionException, ParseException {
		/*
		 * Get all the fields for this Period
		 */
		int periodId = (Integer) xpath.evaluate(
				"*[local-name() = 'PeriodId']/number()", periodElement,
				XPathConstants.NUMBER);
		String periodStartDateStr = (String) xpath.evaluate(
				"*[local-name() = 'Start']/text()", periodElement,
				XPathConstants.STRING);
		String periodEndDateStr = (String) xpath.evaluate(
				"*[local-name() = 'End']/text()", periodElement,
				XPathConstants.STRING);
		/*
		 * Return a Period
		 */
		return new Period(periodStartDateStr, periodEndDateStr, periodId - 1);
	}

	/**
	 * Processes a Boundary element within one Dataset and returns its internal
	 * representation.
	 * 
	 * @param boundaryElement
	 *            The Node that represents currently-processed &lt;Boundary>
	 *            element.
	 * @param xpath
	 *            The XPath to use when processing this element.
	 *            GetTablesMethodCall promotes reusable XPaths.
	 * @return A {@link Boundary} that represents this the NDE2 Boundary.
	 * @throws XPathExpressionException
	 */
	protected Boundary processBoundary(Node boundaryElement, XPath xpath)
			throws XPathExpressionException {
		/*
		 * Get all the fields for this Boundary
		 */
		String bcode = (String) xpath.evaluate(
				"*[local-name() = 'BoundaryCode']/text()", boundaryElement,
				XPathConstants.STRING);
		String benvelope = (String) xpath
				.evaluate(
						"*[local-name() = 'BoundaryMetadata']/*[local-name() = 'Coverage.Spatial.Location']/text()",
						boundaryElement, XPathConstants.STRING);
		String bcreator = (String) xpath
				.evaluate(
						"*[local-name() = 'BoundaryMetadata']/*[local-name() = 'Creator']/text()",
						boundaryElement, XPathConstants.STRING);
		String btitle = (String) xpath
				.evaluate(
						"*[local-name() = 'BoundaryMetadata']/*[local-name() = 'Title']/text()",
						boundaryElement, XPathConstants.STRING);
		int bidentifier = (Integer) xpath
				.evaluate(
						"*[local-name() = 'BoundaryMetadata']/*[local-name() = 'Identifier']/number()",
						boundaryElement, XPathConstants.NUMBER);
		int bid = (Integer) xpath.evaluate(
				"*[local-name() = 'BoundaryId']/number()", boundaryElement,
				XPathConstants.NUMBER);

		/*
		 * Return a Boundary
		 */
		return new Boundary(bcode, benvelope, bcreator, bidentifier, bid - 1,
				btitle);
	}

	/**
	 * Processes a Topic element within one Dataset and returns its internal
	 * representation. It also fixes some of the <s>WTF</s> "interesting"
	 * properties of the NDE indexing of Topics.
	 * 
	 * @param topicElement
	 *            The Node that represents currently-processed &lt;Topic>
	 *            element.
	 * @param xpath
	 *            The XPath to use when processing this element.
	 *            GetTablesMethodCall promotes reusable XPaths.
	 * @param topicIdBase
	 *            The length of the Topics list of the previous dataset, or 0 if
	 *            this is the first dataset being processed. <blockquote>Process
	 *            dataset topics. For some reason, topic IDs are used
	 *            consistently within datasets, so one dataset item won't have a
	 *            topic that's from an entirely different dataset, BUT: the
	 *            topic IDs are also assigned globally, so if one dataset has
	 *            topic IDs 1-30, the next dataset will have topic IDs 31+.
	 *            Also, topic IDs have a 1-based index because apparently the
	 *            thing was coded in ruddy Pascal. <br/>
	 *            <cite>--Comment on Topics, inside
	 *            {@link GetTablesMethodCall#processDataset(Node, XPath)}
	 *            </cite></blockquote>
	 * @return A {@link Topic} that represents the NDE2 Topic, adjusted for use
	 *         in this API.
	 * @throws XPathExpressionException
	 */
	protected Topic processTopic(Node topicElement, XPath xpath, int topicIdBase)
			throws XPathExpressionException {
		/*
		 * Get all the fields for this Topic
		 */
		int topicId = (Integer) xpath.evaluate(
				"*[local-name() = 'TopicId']/number()", topicElement,
				XPathConstants.NUMBER);
		int topicCode = (Integer) xpath.evaluate(
				"*[local-name() = 'TopicCode']/number()", topicElement,
				XPathConstants.NUMBER);
		int topicIdentifier = (Integer) xpath
				.evaluate(
						"*[local-name() = 'TopicMetadata']/*[local-name() = 'Identifier']/number()",
						topicElement, XPathConstants.NUMBER);
		String topicCreator = (String) xpath
				.evaluate(
						"*[local-name() = 'TopicMetadata']/*[local-name() = 'Creator']/text()",
						topicElement, XPathConstants.STRING);
		String topicTitle = (String) xpath
				.evaluate(
						"*[local-name() = 'TopicMetadata']/*[local-name() = 'Title']/text()",
						topicElement, XPathConstants.STRING);
		String topicDescription = (String) xpath
				.evaluate(
						"*[local-name() = 'TopicMetadata']/*[local-name() = 'Description']/text()",
						topicElement, XPathConstants.STRING);
		String topicCoinageUnit = (String) xpath
				.evaluate(
						"*[local-name() = 'TopicMetadata']/*[local-name() = 'Coinage.Unit']/text()",
						topicElement, XPathConstants.STRING);
		/*
		 * Now, we need to do some maths on the topicId to make it a zero-based
		 * index and so that each separate dataset has topics whose IDs start
		 * from 0. If this is the first dataset being processed, topicIdBase
		 * will be 0, which will mean that the first topic will have topic = 1;
		 * thus topicId = 1 - 0 - 1 => 0, and so on. For the next dataset
		 * processed, topicIdBase will be the length of previous dataset's topic
		 * list. So if that length is 33, then: topicId = 34; topicId = 34 - 33
		 * - 1 => 0.
		 */
		topicId = topicId - topicIdBase - 1;

		/*
		 * And finally, we return a single topic.
		 */
		return new Topic(topicId, topicCode, topicIdentifier, topicCreator,
				topicDescription, topicTitle, topicCoinageUnit);
	}
}
