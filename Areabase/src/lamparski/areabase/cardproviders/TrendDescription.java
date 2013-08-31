package lamparski.areabase.cardproviders;

public class TrendDescription {
	public static final int FALLING_RAPIDLY = -2;
	public static final int FALLING = -1;
	public static final int STABLE = 0;
	public static final int RISING = 1;
	public static final int RISING_RAPIDLY = 2;

	public int which;
	public float currentValue;
}