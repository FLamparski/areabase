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
import android.widget.TextView;

import com.fima.cardsui.objects.RecyclableCard;

import lamparski.areabase.R;

public class BasicCard extends RecyclableCard {

	/**
	 * @param title title of the card
	 * @param desc text on the card
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
