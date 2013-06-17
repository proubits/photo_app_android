package app.camera.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
//import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.VideoView;

//import android.webkit.WebChromeClient.CustomViewCallback;
/**
 * Browse your uploaded photos in this activity
 * @author webmstr
 *
 */
public class PhotoWebView extends Activity {
	WebView mWebView;
	private static final String TAG = "PhotoWebView";
	private ProgressDialog progressBar;
	private AlertDialog alertDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		Intent intent = getIntent();
		String deviceId = intent.getStringExtra("deviceId");
		System.out.println("deviceId=" + deviceId);

		alertDialog = new AlertDialog.Builder(this).create();

		progressBar = ProgressDialog.show(PhotoWebView.this, "Loading photos",
				"Please be patient...");

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.setWebViewClient(new HelloWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setPluginsEnabled(true);
		mWebView.getSettings().setAllowFileAccess(true);
		String url = "http://" + CameraBaseActivity.WEBSITE + "/photos?taker_id=" + deviceId;
		mWebView.loadUrl(url);
		//mWebView.loadUrl("file:///android_asset/demo.html");
	}

	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			progressBar = ProgressDialog.show(PhotoWebView.this,
					"Loading photo", "Please be patient...");
			view.loadUrl(url);
			return true;
		}

		public void onPageFinished(WebView view, String url) {
			Log.i(TAG, "Finished loading URL: " + url);
			if (progressBar.isShowing()) {
				progressBar.dismiss();
			}
		}

		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Log.e(TAG, "Error: " + description);
			// Toast.makeText(activity, "Oh no! " + description,
			// Toast.LENGTH_SHORT).show();
			alertDialog.setTitle("Error");
			alertDialog.setMessage(description);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			alertDialog.show();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}

/*
 * VideoView Rotation
 * http://stackoverflow.com/questions/3569280/android-videoview
 * -landscape-orientation-problem Hello WebView
 * http://developer.android.com/guide/tutorials/views/hello-webview.html
 * http://www.mkyong.com/android/android-webview-example/ start activity
 * http://stackoverflow
 * .com/questions/4186021/how-to-start-a-new-activity-when-click-on-button
 * progress loading http://www.chrisdanielson.com/tag/webviewclient/
 */
