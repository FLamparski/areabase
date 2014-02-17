package lamparski.areabase.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoLightTextView extends TextView {

	public RobotoLightTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupFont();
	}

	public RobotoLightTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupFont();
	}

	public RobotoLightTextView(Context context) {
		super(context);
		setupFont();
	}

	private void setupFont() {
		if (!isInEditMode()) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				setTypeface(Typeface
						.create("sans-serif-light", Typeface.NORMAL));
			} else {
				this.setTypeface(Typeface.createFromAsset(this.getContext()
						.getAssets(), "fonts/Roboto-Thin.ttf"));
			}
		} else {
			setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
		}
		setTypeface(getTypeface(), Typeface.NORMAL);
	}
}
