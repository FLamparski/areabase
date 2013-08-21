package lamparski.areabase.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.fima.cardsui.views.CardUI;

/**
 * A {@link CardUI} that is nice enough to not hog incoming touch events that
 * don't go to actual cards, and pass them to the view below it.
 * 
 * @author filip
 * 
 */
public class ClickthroughCardUI extends CardUI {

	private View viewBelow;

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ClickthroughCardUI(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ClickthroughCardUI(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
	}

	/**
	 * @param context
	 */
	public ClickthroughCardUI(Context context) {
		super(context);
		setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
	}

	/**
	 * @return the viewBelow
	 */
	public View getViewBelow() {
		return viewBelow;
	}

	/**
	 * @param viewBelow
	 *            the viewBelow to set
	 */
	public void setViewBelow(View viewBelow) {
		this.viewBelow = viewBelow;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		viewBelow.onTouchEvent(event);
		return true;
	}
}
