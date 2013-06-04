package lamparski.areabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import lamparski.areabase.map_support.OSMapWebViewClient;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

public class SummaryFragment extends SherlockFragment {

	private WebView mOpenSpaceView;

	public SummaryFragment() {
		super();
	}

	@SuppressLint("SetJavaScriptEnabled")
	// ^ This is to tell ADK that I know what I'm doing
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View theView = inflater.inflate(R.layout.fragment_summary, container,
				false);

		mOpenSpaceView = (WebView) theView
				.findViewById(R.id.summary_map_webview);
		String htmlData = getSlippymapFromAssets();
		if (htmlData != null) {
			OSMapWebViewClient wvclient = new OSMapWebViewClient();

			// Alert creation etc. -- keep collapsed for clarity
			// I'm sorry if you have to read this on paper...
			wvclient.setOnIllegalURLListener(new OSMapWebViewClient.OnIllegalURLListener() {
				/*
				 * So here's how it works: Theoretically, a malicious program
				 * may want to inject some code that would cause the WebView to
				 * navigate away from the slippymap and to a site pretending to
				 * be the app's UI element, requiring payment. In order to
				 * mitigate that, URLs that are not local (file:///) or OS API's
				 * are gutted and a dialogue warning the user is presented
				 * instead. Now, the user still has the option to visit the
				 * page, but in the system browser.
				 */
				@Override
				public void onIllegalUrl(final String url) {
					AlertDialog.Builder alert_bldr = new AlertDialog.Builder(
							getActivity());
					alert_bldr
							.setTitle(R.string.error_illegal_url_title)
							.setMessage(
									getResources()
											.getString(
													R.string.error_illegal_url_formatstring,
													url))
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setPositiveButton(
									R.string.error_illegal_url_response_dontgo,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									})
							.setNegativeButton(
									R.string.error_illegal_url_response_open,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Uri uri = Uri.parse(url);
											Intent intent = new Intent(
													Intent.ACTION_VIEW, uri);
											startActivity(intent);
										}
									});
					alert_bldr.show();
				}
			});

			mOpenSpaceView.setWebViewClient(wvclient);
			// Now this line should not cause many problems.
			mOpenSpaceView.getSettings().setJavaScriptEnabled(true);
			mOpenSpaceView.loadDataWithBaseURL("file:///android_asset/images",
					htmlData, "text/html", "UTF-8", null);

		} else {
			Toast errt = new Toast(getActivity());
			errt.setText(R.string.toast_mapview_not_loading);
			errt.setDuration(Toast.LENGTH_SHORT);
			errt.show();
			// Can't show the map? Don't waste space.
			// This collapses the WebView.
			mOpenSpaceView.setVisibility(View.GONE);
		}

		return theView;
	}

	private String getSlippymapFromAssets() {
		InputStream is;
		StringBuilder htmlBuilder = new StringBuilder();

		try {
			is = getActivity().getAssets().open("os_openspace/slippymap.html");
			if (is != null) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				String line;
				while ((line = reader.readLine()) != null) {
					htmlBuilder.append(line);
				}
			}
		} catch (IOException e) {
			Log.e("SummaryFragment", "Error loading slippymap from assets", e);
		}

		return htmlBuilder.toString();
	}

}
