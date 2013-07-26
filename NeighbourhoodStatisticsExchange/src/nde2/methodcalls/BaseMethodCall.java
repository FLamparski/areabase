package nde2.methodcalls;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
 * This is where the plumbing for the web service lives. This class should not
 * be inherited directly, try using either
 * {@link nde2.methodcalls.discovery.BaseMethodCall} or
 * {@link nde2.methodcalls.delivery.BaseMethodCall} (though really, at this
 * level of abstraction, this can be used for ANY service with an interface like
 * NDE2.
 * 
 * @author filip
 * 
 */
public abstract class BaseMethodCall {

	/**
	 * Really generic method for wrapping the NDE2 web service (though really,
	 * it may as well be any JAX-WS service, I guess?). Based on parameters
	 * given, it queries the endpoint and returns a {@link Document} that was
	 * returned by the server.
	 * 
	 * @param endpoint
	 *            Base URL for the call (i.e. http://example.com/WebService)
	 * @param method
	 *            Name of the web service method to call
	 * @param params
	 *            Parameters to call the method with
	 * @return A {@link Document} representation of the server's response to the
	 *         method call.
	 * @throws ParserConfigurationException
	 *             When the parser can't be namespace-aware.
	 * @throws SAXException
	 *             When the document at the given URL could be parsed. May
	 *             indicate a server error.
	 * @throws IOException
	 *             When any {@link IOException}s occur when pulling the
	 *             response. May indicate connection problems.
	 * @throws XPathExpressionException
	 *             When the XPath is messed up
	 * @throws NDE2Exception
	 *             When the server signals an error in the query
	 */
	protected Document doCall_base(String endpoint, String method,
			Map<String, String> params) throws ParserConfigurationException,
			SAXException, IOException, XPathExpressionException, NDE2Exception {
		/* Build a URL which for the method call */
		StringBuilder methodCallStrBuilder = new StringBuilder(endpoint);
		methodCallStrBuilder.append(method).append("?");
		Set<Entry<String, String>> paramEntries = params.entrySet();
		for (Entry<String, String> param : paramEntries) {

			methodCallStrBuilder.append(param.getKey() + "="
					+ URLEncoder.encode(param.getValue(), "UTF-8"));
		}

		URL callUrl = new URL(methodCallStrBuilder.toString());
		HttpURLConnection callConnection = (HttpURLConnection) callUrl
				.openConnection();
		InputStream is = callConnection.getInputStream();

		/* Prepare a Document from that URL. The system will pull it nicely. */
		DocumentBuilderFactory docBldFac = DocumentBuilderFactory.newInstance();
		docBldFac.setNamespaceAware(true);
		DocumentBuilder docBld = docBldFac.newDocumentBuilder();
		Document doc = docBld.parse(is);
		callConnection.disconnect();

		XPath xpath = XPathFactory.newInstance().newXPath();

		Node errorNode = (Node) xpath.evaluate("/Error", doc,
				XPathConstants.NODE);
		if (errorNode != null) {
			String errMsg = ((Node) xpath.evaluate("/Error/message", doc,
					XPathConstants.NODE)).getTextContent();
			String errDetail = ((Node) xpath.evaluate("/Error/detail", doc,
					XPathConstants.NODE)).getTextContent();
			throw new NDE2Exception(errMsg, errDetail, 0);
		}

		return doc;
	}

}
