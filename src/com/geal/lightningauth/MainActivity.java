package com.geal.lightningauth;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import com.geal.lightningauth.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity implements OnClickListener {
	private ImageButton btn;
	private ImageButton authbtn;
	private String authurl;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		String SENDER_ID = "13430005966";
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		Log.v("qauth", "gcm: my reg id is "+regId);
		if (regId.equals("")) {
		  GCMRegistrar.register(this, SENDER_ID);
		  Log.v("qauth", "gcm registering");
		} else {
		  Log.v("qauth", "gcm already registered");
		}

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.v("qauth", "a");
		btn = (ImageButton) findViewById(R.id.qrbutton);
		Log.v("qauth", "b");
		btn.setOnClickListener((OnClickListener) this);
		Log.v("qauth", "c");
		authbtn = (ImageButton) findViewById(R.id.authbutton);
		Log.v("qauth", "d");
		authbtn.setOnClickListener((OnClickListener) this);
	}

	public void showQRScreen() {
		btn.setVisibility(View.VISIBLE);
		authbtn.setVisibility(View.GONE);
		ImageView img = (ImageView) findViewById(R.id.logodemo);
		img.setVisibility(View.GONE);
		TextView websitetext = (TextView) findViewById(R.id.websitetext);
		websitetext.setVisibility(View.GONE);
		TextView questiontext = (TextView) findViewById(R.id.questiontext);
		questiontext.setVisibility(View.GONE);
	}

	public void showAuthScreen() {
		btn.setVisibility(View.GONE);
		authbtn.setVisibility(View.VISIBLE);
		ImageView img = (ImageView) findViewById(R.id.logodemo);
		img.setVisibility(View.VISIBLE);
		TextView websitetext = (TextView) findViewById(R.id.websitetext);
		websitetext.setVisibility(View.VISIBLE);
		TextView questiontext = (TextView) findViewById(R.id.questiontext);
		questiontext.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onClick(View view) {
		if( btn.getId() == ((ImageButton)view).getId() ){
			IntentIntegrator integrator = new IntentIntegrator(this);
			integrator.initiateScan();
		}
		else if( authbtn.getId() == ((ImageButton)view).getId() ){
			dialog = new ProgressDialog(this);
			dialog.setMessage("Authenticating...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();

			new AsyncPostTask(this).execute(authurl);
		}
		Log.v("qauth", "click");

	}

	public void onHttpPostResult(Integer status) {
		Log.v("qauth", "got status: "+status);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dialog.hide();
		showQRScreen();
		//Intent myIntent = new Intent(HelloWorldActivity.this, WebActivity.class);
		//myIntent.putExtra("key", str);
		//HelloWorldActivity.this.startActivity(myIntent);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if ((scanResult != null) && (scanResult.getContents() != null)) {
			authurl = scanResult.getContents();
			Log.v("qauth", authurl);
			new AsyncPutTask().execute(authurl);
			showAuthScreen();

			this.finishActivity(requestCode);
		} else {
			Log.v("qauth", "failed intent barcode scanning");
		}
	}

	private class AsyncPostTask extends AsyncTask<String, Integer, Integer> {
		public MainActivity activity;
		private String result;
		public AsyncPostTask(MainActivity a)
		{
			activity = a;
		}
		protected Integer doInBackground(String... urls) {
			try {
				HttpClient httpclient = new DefaultHttpClient();

				HttpPost http = new HttpPost(urls[0]);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("name", "geo"));
				nameValuePairs.add(new BasicNameValuePair("email", "geo@danston.cul"));
				http.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpclient.execute(http);
				StatusLine statusLine = response.getStatusLine();
				if(statusLine.getStatusCode() == HttpStatus.SC_OK){
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					result = out.toString();
					Log.v("qauth", result);
				} else{
					result = "no result";
					//Closes the connection.
					Log.v("qauth", "closing connection");
					response.getEntity().getContent().close();
					//throw new IOException(statusLine.getReasonPhrase());
				}
				return statusLine.getStatusCode();
			} catch(Exception e) {

				Log.v("qauth", "exception: "+e.toString());
			}
			return 0;
		}

		protected void onProgressUpdate(Integer... progress) {
			// setProgressPercent(progress[0]);
		}

		protected void onPostExecute(Integer res) {
			activity.onHttpPostResult(res);
			//showDialog("Downloaded " + result + " bytes");
		}
	}

	private class AsyncPutTask extends AsyncTask<String, Integer, Integer> {
		//public MainActivity activity;
		private String result;
		/*public AsyncPutTask(MainActivity a)
		{
			activity = a;
		}*/
		protected Integer doInBackground(String... urls) {
			try {
				HttpClient httpclient = new DefaultHttpClient();

				HttpPut http = new HttpPut(urls[0]);
				HttpResponse response = httpclient.execute(http);
				StatusLine statusLine = response.getStatusLine();
				if(statusLine.getStatusCode() == HttpStatus.SC_OK){
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					result = out.toString();
					Log.v("qauth", result);
				} else{
					result = "no result";
					//Closes the connection.
					Log.v("qauth", "closing connection");
					response.getEntity().getContent().close();
					//throw new IOException(statusLine.getReasonPhrase());
				}
				return statusLine.getStatusCode();
			} catch(Exception e) {

				Log.v("qauth", "exception: "+e.toString());
			}
			return 0;
		}

		protected void onProgressUpdate(Integer... progress) {
		}

		protected void onPostExecute(Integer res) {
		}
	}
}
