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
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import bzzzt02.activities.BzzztTimer.CountDown;
import bzzzt02.config.ConfigData;
import bzzzt02.global.Constants;
import bzzzt02.global.IntentHelper;
import bzzzt02.global.R;
import bzzzt02.participants.Participant;

public class RecordingHold extends Activity {

	public static final String TAG = "RecordingHold";

	Participant tp;
	String tpIndex,	sampleIndex;
	TextView tv_tpIndex,tv_sampleIndex, tv_recording;
	private ConfigData config;
	private File externalStorageDir;
	private String downloadDir;
	private int maxSamples;
	private CountDown rcd;
	private Vibrator vib;// Needed service vibrator
	Ringtone r;
	private int vibtime;
	
	private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "start activity " + intent.getAction());
			Intent itn = new Intent();
			
			if(intent.getAction().equals("stopRecording")){
				rcd.cancel();
			}
			
			if(intent.getAction().equals("accData")){
//				tv_accx.setText(intent.getStringExtra("xaccR"));
//				tv_accy.setText(intent.getStringExtra("yaccR"));
//				tv_accz.setText(intent.getStringExtra("zaccR"));
				return;
			}

//			if (intent.getAction().equals("startActivityRecording")) {
//				itn = new Intent(context.getApplicationContext(),
//						StartRecord.class);
//			}
//			if (intent.getAction().equals("startActivityEndTurn")) {
//				itn = new Intent(context.getApplicationContext(), EndTurn.class);
//			}
//			itn = IntentHelper.addTPInfo2Intent(itn,
//					intent.getStringExtra(Constants.extra_TPindex),
//					intent.getStringExtra(Constants.extra_SAMPLEindex));
//			startActivity(itn);
//			finish();
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordhold);
		tp = null;
		Log.d(TAG, "... create" + TAG);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("accData");
		intentFilter.addAction("stopRecording");
		registerReceiver(serviceReceiver, intentFilter);
		loadConfig();
		maxSamples = config.getMaxNumberSample();
		tv_tpIndex = (TextView) findViewById(R.id.tv_tpindex);
		tv_tpIndex.bringToFront();
		tv_sampleIndex = (TextView) findViewById(R.id.tv_sampleindex);
		tv_sampleIndex.bringToFront();
		tv_recording = (TextView) findViewById(R.id.tv_recording);
		vibtime = config.getVibInSec();
		vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
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
		Intent itn2 = new Intent("stopRinging");
		sendBroadcast(itn2);
		vib.vibrate(vibtime*1000);
		rcd = new CountDown(vibtime*1000, 1000, tpIndex, sampleIndex);
		rcd.start();
		// TODO startVibriation
		//startService(new Intent("startRecording"));
		//sendBroadcast(new Intent("stopTimer"));
	}
	
//	public void startRinging(){
//		r.play();
//	}
//	
//	public void stopRinging(){
//		r.stop();
//	}
	public class CountDown extends CountDownTimer {
		
		String tpIndex = "";
		String sampleIndex= "";

		public CountDown(long millisec, long interval, String tpIndex, String sampleIndex) {
			super(millisec, interval);
			this.tpIndex = tpIndex;
			this.sampleIndex = sampleIndex;
		}

		@Override
		public void onFinish() {
			Intent itn = new Intent("stopRecording");
			//IntentHelper.addTPInfo2Intent(itn, tpIndex, sampleIndex);
			startService(itn);
			
			
		}

		@Override
		public void onTick(long millisecUntilFinished) {
			//tv_timer.setText(Long.toString(millisecUntilFinished / 1000));
			System.out.println("TICK-----------------------------------------"+Long.toString(millisecUntilFinished / 1000));
			if((millisecUntilFinished/1000)%2==1){
				tv_recording.setVisibility(View.VISIBLE);
			}else{
				tv_recording.setVisibility(View.INVISIBLE);
			}
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


