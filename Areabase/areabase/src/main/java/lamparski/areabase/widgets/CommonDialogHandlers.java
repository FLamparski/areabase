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
import android.content.DialogInterface;

/**
 * Contains very simple, common dialogue box handlers (such as hiding the dialogue).
 * @author Minkovsky
 *
 */
public interface CommonDialogHandlers {
	/**
	 * A shortcut to the dialog.dismiss() callback.
	 */
	public static final DialogInterface.OnClickListener JUST_DISMISS = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	};
}
