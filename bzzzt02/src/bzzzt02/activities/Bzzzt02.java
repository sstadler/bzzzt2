package bzzzt02.activities;

import bzzzt02.global.R;
import android.os.Bundle;
import android.app.Activity;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class Bzzzt02 extends Activity {
	static final String TAG = "Bzzzt02";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "... onCreate");
		setContentView(R.layout.activity_bzzzt02);
	}
	
	protected void onStart(){
		super.onStart();
		Log.d(TAG, "... onStart");
	}
	
	protected void onResume(){
		super.onResume();
		Log.d(TAG, "... onResume");
	}
	
	protected void onStop(){
		super.onStop();
		Log.d(TAG, "... onStop");
	}
	
	protected void onDestroy(){
		super.onDestroy();
		Log.d(TAG, "... onDestroy");
	}

	public void btn_go2Train(View view) {
		Intent intent = new Intent(Bzzzt02.this, NewTurn.class);
		startActivity(intent);
	}

	public void btn_go2Stats(View view) {
		// TODO
	}

	public void btn_go2Settings(View view) {
		// TODO
	}

	public void btn_go2Maintenance(View view) {
		// TODO
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.bzzzt02, menu);
		return true;
	}

}
