package com.geal.lightningauth;

import com.geal.lightningauth.R;
import com.google.zxing.integration.android.IntentIntegrator;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends Activity implements OnClickListener {
	private ImageButton btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.v("qauth", "a");
		btn = (ImageButton) findViewById(R.id.qrbutton);
		Log.v("qauth", "b");
        btn.setOnClickListener((OnClickListener) this);
        Log.v("qauth", "c");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void onClick(View view) {
    	Log.v("qauth", "click");
    	IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
      }

}
