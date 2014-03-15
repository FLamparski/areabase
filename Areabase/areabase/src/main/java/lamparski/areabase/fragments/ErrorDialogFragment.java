package lamparski.areabase.fragments;

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
