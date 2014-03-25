package lamparski.areabase.widgets;
/** !license-block 
    This file is part of Areabase.

    Areabase is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Areabase is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Areabase.  If not, see <http://www.gnu.org/licenses/>.

    Areabase (C) 2013-2014 Filip Wieland <filiplamparski@gmail.com>
*/
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * A special flavour of TextView which can load up a custom typeface.
 */
public class RobotoLightTextView extends TextView {

    @SuppressWarnings("unused")
	public RobotoLightTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupFont();
	}

    @SuppressWarnings("unused")
	public RobotoLightTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupFont();
	}

    @SuppressWarnings("unused")
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
