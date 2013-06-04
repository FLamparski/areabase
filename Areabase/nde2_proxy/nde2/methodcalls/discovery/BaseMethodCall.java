package nde2.methodcalls.discovery;

import java.io.IOException;
import java.util.Dictionary;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nde2.errors.NDE2Exception;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * This class specifies the NeSS/NDE2 Discovery endpoint of the web service and
 * provides a base method for interacting with it.
 * 
 * @author filip
 * 
 */
public abstract class BaseMethodCall extends nde2.methodcalls.BaseMethodCall {

	protected static final String ENDPOINT = "http://neighbourhood.statistics.gov.uk/NDE2/Disco/";

	/**
	 * This method uses {@link nde2.methodcalls.BaseMethodCall}
	 * {@code .doCall_base()} to call the required method on the server, and
	 * then checks for service errors. Actual method calls to the NeSS API can,
	 * therefore, simply supply this function with required parameters and
	 * expect a valid response.
	 * 
	 * @param method
	 *            NeSS/NDE2 method to be called
	 * @param params
	 *            Parameters of the NeSS/NDE2 method
	 * @return A {@link Document} representation of the response, which is
	 *         unlikely to contain errors.
	 * @throws ParserConfigurationException
	 *             See {@link nde2.methodcalls.BaseMethodCall}
	 * @throws SAXException
	 *             See {@link nde2.methodcalls.BaseMethodCall}
	 * @throws IOException
	 *             See {@link nde2.methodcalls.BaseMethodCall}
	 * @throws XPathExpressionException
	 *             Thrown if XPath fails
	 * @throws NDE2Exception
	 *             Thrown if the server returned an &lt;Error> instead of
	 *             expected result
	 */
	protected Document doCall_base(String method,
			Dictionary<String, String> params)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException, NDE2Exception {
		Document response = super.doCall_base(ENDPOINT, method, params);

		XPath xpath = XPathFactory.newInstance().newXPath();

		Node errorNode = (Node) xpath.evaluate("/Error", response,
				XPathConstants.NODE);
		if (errorNode != null) {
			String errMsg = ((Node) xpath.evaluate("/Error/message", response,
					XPathConstants.NODE)).getTextContent();
			String errDetail = ((Node) xpath.evaluate("/Error/detail",
					response, XPathConstants.NODE)).getTextContent();
			throw new NDE2Exception(errMsg, errDetail, 0);
		}

		return response;
	}
}
