package bzzzt02.activities;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import bzzzt02.config.ConfigData;
import bzzzt02.global.Constants;
import bzzzt02.global.IntentHelper;
import bzzzt02.global.R;

public class StartRecord extends Activity {
	public static final String TAG = "StartRecord";

	private TextView tv_tp, tv_sample;
	private String tpIndex, sampleIndex;
	private File externalStorageDir;
	private String downloadDir;
	private ConfigData config;
	private int maxSamples;

	public void initParams() {
		tpIndex     = "";
		sampleIndex = "";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startrecord);

		tv_tp = (TextView) findViewById(R.id.tv_tpindex);
		tv_sample = (TextView) findViewById(R.id.tv_sampleindex);
		loadConfig();
		maxSamples = config.getMaxNumberSample();

	}

	protected void onStart() {
		super.onStart();
		Intent itn = getIntent();
		Log.d(TAG, "... onStart "+itn.hasExtra(Constants.extra_SAMPLEindex));

		tpIndex     = itn.getStringExtra(Constants.extra_TPindex);
		sampleIndex = itn.getStringExtra(Constants.extra_SAMPLEindex);
		
		tv_tp.setText(tpIndex);
		tv_sample.setText(sampleIndex+"/"+Integer.toString(maxSamples));
	}

	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "... onDestroy");
	}

	public void btn_startRecord(View view) {
		Intent itn = new Intent(StartRecord.this, BzzztTimer.class);
		IntentHelper.addTPInfo2Intent(itn, tpIndex, sampleIndex);
		startActivity(itn);
		finish();
	}

	public void finish() {
		super.finish();
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
