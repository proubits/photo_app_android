package app.camera.android;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import app.camera.android.CameraBaseActivity.PhotoUploadHandler;

/**
 * This is the camera activity where you can take a photo and automatically uploaded to web server
 * @author webmstr
 *
 */
public class TelstraActivity extends CameraBaseActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.telstra);
		// cameraButton = (ImageButton) findViewById(R.id.cameraButton);
		// hide text and image view for photo
		findViewById(R.id.telstra_textView2).setVisibility(View.INVISIBLE);
		findViewById(R.id.telstra_imageView1).setVisibility(View.INVISIBLE);
		findViewById(R.id.checkPhotoButton).setVisibility(View.INVISIBLE);
		setupUIHandler();
		addListenerOnButtons();
	}

	protected Activity getContext() {
		return TelstraActivity.this;
	}

	protected void showPhoto() {
		int vis = findViewById(R.id.telstra_textView2).getVisibility();
		if (vis == View.VISIBLE) {
			findViewById(R.id.telstra_textView2).setVisibility(View.INVISIBLE);
			findViewById(R.id.telstra_imageView1).setVisibility(View.INVISIBLE);
			findViewById(R.id.telstra_textView1).setVisibility(View.VISIBLE);
			findViewById(R.id.takePhotoButton).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.telstra_textView1).setVisibility(View.GONE);
			findViewById(R.id.takePhotoButton).setVisibility(View.GONE);
			findViewById(R.id.telstra_textView2).setVisibility(View.VISIBLE);
			findViewById(R.id.telstra_imageView1).setVisibility(View.VISIBLE);
			findViewById(R.id.checkPhotoButton).setVisibility(View.VISIBLE);
		}
	}

	 protected Button getCheckPhotoButton(){
	 return (Button) findViewById(R.id.checkPhotoButton);
	 }
	protected ImageButton getTakePhotoButton() {
		return (ImageButton) findViewById(R.id.takePhotoButton);
	}

	protected ImageView getPhotoView() {
		return (ImageView) findViewById(R.id.telstra_imageView1);
	}

	public static void main(String[] args) {
		try {
			TelstraActivity act = new TelstraActivity();
			File file = new File("c:/temp/photo.jpg");
			DataInputStream dis = new DataInputStream(new FileInputStream(file));
			int size = (int) file.length();
			photo_bytes = new byte[size];
			dis.read(photo_bytes);
			file = new File("c:/temp/photo.jpg");
			size = (int) file.length();
			DataInputStream dis1 = new DataInputStream(
					new FileInputStream(file));
			thumb_bytes = new byte[size];
			dis1.read(thumb_bytes);
			dis.close();
			dis1.close();
			new Thread(act.new PhotoUploadHandler(photo_bytes, thumb_bytes,
					null, null)).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
