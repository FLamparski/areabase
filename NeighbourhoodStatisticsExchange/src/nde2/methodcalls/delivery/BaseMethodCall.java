package nde2.methodcalls.delivery;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class BaseMethodCall extends nde2.methodcalls.BaseMethodCall {

	protected static final String ENDPOINT = "http://neighbourhood.statistics.gov.uk/NDE2/Deli/";

	protected Document doCall_base(String method, Map<String, String> params)
			throws ParserConfigurationException, SAXException, IOException {
		return super.doCall_base(ENDPOINT, method, params);
	}

}
