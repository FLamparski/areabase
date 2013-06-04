package nde2.methodcalls.discovery;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class NDE2DiscoNamespaceContext implements NamespaceContext {

	@Override
	public String getNamespaceURI(String prefix) {
		if (prefix == null)
			throw new NullPointerException("Prefix is null.");
		else if (prefix.equals("str"))
			return "http://neighbourhood.statistics.gov.uk/nde/v1-0/discoverystructs";
		else if (prefix.equals("srv"))
			return "http://neighbourhood.statistics.gov.uk/nde/discoveryservice";
		else
			return XMLConstants.NULL_NS_URI;
	}

	@Override
	public String getPrefix(String uri) {
		// Not necessary for XPath
		throw new UnsupportedOperationException(
				"This implementation is for XPath processing only.");
	}

	@Override
	public Iterator getPrefixes(String uri) {
		// Not necessary for XPath
		throw new UnsupportedOperationException(
				"This implementation is for XPath processing only.");
	}

}
