package bzzzt02.activities;

import java.io.File;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import bzzzt02.config.ConfigData;
import bzzzt02.global.Constants;
import bzzzt02.global.IntentHelper;
import bzzzt02.global.R;
import bzzzt02.participants.Participant;

public class Recording extends Activity {

	public static final String TAG = "Recording";

	Participant tp;
	String tpIndex,	sampleIndex;
	TextView tv_tpIndex,tv_sampleIndex, tv_accx, tv_accy,tv_accz;
	private ConfigData config;
	private File externalStorageDir;
	private String downloadDir;
	private int maxSamples;
	Ringtone r;
	
	private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "start activity " + intent.getAction());
			Intent itn = new Intent();
			
			if(intent.getAction().equals("stopRinging")){
				stopRinging();
				return;
			}
			
			if(intent.getAction().equals("accData")){
//				tv_accx.setText(intent.getStringExtra("x"));
//				tv_accy.setText(intent.getStringExtra("y"));
//				tv_accz.setText(intent.getStringExtra("z"));
//				Log.d(TAG, "x: "+intent.getStringExtra("x"));
//				if(intent.getStringExtra("x").isEmpty()){tv_accx.setText("null");}
				return;
			}

			if (intent.getAction().equals("startActivityRecording")) {
				itn = new Intent(context.getApplicationContext(),
						StartRecord.class);
			}
			if (intent.getAction().equals("startActivityEndTurn")) {
				itn = new Intent(context.getApplicationContext(), EndTurn.class);
			}
			itn = IntentHelper.addTPInfo2Intent(itn,
					intent.getStringExtra(Constants.extra_TPindex),
					intent.getStringExtra(Constants.extra_SAMPLEindex));
			startActivity(itn);
			finish();
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recording);
		tp = null;
		Log.d(TAG, "... create" + TAG);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("startActivityEndTurn");
		intentFilter.addAction("startActivityRecording");
		intentFilter.addAction("accData");
		intentFilter.addAction("stopRinging");
		registerReceiver(serviceReceiver, intentFilter);
		loadConfig();
		maxSamples = config.getMaxNumberSample();
		tv_tpIndex = (TextView) findViewById(R.id.tv_tpindex);
		tv_sampleIndex = (TextView) findViewById(R.id.tv_sampleindex);
		tv_accx = (TextView) findViewById(R.id.tv_x);
		tv_accy = (TextView) findViewById(R.id.tv_y);
		tv_accz = (TextView) findViewById(R.id.tv_z);
		//vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		try {
			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			r = RingtoneManager.getRingtone(getApplicationContext(),
					notification);
		} catch (Exception e) {
		}
	}

	public void finish() {
		super.finish();
	}

	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "... onDestroy");
		unregisterReceiver(serviceReceiver);
	}

	protected void onStart() {
		super.onStart();
		Log.d(TAG, "... onStart");
		Intent itn = getIntent();
		tpIndex = itn.getStringExtra(Constants.extra_TPindex);
		sampleIndex = itn.getStringExtra(Constants.extra_SAMPLEindex);
		tv_tpIndex.setText(tpIndex);
		tv_sampleIndex.setText(sampleIndex + "/"+Integer.toString(maxSamples));
		startRinging();
		startService(new Intent("startRecording"));
		sendBroadcast(new Intent("stopTimer"));
	}
	
	public void startRinging(){
		Log.d(TAG, "start ringing");
		r.play();
	}
	
	public void stopRinging(){
		Log.d(TAG, "stop ringing");
		r.stop();
	}
	public void btn_startRecordingHold(View view){
		Intent itn = new Intent("logRecordingHold");
		startService(itn);
		itn = new Intent(this, RecordingHold.class);
		IntentHelper.addTPInfo2Intent(itn, tpIndex, sampleIndex);
		startActivity(itn);
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


