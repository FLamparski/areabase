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
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;

import lamparski.areabase.R;

public class CommonDialogs {
	/**
	 * Notifies the user about service disconnect errors.
	 * 
	 * @param name
	 *            The name of the incompetent service.
	 */
	public static void serviceDisconnectAlert(ComponentName name, Context ctx) {
		new AlertDialog.Builder(ctx)
				.setTitle(
						R.string.summaryactivity_cardmaker_servicedisconnect_title)
				.setMessage(
						ctx.getResources()
								.getString(
										R.string.summaryactivity_cardmaker_servicedisconnect_message,
										name))
				.setNeutralButton(android.R.string.ok,
						CommonDialogHandlers.JUST_DISMISS).show();
	}

    public static void areaDataServiceError(Throwable err, Context ctx) {
        new AlertDialog.Builder(ctx)
                .setTitle(
                        R.string.error_cannot_fetch_area_data)
                .setMessage(
                        ctx.getResources()
                                .getString(
                                        R.string.error_cannot_fetch_area_data_body, err.toString()))
                .setNeutralButton(android.R.string.ok,
                        CommonDialogHandlers.JUST_DISMISS).show();
    }
}
