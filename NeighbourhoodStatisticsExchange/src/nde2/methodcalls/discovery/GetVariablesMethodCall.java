package nde2.methodcalls.discovery;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import nde2.types.MeasurementUnit;
import nde2.types.StatisticalUnit;
import nde2.types.discovery.DataSetFamiliy;
import nde2.types.discovery.DateRange;
import nde2.types.discovery.VariableFamily;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GetVariablesMethodCall extends BaseMethodCall {
	private final String METHOD_NAME = "GetVariables";
	private int dsFamilyId;
	private DateRange dateRange;

	public GetVariablesMethodCall() {
		dsFamilyId = -1;
		dateRange = null;
	}

	public GetVariablesMethodCall addDatasetFamily(DataSetFamiliy dsFamily) {
		this.dsFamilyId = dsFamily.getFamilyId();
		return this;
	}

	public GetVariablesMethodCall addDatasetFamilyId(int dsFamilyId) {
		this.dsFamilyId = dsFamilyId;
		return this;
	}

	public GetVariablesMethodCall addDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
		return this;
	}

	public List<VariableFamily> getVariables() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception, ParseException, ValueNotAvailable {
		/*
		 * First, create a Dictionary containing parameters to call the remote
		 * method with.
		 */
		Map<String, String> params = new Hashtable<String, String>();
		if (dateRange != null)
			params.put("DateRange", formatDateRangeForCall());
		params.put("DSFamilyId", Integer.toString(dsFamilyId));

		/*
		 * Second, call the remote method. Store the response in a Document.
		 * Create an XPath object as well. Note that this Document should be
		 * what is actually expected, as service errors are detected by
		 * doCall_base(), and thrown as Exceptions.
		 */
		Document nessResponse = doCall_base(METHOD_NAME, params);
		XPath xpath = XPathFactory.newInstance().newXPath();

		NodeList varFamilyNodes = (NodeList) xpath.evaluate(
				"//*[local-name() = 'VarFamily']", nessResponse,
				XPathConstants.NODESET);
		XPathExpression varFamIdExpr = xpath
				.compile("*[local-name() = 'VarFamilyId']/text()");
		XPathExpression sUnitExpr = xpath
				.compile("*[local-name() = 'StatisticalUnit']");
		XPathExpression mUnitExpr = xpath
				.compile("*[local-name() = 'MeasurementUnit']");
		XPathExpression dateRangeExpr = xpath
				.compile("*[local-name() = 'DateRange']");
		XPathExpression nameExpr = xpath
				.compile("*[local-name() = 'Name']/text()");
		XPathExpression mUIdExpr = xpath
				.compile("*[local-name() = 'MUId']/text()");
		XPathExpression sUIdExpr = xpath
				.compile("*[local-name() = 'SUId']/text()");
		XPathExpression startDateExpr = xpath
				.compile("*[local-name() = 'StartDate']/text()");
		XPathExpression endDateExpr = xpath
				.compile("*[local-name() = 'EndDate']/text()");

		List<VariableFamily> variableFamilies = new ArrayList<VariableFamily>();

		for (int i = 0; i < varFamilyNodes.getLength(); i++) {
			Node varFamilyNode = varFamilyNodes.item(i);
			int fam_id = ((Double) varFamIdExpr.evaluate(varFamilyNode,
					XPathConstants.NUMBER)).intValue();
			String fam_name = (String) nameExpr.evaluate(varFamilyNode,
					XPathConstants.STRING);

			Node statUnitNode = (Node) sUnitExpr.evaluate(varFamilyNode,
					XPathConstants.NODE);
			int suid = ((Double) sUIdExpr.evaluate(statUnitNode,
					XPathConstants.NUMBER)).intValue();
			String suname = (String) nameExpr.evaluate(statUnitNode,
					XPathConstants.STRING);
			StatisticalUnit fam_sunit = new StatisticalUnit();
			fam_sunit.setUnitId(suid);
			fam_sunit.setUnitName(suname);

			Node measureUnitNode = (Node) mUnitExpr.evaluate(varFamilyNode,
					XPathConstants.NODE);
			int muid = ((Double) mUIdExpr.evaluate(measureUnitNode,
					XPathConstants.NUMBER)).intValue();
			String muname = (String) nameExpr.evaluate(measureUnitNode,
					XPathConstants.STRING);
			MeasurementUnit fam_munit = new MeasurementUnit();
			fam_munit.setUnitId(muid);
			fam_munit.setUnitName(muname);

			NodeList dateRangeNodes = (NodeList) dateRangeExpr.evaluate(
					varFamilyNode, XPathConstants.NODESET);
			ArrayList<DateRange> dateRangesList = new ArrayList<DateRange>();
			for (int j = 0; j < dateRangeNodes.getLength(); j++) {
				Node dateRangeNode = dateRangeNodes.item(j);
				String startDateString = (String) startDateExpr.evaluate(
						dateRangeNode, XPathConstants.STRING);
				String endDateString = (String) endDateExpr.evaluate(
						dateRangeNode, XPathConstants.STRING);
				DateRange dateRange = new DateRange(startDateString,
						endDateString);
				dateRangesList.add(dateRange);
			}

			DateRange[] dateRanges = dateRangesList.toArray(new DateRange[1]);

			VariableFamily varFam = new VariableFamily(fam_id, fam_name,
					dateRanges, fam_sunit, fam_munit);
			variableFamilies.add(varFam);
		}
		if (!(variableFamilies.isEmpty()))
			return variableFamilies;
		else
			throw new ValueNotAvailable(
					"No variables available for this query.");
	}

	private String formatDateRangeForCall() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
		String startDateFormatted = simpleDateFormat.format(dateRange
				.getStartDate());
		String endDateFormatted = simpleDateFormat.format(dateRange
				.getEndDate());
		return (startDateFormatted + ":" + endDateFormatted);
	}

}
