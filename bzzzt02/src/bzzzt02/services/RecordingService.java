package bzzzt02.services;

import java.io.File;

import bzzzt02.config.ConfigData;
import bzzzt02.global.Constants;
import bzzzt02.global.IntentHelper;
import bzzzt02.participants.Participant;

import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;

import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class RecordingService extends Service{
	private static final String TAG="RecordingService";
	
	private Participant tp;
	private int maxSamples;
	private File externalStorageDir = Environment.getExternalStorageDirectory();
	private String downloadDir; 
	private ConfigData config;
	boolean finished = false;
	
	@Override
	public void onCreate() {
		Log.d(TAG, "... onCreate "+TAG);
		tp = null;
		loadConfig();
		maxSamples = config.getMaxNumberSample();
	}
	
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.d(TAG, "... onStartCommand");
		
		if(intent.getAction().equals(Constants.extra_NEWTP)){
			tp = (Participant)intent.getParcelableExtra(Constants.extra_PARTICIPANT);
			
			tp.initSensor(Constants.sensor_Accelerometer, (SensorManager) getSystemService(Context.SENSOR_SERVICE));
			Log.d(TAG, "new TP: "+tp.indexTP);
		}
		
		if(intent.getAction().equals("startRecording")){
			tp.sensors.get(Constants.sensor_Accelerometer).createNopenFile();

			Thread fin = new Thread(new Runnable(){
				    public void run() {
				    // TODO Auto-generated method stub
				    while(!finished)
				    {
				       try {
						Thread.sleep(500);
						Log.d(TAG, "finished: "+finished);
						finished = tp.sensors.get(Constants.sensor_Accelerometer).checkFinished();

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				       //REST OF CODE HERE//
				    }
				    int sampleIndex = tp.sensors.get(Constants.sensor_Accelerometer).getSampleIndex();
				    Intent itn = new Intent();
					if(sampleIndex<= maxSamples){
						if(finished){
							tp.sensors.get(Constants.sensor_Accelerometer).closeFile();
							Log.d(TAG,"sampleindex: "+sampleIndex);
							Log.d(TAG,"maxSampls:   "+maxSamples);
							if(sampleIndex< maxSamples){
								itn = new Intent("startActivityRecording");
								itn = IntentHelper.addTPInfo2Intent(itn, Integer.toString(tp.indexTP), Integer.toString(sampleIndex+1));
							}
							if(sampleIndex==maxSamples){
								tp.sensors.get(Constants.sensor_Accelerometer).resetSampleIndex();
								itn= new Intent("startActivityEndTurn");
								IntentHelper.addTPInfo2Intent(itn, Integer.toString(tp.indexTP), Integer.toString(sampleIndex));
							}
						}	
					}
					else{itn= new Intent("startActivityEndTurn");}
					sendBroadcast(itn);
					finished = false;

				                    }
				});
			fin.start();

		}
		 
		return START_STICKY;
	}
	
	public void onDestroy(){
		super.onDestroy();
		Log.d(TAG, "... onDestroy");
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
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
