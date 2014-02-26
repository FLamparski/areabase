package police.types;

public class EngagementMethod {
	private String url;
	private String description;
	private String title;

	@SuppressWarnings("unused")
	private EngagementMethod() {
	}

	/**
	 * @param url
	 * @param description
	 * @param title
	 */
	public EngagementMethod(String url, String description, String title) {
		this.url = url;
		this.description = description;
		this.title = title;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
		if (obj == null) {
            return false;
        }
		if (!(obj instanceof EngagementMethod)) {
            return false;
        }
		EngagementMethod other = (EngagementMethod) obj;
		if (description == null) {
			if (other.description != null) {
                return false;
            }
		} else if (!description.equals(other.description)) {
            return false;
        }
		if (title == null) {
			if (other.title != null) {
                return false;
            }
		} else if (!title.equals(other.title)) {
            return false;
        }
		if (url == null) {
			if (other.url != null) {
                return false;
            }
		} else if (!url.equals(other.url)) {
            return false;
        }
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EngagementMethod [url=" + url + ", description=" + description
				+ ", title=" + title + "]";
	}

}
