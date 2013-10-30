package nde2.types.discovery;

import org.w3c.dom.Node;

/**
 * This class of objects represents UK's administrative and geographical areas
 * in more detail. You want this in order to get the OS-grid encoded envelope
 * for this area if you want to use maps.
 * 
 * @author filip
 * @see {@link Area}
 */
@Deprecated
public class DetailedArea extends Area {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String extCode;
	private String envelope;
	private Node mandatoryMetadata;
	private Node optionalMetadata;

	public DetailedArea(String name, long areaId, int levelTypeId,
			int hierarchyId, String extCode, String envelope,
			Node mandatoryMetadata, Node optionalMetadata) {
		super(name, areaId, levelTypeId, hierarchyId);
		this.envelope = envelope;
		this.extCode = extCode;
		this.mandatoryMetadata = mandatoryMetadata;
		this.optionalMetadata = optionalMetadata;
	}

	public DetailedArea(Area area, String extCode, String envelope,
			Node mandatoryMetadata, Node optionalMetadata) {
		super(area);
		this.envelope = envelope;
		this.extCode = extCode;
		this.mandatoryMetadata = mandatoryMetadata;
		this.optionalMetadata = optionalMetadata;
	}

	/**
	 * 
	 * @return The SNAC code for this area
	 */
	public String getExtCode() {
		return extCode;
	}

	/**
	 * This is not a polygon wrapping the area by its borders; there's an
	 * OpenSpace API for that. This is just a rectangle that is just big enough
	 * to contain this area.
	 * 
	 * @return Ordnance Survey easting/northing envelope of this area.
	 */
	public String getEnvelope() {
		return envelope;
	}

	/**
	 * 
	 * @return The "mandatory" metadata for this area.
	 */
	public Node getMandatoryMetadata() {
		return mandatoryMetadata;
	}

	/**
	 * 
	 * @return The "optional" metadata for this area.
	 */
	public Node getOptionalMetadata() {
		return optionalMetadata;
	}

}
