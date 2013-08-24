package lamparski.areabase.cards;

import lamparski.areabase.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fima.cardsui.objects.Card;

public class BasicCard extends Card {

	/**
	 * @param title
	 * @param desc
	 */
	public BasicCard(String title, String desc) {
		super(title, desc);
		// TODO Auto-generated constructor stub
		// setBackgroundResource(R.drawable.card);
	}

	public BasicCard() {
	}

	@Override
	public View getCardContent(Context context) {
		View contentView = ((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.card_ex, null);

		((TextView) contentView.findViewById(R.id.card_basic_title))
				.setText(title);
		((TextView) contentView.findViewById(R.id.card_basic_text))
				.setText(desc);

		return contentView;
	}

}
