package nde2.types.discovery;

import org.w3c.dom.Node;

public class DetailedArea extends Area {

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

	public String getExtCode() {
		return extCode;
	}

	public String getEnvelope() {
		return envelope;
	}

	public Node getMandatoryMetadata() {
		return mandatoryMetadata;
	}

	public Node getOptionalMetadata() {
		return optionalMetadata;
	}

}
