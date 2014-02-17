package lamparski.areabase.cards;

import lamparski.areabase.R;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fima.cardsui.objects.RecyclableCard;

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
