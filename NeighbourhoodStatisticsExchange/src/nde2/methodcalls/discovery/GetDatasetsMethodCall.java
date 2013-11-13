package nde2.methodcalls.discovery;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.types.discovery.Area;
import nde2.types.discovery.DataSetFamily;
import nde2.types.discovery.DateRange;
import nde2.types.discovery.Subject;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <i>Encapsulates the GetDatasets() call to the NDE2 web service. Creation
 * follows the Builder pattern.</i>
 * 
 * <p>
 * This operation enables you to obtain the dataset families associated with
 * your requested subject. The SubjectId will have been obtained from a
 * getSubjects() call. An AreaId is optional, and if supplied, only datasets
 * which have data for this area will be returned.
 * 
 * @author filip
 * 
 */
@Deprecated
public class GetDatasetsMethodCall extends BaseMethodCall {

	private static final String METHOD_NAME = "GetDatasets";

	private long limitingAreaId = 0;
	private int subjectId;

	public GetDatasetsMethodCall addArea(Area area) {
		this.limitingAreaId = area.getAreaId();
		return this;
	}

	public GetDatasetsMethodCall addAreaId(long areaId) {
		this.limitingAreaId = areaId;
		return this;
	}

	public GetDatasetsMethodCall addSubject(Subject subject) {
		this.subjectId = subject.getId();
		return this;
	}

	public GetDatasetsMethodCall addSubjectId(int subjectId) {
		this.subjectId = subjectId;
		return this;
	}

	/**
	 * Executes the remote operation and fetches dataset families for the
	 * specified subject, limited by area (optional).
	 * 
	 * @return
	 * @throws XPathExpressionException
	 *             Thrown when the XPath expressions fail to evaluate.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws NDE2Exception
	 * @throws ParseException
	 * @throws ValueNotAvailable
	 * @see {@link BaseMethodCall} for more information on exceptions thrown.
	 */
	public List<DataSetFamily> getDatasets() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, ParseException, ValueNotAvailable {
		/*
		 * First, create a Dictionary containing parameters to call the remote
		 * method with.
		 */
		Map<String, String> params = new Hashtable<String, String>();
		if (limitingAreaId != 0)
			params.put("AreaId", Long.toString(limitingAreaId));
		params.put("SubjectId", Integer.toString(subjectId));

		/*
		 * Second, call the remote method. Store the response in a Document.
		 * Create an XPath object as well. Note that this Document should be
		 * what is actually expected, as service errors are detected by
		 * doCall_base(), and thrown as Exceptions.
		 */
		Document nessResponse = doCall_base(METHOD_NAME, params);
		XPath xpath = XPathFactory.newInstance().newXPath();

		/*
		 * Prepare the XPath expressions here -- compiling them before
		 * evaluation boosts performance (allegedly?)
		 */
		XPathExpression nameXpathExpr = xpath
				.compile("*[local-name() = 'Name']/text()");
		XPathExpression dsFamIdXpathExpr = xpath
				.compile("*[local-name() = 'DSFamilyId']/text()");
		XPathExpression dateRangeXpathExpr = xpath
				.compile("*[local-name() = 'DateRange']");
		XPathExpression startDateXpathExpr = xpath
				.compile("*[local-name() = 'StartDate']/text()");
		XPathExpression endDateXpathExpr = xpath
				.compile("*[local-name() = 'EndDate']/text()");

		NodeList dsfamilies = (NodeList) xpath.evaluate(
				"//*[local-name() = 'DSFamily']", nessResponse,
				XPathConstants.NODESET);
		ArrayList<DataSetFamily> dataSetFamilies = new ArrayList<DataSetFamily>();
		for (int i = 0; i < dsfamilies.getLength(); i++) {
			Node dsfamily = dsfamilies.item(i);

			String dsname = (String) nameXpathExpr.evaluate(dsfamily,
					XPathConstants.STRING);
			int dsid = ((Double) dsFamIdXpathExpr.evaluate(dsfamily,
					XPathConstants.NUMBER)).intValue();

			NodeList xDateRanges = (NodeList) dateRangeXpathExpr.evaluate(
					dsfamily, XPathConstants.NODESET);
			ArrayList<DateRange> dateRangesList = new ArrayList<DateRange>();
			for (int j = 0; j < xDateRanges.getLength(); j++) {
				Node dateRangeNode = xDateRanges.item(j);
				String startDateString = (String) startDateXpathExpr.evaluate(
						dateRangeNode, XPathConstants.STRING);
				String endDateString = (String) endDateXpathExpr.evaluate(
						dateRangeNode, XPathConstants.STRING);
				DateRange dateRange = new DateRange(startDateString,
						endDateString);
				dateRangesList.add(dateRange);
			}

			DateRange[] dateRanges = dateRangesList.toArray(new DateRange[1]);

			dataSetFamilies.add(new DataSetFamily(dateRanges, dsid, dsname));
		}

		if (!(dataSetFamilies.isEmpty()))
			return dataSetFamilies;
		else
			throw new ValueNotAvailable(
					"No dataset families available for this subject");
	}
}
