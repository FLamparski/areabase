package nde2.types.discovery;

public class DetailedSubject extends Subject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String description;
	private String moreDescription;

	public DetailedSubject(String name, int id, String desc, String desc_ext) {
		super(name, id);
		this.description = desc;
		this.moreDescription = desc_ext;
	}

	public DetailedSubject(Subject simpleSubject, String desc, String desc_ext) {
		super(simpleSubject);
		this.description = desc;
		this.moreDescription = desc_ext;
	}

	public String getDescription() {
		return description;
	}

	public String getMoreDescription() {
		return moreDescription;
	}

}
