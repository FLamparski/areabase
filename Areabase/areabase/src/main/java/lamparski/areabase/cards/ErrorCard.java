package lamparski.areabase.cards;
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
