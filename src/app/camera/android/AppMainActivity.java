package app.camera.android;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * This is the app entry point.
 * Click the "Web Promote" button to start browser activity to view your uploaded photos;
 * Click the "Telstra" button to start camera activity to take a photo.
 * @author webmstr
 *
 */
public class AppMainActivity extends Activity {
	//ImageButton telstraButton, optusButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		addListenerOnButton();
	}

	public void addListenerOnButton() {	
		final Context context = this;
		//telstraButton = (ImageButton) findViewById(R.id.telstraButton); 
		findViewById(R.id.telstraButton).setOnClickListener(new OnClickListener() { 
			public void onClick(View arg) { 
			   //start telstra promote activity
				 Intent intent = new Intent(context, TelstraActivity.class);
                 startActivity(intent);   
			} 
		}); 
		findViewById(R.id.promButton).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(AppMainActivity.this, PhotoWebView.class);
				myIntent.putExtra("deviceId", getDeviceId());
				AppMainActivity.this.startActivity(myIntent);
			}
		});
	}


	private String getPhoneId() {
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getDeviceId() + "-"
				+ mTelephonyMgr.getLine1Number();
	}

	private String getDeviceId() {
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getDeviceId();
	}
	
}
