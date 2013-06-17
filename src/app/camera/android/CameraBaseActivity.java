package app.camera.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
//import android.app.AlertDialog;
//import android.content.DialogInterface;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
//import android.util.Base64;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import app.camera.util.FilePoster;

public abstract class CameraBaseActivity extends Activity {
	UIHandler uiHandler;
	Uri mImageUri;
	// ImageButton telstraButton, optusButton;
	// protected ImageButton cameraButton;
	// SplashScreen splash;
	private ProgressDialog progressDialog;

	protected abstract Activity getContext();

	protected abstract void showPhoto();

	protected abstract Button getCheckPhotoButton();

	protected abstract ImageButton getTakePhotoButton();

	protected abstract ImageView getPhotoView();

	public static byte[] photo_bytes, thumb_bytes;
	private static final String TAG = "CameraBaseActivity";
	public static final String WEBSITE = "ozpromotion18.heroku.com";

	protected void setupUIHandler() {

		HandlerThread uiThread = new HandlerThread("UIHandler");
		uiThread.start();
		uiHandler = new UIHandler(uiThread.getLooper());
	}

	protected void addListenerOnButtons() {
		// Button button = (Button) findViewById(R.id.button1);
		getTakePhotoButton().setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// showMessage("Take a photo.");
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				// intent.putExtra( MediaStore.EXTRA_OUTPUT, Uri.fromFile( new
				// File( mTmpFilePath ) ) );
				// intent.putExtra( "filename", mTmpFilePath );
				File photo = null;
				try {
					// place where to store camera taken picture
					photo = createTemporaryFile("picture", ".jpg");
					photo.delete();
					mImageUri = Uri.fromFile(photo);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
					startActivityForResult(intent, 0);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					// Log.v(TAG, "Can't create file to take picture!");
					Toast.makeText(getApplicationContext(),
							"Please check SD card! Image shot is impossible!",
							10000);
					// return false;
				}
			}
		});

		getCheckPhotoButton().setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				Intent browserIntent = new Intent(
//						Intent.ACTION_VIEW,
//						Uri.parse("http://cold-stream-6758.heroku.com/photos?taker="
//								+ getDeviceId()));
//				startActivity(browserIntent);

				Intent myIntent = new Intent(getContext(), PhotoWebView.class);
				myIntent.putExtra("deviceId", getDeviceId());
				getContext().startActivity(myIntent);
			}
		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_CANCELED) {
			showMessage("Camera cancelled.");
			Log.i(TAG, "Camera cancelled.");
			return;
		}

		if (resultCode == RESULT_OK) {
			System.out.println("Camera result is OK");
			Log.i(TAG, "Camera result is OK");
			afterPhotoTaken(data);
		} else {
			System.out.println("Camera failed.");
			Log.i(TAG, "Camera failed.");
		}
	}

	private void afterPhotoTaken(Intent data) {
		try {
			// Bitmap bitmap = (Bitmap) data.getExtras().get("data");
			// this.getContentResolver().notifyChange(mImageUri, null);
			ContentResolver cr = this.getContentResolver();
			Bitmap bitmap = null, tbitmap=null, pbitmap=null;
			final ByteArrayOutputStream bao = new ByteArrayOutputStream();
			final ByteArrayOutputStream tbao = new ByteArrayOutputStream();

			bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr,
					mImageUri);
			// Bitmap tbitmap = (Bitmap) data.getExtras().get("data");
			// tbitmap.compress(Bitmap.CompressFormat.JPEG, 90, tbao);
			String timestamp = Long.toString(System.currentTimeMillis());
			MediaStore.Images.Media.insertImage(cr, bitmap, timestamp,
					timestamp);
			pbitmap = Bitmap.createScaledBitmap(bitmap,
					(int) (bitmap.getWidth() / 3),
					(int) (bitmap.getHeight() / 3), false);
			getPhotoView().setImageBitmap(pbitmap);
			
			bitmap = Bitmap.createScaledBitmap(bitmap,
					(int) (bitmap.getWidth() / 2),
					(int) (bitmap.getHeight() / 2), false);
			tbitmap = Bitmap.createScaledBitmap(bitmap,
					(int) (bitmap.getWidth() / 7),
					(int) (bitmap.getHeight() / 7), false);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
			tbitmap.compress(Bitmap.CompressFormat.JPEG, 90, tbao);
			showPhoto();

			final byte[] ba = bao.toByteArray();
			final byte[] tba = tbao.toByteArray();
			System.out.println("About to show progress dialog.");
			Log.i(TAG, "About to show progress dialog.");
			progressDialog = ProgressDialog.show(getContext(),
					"Uploading the photo...", "Please wait...", true, false);
			System.out.println("About to post image.");
			Log.i(TAG, "About to post image.");
			new Thread(new PhotoUploadHandler(ba, tba, bao, tbao)).start();
		} catch (Exception e) {
			// showMessage(e.getMessage());
			// Log.i(TAG, e.getMessage());
			// System.out.println(e.getMessage());
			if (e != null)
				e.printStackTrace();
		}
	}

	private File createTemporaryFile(String part, String ext) throws Exception {
		File tempDir = Environment.getExternalStorageDirectory();
		// System.out.println(tempDir.toString());
		// tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
		// System.out.println(tempDir.toString());
		// if(!tempDir.exists())
		// {
		// tempDir.mkdir();
		// System.out.println("dir created. "+tempDir.toString());
		// }
		File temp = File.createTempFile(part, ext, tempDir);
		System.out.println("temp file created = " + temp.toString());
		return temp;
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

	private void showMessage(String message) {
		Message msg = uiHandler.obtainMessage(UIHandler.DISPLAY_UI_TOAST);
		msg.obj = message;
		uiHandler.sendMessage(msg);
	}

	private void showSplashScreen() {
		uiHandler.sendEmptyMessage(UIHandler.DISPLAY_UI_SPLASH);
	}

	private void closeProgressDialog() {
		uiHandler.sendEmptyMessage(UIHandler.CLOSE_UI_DIALOG);
	}

	private final class UIHandler extends Handler {
		public static final int DISPLAY_UI_TOAST = 0;
		public static final int CLOSE_UI_DIALOG = 1;
		public static final int DISPLAY_UI_SPLASH = 2;

		public UIHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UIHandler.DISPLAY_UI_TOAST: {
				Context context = getApplicationContext();
				Toast t = Toast.makeText(context, (String) msg.obj,
						Toast.LENGTH_LONG);
				t.show();
			}
			case UIHandler.CLOSE_UI_DIALOG:
				if (progressDialog != null)
					progressDialog.dismiss();
				break;
			case UIHandler.DISPLAY_UI_SPLASH:
				SplashScreen splash = new SplashScreen();
				Intent i = new Intent();
				Context context = getApplicationContext();
				i.setClass(context, SplashScreen.class);
				startActivity(i);
				break;
			default:
				break;
			}
		}
	}

	protected final class PhotoUploadHandler implements Runnable {
		private byte[] ba, tba;
		private ByteArrayOutputStream bao, tbao;

		public PhotoUploadHandler(byte[] photo, byte[] thumb,
				ByteArrayOutputStream ibao, ByteArrayOutputStream itbao) {
			ba = photo;
			tba = thumb;
			bao = ibao;
			tbao = itbao;
		}

		public void run() {

			try {
				//String website = getResources().getString(R.string.website);
				String resp = FilePoster.doPost(WEBSITE, ba, tba, getPhoneId());
				if (bao != null)
					bao.close();
				if (tbao != null)
					tbao.close();
				closeProgressDialog();
				String msg = "Photo uploaded!\nTo check your photos, please press the check photos button.";
				showMessage(msg);
				//System.out.println(resp);
				// showCheckPhotoButton();
			} catch (IOException ioe) {
				Log.i(TAG, ioe.getMessage());
				ioe.printStackTrace();
				showMessage(ioe.getMessage());
			}
		}
	}
}
