package lamparski.areabase.cards;

import lamparski.areabase.R;
import lamparski.areabase.dummy.mockup_classes.DummyData;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CardView extends LinearLayout {
	/**
	 * Just a little interface to drop in both click and long-press handlers
	 * into the same package.
	 * 
	 * @author filip
	 * 
	 */
	public interface CardCallbacks extends View.OnClickListener,
			View.OnLongClickListener {
		/**
		 * Event handler for the card being clicked (tapped)
		 * 
		 * @param v
		 *            The card being clicked
		 */
		public void onClick(View v);

		/**
		 * Event handler for the card being long-clicked (long-pressed)
		 * 
		 * @param v
		 *            The card being long-clicked
		 */
		public boolean onLongClick(View v);
	}

	private CardCallbacks mCallbacks;

	private Typeface mRobotoCondensed;

	private TextView mCardContent;
	private TextView mCardSubtitle;

	public CardView(Context context, CharSequence cardText, int cardSourceResId) {
		super(context);

		mRobotoCondensed = Typeface.createFromAsset(context.getAssets(),
				"fonts/Roboto-Condensed.ttf");

		mCardContent = new TextView(context);
		mCardContent.setText(cardText);
		addView(mCardContent);

		mCardSubtitle = new TextView(context);
		mCardSubtitle.setText(cardSourceResId);
		mCardSubtitle.setGravity(Gravity.RIGHT);
		mCardSubtitle.setTypeface(mRobotoCondensed);
		addView(mCardSubtitle);

		// default background. override for specific cards.
		setBackgroundResource(R.drawable.card_background);

		setClickable(true); // need to set this to get events
		setLongClickable(true);
	}

	@DummyData(why = "Needed for the editor", replace_with = "Use the constructor CardView(context, cardText, cardSourceResId) instead.")
	public CardView(Context context) {
		super(context);
		if (isInEditMode()) {
			mRobotoCondensed = Typeface.createFromAsset(context.getAssets(),
					"fonts/Roboto-Condensed.ttf");
			mCardContent = new TextView(context);
			mCardContent.setText(Html.fromHtml("Card stuff goes <b>here</b>."));
			addView(mCardContent);
			mCardSubtitle = new TextView(context);
			mCardSubtitle.setText("Card subtitle");
			mCardSubtitle.setGravity(Gravity.RIGHT);
			mCardSubtitle.setTypeface(mRobotoCondensed);
			addView(mCardSubtitle);
			setBackgroundResource(R.drawable.card_background);
		} else {
			Log.e("CardView",
					"Who the hell called the test constructor from runtime?");
			throw new IllegalStateException(
					"This constructor should not be invoked outside of edit mode. Use CardView(context, cardText, cardSourceResId) instead.");
		}
	}

	/**
	 * 
	 * @param callbacks
	 *            Functions handling the card being clicked and long-pressed.
	 */
	public void setCallbacks(CardCallbacks callbacks) {
		mCallbacks = callbacks;
		setOnClickListener(mCallbacks);
		setOnLongClickListener(mCallbacks);
	}

	/**
	 * 
	 * @param text
	 *            New text to be set on the card.
	 */
	public void setText(CharSequence text) {
		mCardContent.setText(text);
	}

	/**
	 * 
	 * @param resid
	 *            Resource ID of new text to be set on the card.
	 */
	public void setText(int resid) {
		mCardContent.setText(resid);
	}

	/**
	 * 
	 * @return Text on the card
	 */
	public CharSequence getText() {
		return mCardContent.getText();
	}

	/**
	 * 
	 * @param text
	 *            New subtitle to be set for the card.
	 */
	public void setSubtitle(CharSequence text) {
		mCardSubtitle.setText(text);
	}

	/**
	 * 
	 * @param resid
	 *            Resource ID of new subtitle for the card.
	 */
	public void setSubtitle(int resid) {
		mCardSubtitle.setText(resid);
	}

	/**
	 * 
	 * @return Card's subtitle text.
	 */
	public CharSequence getSubtitle() {
		return mCardSubtitle.getText();
	}

}
