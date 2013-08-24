package lamparski.areabase.cards;

import lamparski.areabase.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fima.cardsui.objects.Card;

public class PlayCard extends Card {

	/**
	 * @param titlePlay
	 * @param description
	 * @param color
	 * @param titleColor
	 * @param hasOverflow
	 * @param isClickable
	 */
	public PlayCard(String titlePlay, String description, String color,
			String titleColor, Boolean hasOverflow, Boolean isClickable) {
		super(titlePlay, description, color, titleColor, hasOverflow,
				isClickable);
		// TODO Auto-generated constructor stub
	}

	public PlayCard() {
	}

	@Override
	public View getCardContent(Context context) {
		View playCardView = ((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.card_play, null);

		((TextView) playCardView.findViewById(R.id.card_play_title))
				.setText(titlePlay);
		((TextView) playCardView.findViewById(R.id.card_play_text))
				.setText(description);
		playCardView.findViewById(R.id.card_play_stripe).setBackgroundColor(
				Color.parseColor(color));

		if (isClickable == true)
			((LinearLayout) playCardView.findViewById(R.id.contentLayout))
					.setBackgroundResource(R.drawable.selectable_background_cardbank);

		if (hasOverflow == true)
			((ImageView) playCardView.findViewById(R.id.card_play_overflow))
					.setVisibility(View.VISIBLE);
		else
			((ImageView) playCardView.findViewById(R.id.card_play_overflow))
					.setVisibility(View.GONE);

		return playCardView;
	}
}
