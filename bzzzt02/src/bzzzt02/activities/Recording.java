package bzzzt02.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import bzzzt02.global.Constants;
import bzzzt02.global.IntentHelper;
import bzzzt02.global.R;
import bzzzt02.participants.Participant;

public class Recording extends Activity {

	public static final String TAG = "Recording";

	Participant tp;

	private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "start activity " + intent.getAction());

			Intent itn = new Intent();
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
		setContentView(R.layout.bzzzttimer);
		tp = null;
		Log.d(TAG, "... create" + TAG);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("startActivityEndTurn");
		intentFilter.addAction("startActivityRecording");
		registerReceiver(serviceReceiver, intentFilter);
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
		try {
			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
					notification);
			r.play();
		} catch (Exception e) {
		}
		// TODO End ringing
		startService(new Intent("startRecording"));
		sendBroadcast(new Intent("stopTimer"));
	}

}
