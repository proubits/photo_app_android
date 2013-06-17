package app.camera.util;

import java.io.*;
import java.util.*;
import org.apache.http.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;

/**
 * Test photo upload
 * @author webmstr
 *
 */
public class Uploader {

	public static void main(String[] args) {
		Uploader up = new Uploader();
		try {
		File file = new File("/tmp/001317.jpg");
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		int size = (int) file.length();
		byte[] bytes = new byte[size];
		dis.read(bytes);
		up.post(bytes);
		}catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}

	private void post(byte[] ba) {

		String ba1 = app.camera.util.Base64.encodeBytes(ba);

		ArrayList<NameValuePair> nameValuePairs = new

		ArrayList<NameValuePair>();

		nameValuePairs.add(new BasicNameValuePair("image", new String(ba1)));
		InputStream is = null;

		try {

			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httppost = new

			HttpPost("http://localhost/processImage.php");

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();

			is = entity.getContent();
			java.io.BufferedInputStream bis = new java.io.BufferedInputStream(
					is);
			int c = bis.read();
			String resp = "";
			while (c != -1) {
				System.out.print((char) c);
				resp += (char)c;
				c = bis.read();
			}
			bis.close();
			is.close();
			//showMessage(resp);
		} catch (Exception e) {
			e.printStackTrace();
			//Log.e("log_tag", "Error in http connection " + e.toString());
			//showMessage("Error in http connection " + e.toString());
		}
	}
}
