package lamparski.areabase.widgets;

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
