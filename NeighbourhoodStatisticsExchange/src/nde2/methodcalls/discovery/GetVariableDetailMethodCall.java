package nde2.methodcalls.discovery;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nde2.errors.NDE2Exception;
import nde2.types.discovery.DetailedVariableFamily;
import nde2.types.discovery.Periodicity;
import nde2.types.discovery.VariableFamily;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class GetVariableDetailMethodCall extends BaseMethodCall {
	private final String METHOD_NAME = "GetVariableDetail";
	private VariableFamily varFamily;

	public GetVariableDetailMethodCall addVariable(VariableFamily varFamily) {
		this.varFamily = varFamily;
		return this;
	}

	public DetailedVariableFamily getVariableDetail()
			throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException, NDE2Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("VarFamilyId",
				Integer.toString(varFamily.getVariableFamilyId()));

		Document nessResponse = doCall_base(METHOD_NAME, params);
		XPath xpath = XPathFactory.newInstance().newXPath();

		String vFamDesc = (String) xpath
				.evaluate(
						"/*[local-name() = 'VariableDetail']/*[local-name() = 'OptionalMetadata']/text()",
						nessResponse, XPathConstants.STRING);

		int vFamPeriodId = (Integer) xpath
				.evaluate(
						"//*[local-name() = 'Periodicity']/*[local-name() = 'PeriodicityId']/number()",
						nessResponse, XPathConstants.NUMBER);
		String vFamPeriodDesc = (String) xpath
				.evaluate(
						"//*[local-name() = 'Periodicity']/*[local-name() = 'MandatoryMetaData']/text()",
						nessResponse, XPathConstants.STRING);
		String vFamPeriodExtDesc = (String) xpath
				.evaluate(
						"//*[local-name() = 'Periodicity']/*[local-name() = 'OptionalMetaData']/text()",
						nessResponse, XPathConstants.STRING);
		String vFamPeriodName = (String) xpath
				.evaluate(
						"//*[local-name() = 'Periodicity']/*[local-name() = 'Name']/text()",
						nessResponse, XPathConstants.STRING);

		return new DetailedVariableFamily(varFamily, vFamDesc,
				new Periodicity(vFamPeriodName, vFamPeriodId, vFamPeriodDesc,
						vFamPeriodExtDesc));
	}
}
