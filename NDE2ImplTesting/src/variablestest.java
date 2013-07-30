import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import nde2.errors.NDE2Exception;
import nde2.errors.ValueNotAvailable;
import nde2.methodcalls.discovery.GetVariablesMethodCall;
import nde2.types.discovery.VariableFamily;

import org.xml.sax.SAXException;

public class variablestest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int fid = 2266;
		List<VariableFamily> variableFamilies = null;
		try {
			variableFamilies = new GetVariablesMethodCall().addDatasetFamilyId(
					fid).getVariables();
		} catch (XPathExpressionException | ParserConfigurationException
				| SAXException | IOException | NDE2Exception | ParseException
				| ValueNotAvailable e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println(String.format("Found %d families.",
				variableFamilies.size()));

		for (VariableFamily variableFamily : variableFamilies) {
			System.out
					.println(String
							.format("Family #%d: %s (Stat unit #%d = %s; Measure unit #%d = %s)",
									variableFamily.getVariableFamilyId(),
									variableFamily.getName(), variableFamily
											.getStatisticalUnit().getUnitId(),
									variableFamily.getStatisticalUnit()
											.getUnitName(), variableFamily
											.getMeasurementUnit().getUnitId(),
									variableFamily.getMeasurementUnit()
											.getUnitName()));
		}
	}

}
