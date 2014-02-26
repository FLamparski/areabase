package police.types;

import java.util.Collection;

public class ForceInformation {
	private String description;
	private String url;
	private String telephone;
	private String id;
	private String name;
	private Collection<EngagementMethod> engagement_methods;

	@SuppressWarnings("unused")
	private ForceInformation() {
	}

	/**
	 * @param description
	 * @param url
	 * @param telephone
	 * @param id
	 * @param name
	 * @param engagement_methods
	 */
	public ForceInformation(String description, String url, String telephone,
			String id, String name,
			Collection<EngagementMethod> engagement_methods) {
		this.description = description;
		this.url = url;
		this.telephone = telephone;
		this.id = id;
		this.name = name;
		this.engagement_methods = engagement_methods;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the engagement_methods
	 */
	public Collection<EngagementMethod> getEngagement_methods() {
		return engagement_methods;
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
		result = prime
				* result
				+ ((engagement_methods == null) ? 0 : engagement_methods
						.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((telephone == null) ? 0 : telephone.hashCode());
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
		if (!(obj instanceof ForceInformation)) {
            return false;
        }
		ForceInformation other = (ForceInformation) obj;
		if (description == null) {
			if (other.description != null) {
                return false;
            }
		} else if (!description.equals(other.description)) {
            return false;
        }
		if (engagement_methods == null) {
			if (other.engagement_methods != null) {
                return false;
            }
		} else if (!engagement_methods.equals(other.engagement_methods)) {
            return false;
        }
		if (id == null) {
			if (other.id != null) {
                return false;
            }
		} else if (!id.equals(other.id)) {
            return false;
        }
		if (name == null) {
			if (other.name != null) {
                return false;
            }
		} else if (!name.equals(other.name)) {
            return false;
        }
		if (telephone == null) {
			if (other.telephone != null) {
                return false;
            }
		} else if (!telephone.equals(other.telephone)) {
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
		return "ForceInformation [description=" + description + ", url=" + url
				+ ", telephone=" + telephone + ", id=" + id + ", name=" + name
				+ ", engagement_methods=" + engagement_methods + "]";
	}

}
