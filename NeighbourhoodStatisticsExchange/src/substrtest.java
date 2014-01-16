import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class substrtest {
	public static final Pattern ECONOMY_SECTOR_DECLUTTER_PATTERN = Pattern.compile("^[A-Z](\\d+\\W+)*\\W?");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] stuff = { "A Agriculture, Forestry and Fishing",
				"C10-12 Manufacturing; Food, Beverages and Tobacco",
				"C16,17 Manufacturing; Wood, Paper and Paper Products",
				"C18, 31, 32 Manufacturing; Other",
				"R,S Arts, Entertainment and Recreation; Other Service Activities"
		};
		for(String str : stuff){
				// ^[A-Z](\d+\W+)*\W?
				
				String sector = str.replaceAll("^[A-Z](,[A-Z])?(\\d+\\W+)*\\W?", "");
				System.out.println(sector);
		}
	}

}
