package nde2.types.discovery;

/**
 * A more detailed representation of a subject. Has *two* descriptions!
 * 
 * @author filip
 * @see {@link Subject}
 */
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

	/**
	 * 
	 * @return A brief description of the subject
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @return An extended description of the subject
	 */
	public String getMoreDescription() {
		return moreDescription;
	}

}
