package lamparski.areabase.cardproviders;

import java.io.IOException;

import lamparski.areabase.R;
import lamparski.areabase.cards.PlayCard;
import lamparski.areabase.map_support.HoloCSSColourValues;

import nde2.errors.InvalidParameterException;
import nde2.errors.NDE2Exception;
import nde2.pull.types.Area;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;

import com.fima.cardsui.objects.CardModel;

public class EconomyCardProvider {
	public static final String[] DATASET_KEYWORDS = {
			"Income", // Finds the model median income
			"KS601EW", // Economic activity
			"QS605EW" // Type of economic activity
			};
	
	public static CardModel economyCardForArea(Area area, Resources res)
			throws InvalidParameterException, IOException,
			XmlPullParserException, NDE2Exception, ClassNotFoundException {
		
		String type_of_economic_activity = "Dummy";
		String biggest_sector = "Widgets";
		String avg_income_v_national = res.getStringArray(R.array.card_economy_compare_income_with_national)[2];
		String avg_income_trend = res.getStringArray(R.array.card_economy_income_trend)[2];
		
		String card_title = res.getString(R.string.card_economy_title, area.getName());
		String card_body = res.getString(R.string.card_economy_body_base, area.getName(),
				type_of_economic_activity,
				biggest_sector,
				avg_income_v_national,
				avg_income_trend);
		return makeCard(card_title, card_body);
	}
	
	private static CardModel makeCard(String card_title, String card_description) {
		return new CardModel(card_title, card_description,
				HoloCSSColourValues.GREEN.getCssValue(),
				HoloCSSColourValues.GREEN.getCssValue(), false, false,
				PlayCard.class);
	}
}
