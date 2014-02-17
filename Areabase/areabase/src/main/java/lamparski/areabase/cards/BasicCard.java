package lamparski.areabase.cards;

import lamparski.areabase.R;
import android.view.View;
import android.widget.TextView;

import com.fima.cardsui.objects.RecyclableCard;

public class BasicCard extends RecyclableCard {

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
	protected void applyTo(View convertCardView) {
		((TextView) convertCardView.findViewById(R.id.card_basic_title))
				.setText(title);
		((TextView) convertCardView.findViewById(R.id.card_basic_text))
				.setText(desc);
	}

	@Override
	protected int getCardLayoutId() {
		return R.layout.card_ex;
	}

}
