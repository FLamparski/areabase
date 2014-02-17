package lamparski.areabase.map_support;

public enum HoloCSSColourValues {
	CYAN("#33B5E5"), VIOLET("#AA66CC"), LIME("#99CC00"), YELLOW("#FFBB33"), PINK(
			"#FF4444"), AQUAMARINE("#0099CC"), PURPLE("#9933CC"), GREEN(
			"#669900"), ORANGE("#FF8800"), RED("#CC0000");

	private String cssColorValue;

	private HoloCSSColourValues(String cssValue) {
		this.cssColorValue = cssValue;
	}

	public String getCssValue() {
		return cssColorValue;
	}
}
