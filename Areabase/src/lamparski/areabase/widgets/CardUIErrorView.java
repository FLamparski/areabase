package lamparski.areabase.widgets;

import lamparski.areabase.R;
import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CardUIErrorView extends LinearLayout {
	
	private TextView mErrorTitle, mErrorMsg;
	private ImageView mIcon;
	
	private int errorTitleId, errorMsgId, iconId;
	
	public CardUIErrorView(Context context) {
		super(context);
		addView(inflate(context, R.layout.cardsui_error_placeholder_view, null));
	}

	public CardUIErrorView(Context context, int errorTitleId, int errorMsgId, int iconId) {
		super(context);
		addView(inflate(context, R.layout.cardsui_error_placeholder_view, null));
		setErrorTitleId(errorTitleId);
		setErrorMsgId(errorMsgId);
		setIconId(iconId);
	}

	public int getErrorTitleId() {
		return errorTitleId;
	}

	public void setErrorTitleId(int errorTitleId) {
		this.errorTitleId = errorTitleId;
		mErrorTitle.setText(errorTitleId);
	}

	public int getErrorMsgId() {
		return errorMsgId;
	}

	public void setErrorMsgId(int errorMsgId) {
		this.errorMsgId = errorMsgId;
		mErrorMsg.setText(errorMsgId);
	}

	public int getIconId() {
		return iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
		mIcon.setImageResource(iconId);
	}
	
	

}
