package lamparski.areabase.widgets;

import lamparski.areabase.R;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;

public class CommonDialogs {
	/**
	 * Notifies the user about service cock-ups.
	 * 
	 * @param name
	 *            The name of the incompetent service.
	 */
	public static void serviceCockupNotify(ComponentName name, Context ctx) {
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
}
