package lamparski.areabase.widgets;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;

import lamparski.areabase.R;

public class CommonDialogs {
	/**
	 * Notifies the user about service cock-ups.
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
