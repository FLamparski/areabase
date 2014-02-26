package lamparski.areabase.cards;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fima.cardsui.objects.RecyclableCard;

import lamparski.areabase.R;

public class PlayCard extends RecyclableCard {

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
	}

	public PlayCard() {
	}

	@Override
	protected void applyTo(View playCardView) {
		((TextView) playCardView.findViewById(R.id.card_play_title))
				.setText(titlePlay);
		((TextView) playCardView.findViewById(R.id.card_play_title))
				.setTextColor(Color.parseColor(titleColor));

		((TextView) playCardView.findViewById(R.id.card_play_text))
				.setText(description);

		playCardView.findViewById(R.id.card_play_stripe).setBackgroundColor(
				Color.parseColor(color));

		if (isClickable) {
            ((LinearLayout) playCardView.findViewById(R.id.contentLayout))
                    .setBackgroundResource(R.drawable.selectable_background_cardbank);
        }

		if (hasOverflow) {
            ((ImageView) playCardView.findViewById(R.id.card_play_overflow))
                    .setVisibility(View.VISIBLE);
        } else {
            ((ImageView) playCardView.findViewById(R.id.card_play_overflow))
                    .setVisibility(View.GONE);
        }
	}

	@Override
	protected int getCardLayoutId() {
		return R.layout.card_play;
	}
}
