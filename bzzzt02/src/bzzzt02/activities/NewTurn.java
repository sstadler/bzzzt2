package bzzzt02.activities;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import bzzzt02.config.ConfigData;
import bzzzt02.global.Constants;
import bzzzt02.global.IntentHelper;
import bzzzt02.global.R;
import bzzzt02.global.RegexHelper;
import bzzzt02.participants.Participant;
import bzzzt02.participants.ParticipantHelper;

public class NewTurn extends Activity {
	public static final String TAG = "NewTurn";
	
	private EditText edt_age;
	private Spinner spinner_gender;
	private File externalStorageDir;
	private String downloadDir;
	private ConfigData config;
	
	public void initParams(){
		edt_age        = (EditText) findViewById(R.id.edt_age);
		spinner_gender = (Spinner) findViewById(R.id.sp_gender);
		loadConfig();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");

		setContentView(R.layout.newturn);

		initParams();
	}

	public void btn_newTurn(View view) {
		String age = edt_age.getText().toString();
		if (!RegexHelper.regexMatch(age, Constants.regex_age)) {
			showError();
			return;
		}

		File dataDir  = new File(config.getTPSamplePath());
		String gender = spinner_gender.getSelectedItem().toString();
		int usrID     = ParticipantHelper.getIndexTP(dataDir);

		Participant tp = new Participant(usrID, Integer.valueOf(age),
				ParticipantHelper.formatGender(gender));

		Intent itn = new Intent(Constants.extra_NEWTP);
		itn.putExtra(Constants.extra_PARTICIPANT, tp);
		startService(itn);

		itn = new Intent(NewTurn.this, StartRecord.class);
		itn = IntentHelper.addTPInfo2Intent(itn, Integer.toString(tp.indexTP),"1");
		startActivity(itn);
		finish();
	}

	private void showError() {
		edt_age.setError("Invalid age!");
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
