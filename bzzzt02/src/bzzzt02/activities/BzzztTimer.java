package bzzzt02.activities;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import bzzzt02.config.ConfigData;
import bzzzt02.global.Constants;
import bzzzt02.global.IntentHelper;
import bzzzt02.global.R;

public class BzzztTimer extends Activity {
	public static String TAG = "BzzztTimer";
	
	private TextView tv_timer, tv_tpIndex, tv_sampleIndex;
	private String tpIndex, sampleIndex;
	private File externalStorageDir;
	private String downloadDir;
	private ConfigData config;
	private int maxSamples;
	
	public void initParams(){
		tpIndex = "";
		sampleIndex = "";
		loadConfig();
		maxSamples = config.getMaxNumberSample();
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bzzzttimer);
		initParams();
		tv_timer = (TextView) findViewById(R.id.tv_timer);
		tv_timer.setText("15");

		tv_tpIndex = (TextView) findViewById(R.id.tv_tpindex);
		tv_sampleIndex = (TextView) findViewById(R.id.tv_sampleindex);
 
	}

	protected void onStart() {
		// Reusable Intent for each tab
		super.onStart();
		Log.d(TAG, "... onStart " + TAG);

		Intent itn = getIntent();
		tpIndex = itn.getStringExtra(Constants.extra_TPindex);
		sampleIndex = itn.getStringExtra(Constants.extra_SAMPLEindex);
		tv_tpIndex.setText(tpIndex);
		tv_sampleIndex.setText(sampleIndex + "/"+Integer.toString(maxSamples));

		new CountDown(15000, 1000).start();
	}

	protected void onResume() {
		super.onResume();
		Log.d(TAG, ".... onResume ");
	}

	protected void onStop() {
		super.onStop();
		Log.d(TAG, ".... onStop");
	}

	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, ".... onDestroy");
	}

	public void finish() {
		super.finish();
	}

	public class CountDown extends CountDownTimer {

		public CountDown(long millisec, long interval) {
			super(millisec, interval);
		}

		@Override
		public void onFinish() {
			Intent itn = new Intent(BzzztTimer.this, Recording.class);
			IntentHelper.addTPInfo2Intent(itn, tpIndex, sampleIndex);
			startActivity(itn);
		}

		@Override
		public void onTick(long millisecUntilFinished) {
			tv_timer.setText(Long.toString(millisecUntilFinished / 1000));
		}
	}
	public void loadConfig(){
		externalStorageDir = Environment.getExternalStorageDirectory();
		downloadDir = "/Download/bzzzt.config";
		config = ConfigData.getInstance();
		if(!config.loaded){
			config.loadConfig(externalStorageDir.getAbsoluteFile() + downloadDir);
		}
	}
}
