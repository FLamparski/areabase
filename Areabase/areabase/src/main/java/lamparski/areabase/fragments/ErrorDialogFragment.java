package lamparski.areabase.fragments;
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
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Something the framework requires, not even sure what it is...
 */
public class ErrorDialogFragment extends DialogFragment {
	private Dialog mDialog;

	public ErrorDialogFragment() {
		super();
		mDialog = null;
	}

	public void setDialog(Dialog dialog) {
		this.mDialog = dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return mDialog;
	}
}
