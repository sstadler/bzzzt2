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
import bzzzt02.global.R;

public class EndTurn extends Activity{
	public static final String TAG="EndTurn";
	
	TextView tv_tpIndex, tv_sampleIndex;
	private File externalStorageDir;
	private String downloadDir;
	private ConfigData config;
	private int maxSamples;
	
	public void initParams(){
		tv_tpIndex     = (TextView)findViewById(R.id.tv_tpindex);
		tv_sampleIndex = (TextView)findViewById(R.id.tv_sampleindex);
		loadConfig();
		maxSamples = config.getMaxNumberSample();
	}
	   
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"... onCreate");
        setContentView(R.layout.endturn);
        
        initParams();
    }
	
	protected void onStart() {
		super.onStart();
		Log.d(TAG,"... onStart");
		String tpIndex     = getIntent().getStringExtra(Constants.extra_TPindex);
		String sampleIndex = getIntent().getStringExtra(Constants.extra_SAMPLEindex);
		tv_tpIndex.setText(tpIndex);
		tv_sampleIndex.setText(sampleIndex+"/"+Integer.toString(maxSamples));
	}
	
	protected void onDestroy(){
		super.onDestroy();
		Log.d(TAG,"... onDestroy");
	}
	public void btn_go2Home(View view){
    	startActivity(new Intent(EndTurn.this, Bzzzt02.class));
    	finish();
	}
	public void finish(){
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
