package nde2.methodcalls.discovery;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nde2.errors.NDE2Exception;
import nde2.types.discovery.Area;
import nde2.types.discovery.DetailedArea;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * <i>Encapsulates the GetAreaDetail() call to the NDE2 web service. Creation
 * follows the Builder pattern.</i>
 * 
 * <p>
 * This is a simple operation which gives you extra information about an area,
 * by supplying its AreaId. This AreaId is internal to the NeSS datastore and
 * must have been previously obtained via other discovery calls.
 * <p>
 * In the returned information, the Envelope can be used in a GIS system – it is
 * the four corners of the map of the area MinX:MinY:MaxX:MaxY - these numbers
 * being Ordnance Survey Eastings and Northings. The ExtCode is the standard
 * SNAC code for the area. There may be metadata for the area or nil elements.
 * 
 * @author filip
 * 
 */
public class GetAreaDetailMethodCall extends BaseMethodCall {

	private static final String METHOD_NAME = "GetAreaDetail";

	private Area basicArea;

	public GetAreaDetailMethodCall() {
		basicArea = null;
	}

	public GetAreaDetailMethodCall addArea(Area basicArea) {
		this.basicArea = basicArea;
		return this;
	}

	/**
	 * This method will take data supplied to this
	 * {@link GetAreaDetailMethodCall} and call the web service. It will then
	 * parse the result, or thrown an Exception if there is a problem.
	 * 
	 * @return A {@link DetailedArea} corresponding to the supplied Area.
	 * @throws XPathExpressionException
	 *             Thrown when the XPath expressions fail to evaluate.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws NDE2Exception
	 * @see {@link BaseMethodCall} for more information about the exceptions
	 *      thrown.
	 */
	public DetailedArea getAreaDetail() throws XPathExpressionException,
			ParserConfigurationException, SAXException, IOException,
			NDE2Exception {
		/*
		 * First, create a Dictionary containing parameters to call the remote
		 * method with.
		 */
		Map<String, String> params = new Hashtable<String, String>();
		params.put("AreaId", Long.toString(basicArea.getAreaId()));

		/*
		 * Second, call the remote method. Store the response in a Document.
		 * Create an XPath object as well. Note that this Document should be
		 * what is actually expected, as service errors are detected by
		 * doCall_base(), and thrown as Exceptions.
		 */
		Document nessResponse = doCall_base(METHOD_NAME, params);
		XPath xpath = XPathFactory.newInstance().newXPath();

		String envelope = ((Node) xpath.evaluate(
				"//*[local-name() = 'Envelope']", nessResponse,
				XPathConstants.NODE)).getTextContent();
		String extCode = ((Node) xpath.evaluate(
				"//*[local-name() = 'ExtCode']", nessResponse,
				XPathConstants.NODE)).getTextContent();
		Node mandatoryMetadata = (Node) xpath.evaluate(
				"//*[local-name() = 'MandatoryMetaData']", nessResponse,
				XPathConstants.NODE);
		Node optionalMetadata = (Node) xpath.evaluate(
				"//*[local-name() = 'OptionalMetaData']", nessResponse,
				XPathConstants.NODE);
		return new DetailedArea(basicArea, extCode, envelope,
				mandatoryMetadata, optionalMetadata);
	}
}
