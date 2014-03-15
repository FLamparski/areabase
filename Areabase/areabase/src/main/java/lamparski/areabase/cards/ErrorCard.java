package lamparski.areabase.cards;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fima.cardsui.objects.RecyclableCard;

import lamparski.areabase.R;

/**
 * An attempt at user-friendly error messages.
 *
 * Of course it doesn't work all the time, but hey.
 */
public class ErrorCard extends RecyclableCard {

	@Override
	protected void applyTo(View convertView) {
		((ImageView) convertView
				.findViewById(R.id.cardsui_error_placeholder_icon))
				.setImageResource(this.imageRes);
		((TextView) convertView
				.findViewById(R.id.cardsui_error_placeholder_title))
				.setText(this.titlePlay);
		((TextView) convertView
				.findViewById(R.id.cardsui_error_placeholder_msg))
				.setText(this.description);
	}

	@Override
	protected int getCardLayoutId() {
		return R.layout.card_error;
	}

}
