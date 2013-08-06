package lamparski.areabase;

public class NavDrawerListItemModel {
	private int stringId;
	private int iconId;
	private int fragId;

	/**
	 * @param stringId
	 *            the ID of the string to display
	 * @param iconId
	 *            the ID of the icon to display (ignored with headers)
	 */
	public NavDrawerListItemModel(int stringId, int iconId, int fragId) {
		this.stringId = stringId;
		this.iconId = iconId;
		this.fragId = fragId;
	}

	/**
	 * @return the stringId
	 */
	public int getStringId() {
		return stringId;
	}

	/**
	 * @return the iconId
	 */
	public int getIconId() {
		return iconId;
	}

	/**
	 * @return the fragId
	 */
	public int getFragId() {
		return fragId;
	}

}
