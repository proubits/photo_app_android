package app.camera.util;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

/**
 * Post the photo file to web server.
 * @author webmstr
 * TODO: find out location id from MyLocation and post to server
 */
public class FilePoster {

	public static String doPost(String website, byte[] ba, byte[] tba, String phoneId) {
		
		String resp = "";
		System.out.println("file size is " + ba.length);
		String ba1 = Base64.encodeBytes(ba);
		String tba1 = null;
		if (tba != null)
			tba1 = Base64.encodeBytes(tba);

		ArrayList<NameValuePair> nameValuePairs = new

		ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("image", ba1));
		if (tba1 != null)
			nameValuePairs.add(new BasicNameValuePair("thumb", tba1));
		nameValuePairs.add(new BasicNameValuePair("taker", phoneId));
		nameValuePairs.add(new BasicNameValuePair("gps", "?"));
		//nameValuePairs.add(new BasicNameValuePair("tk", "1"));
		//nameValuePairs.add(new BasicNameValuePair("ts", "1"));
		nameValuePairs.add(new BasicNameValuePair("size", "" + ba.length));
		InputStream is = null;

		try {

			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httppost = new

			HttpPost("http://" + website + "/photos.xml");

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();

			is = entity.getContent();
			java.io.BufferedInputStream bis = new java.io.BufferedInputStream(
					is);
			int c = bis.read();
			while (c != -1) {
				System.out.print((char) c);
				resp += (char) c;
				c = bis.read();
			}
			bis.close();
			is.close();

		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
			resp = ("Error in http connection " + e.toString());
		}
		return resp;
	}
}
