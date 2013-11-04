package bzzzt02.services;

import java.io.File;

import bzzzt02.config.ConfigData;
import bzzzt02.global.Constants;
import bzzzt02.global.IntentHelper;
import bzzzt02.participants.Participant;
import bzzzt02.sensors.Accelerometer;
import bzzzt02.sensors.Rotation;

import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
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
	boolean movement = false;
	boolean vib = false;
	
	@Override
	public void onCreate() {
		Log.d(TAG, "... onCreate "+TAG);
		tp = null;
		loadConfig();
		maxSamples = config.getMaxNumberSample();
	}
	
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.d(TAG, "... onStartCommand "+intent.getAction());
		
		if(intent.getAction().equals("logRecordingHold")){
			tp.sensors.get(Constants.sensor_Accelerometer).logHoldState();
			tp.sensors.get(Constants.sensor_Rotation).logHoldState();
			tp.sensors.get(Constants.sensor_Gravity).logHoldState();
		}
		
		if(intent.getAction().equals(Constants.extra_NEWTP)){
			tp = (Participant)intent.getParcelableExtra(Constants.extra_PARTICIPANT);
			
			SensorManager sm = (SensorManager)getSystemService(SENSOR_SERVICE);
			System.out.println("SENSORMAGER: "+(sm!=null));
			tp.initSensor(Constants.sensor_Accelerometer, sm, Accelerometer.RAW_AND_LINEAR);
			tp.initSensor(Constants.sensor_Rotation, sm, Rotation.ALL);
			tp.initSensor(Constants.sensor_Gravity, sm, -1);
			
			Log.w(TAG,tp.sensors.toString());
			Log.d(TAG, "new TP: "+tp.indexTP);
		}
		
		if(intent.getAction().equals("startRecording")){
			tp.sensors.get(Constants.sensor_Accelerometer).createNopenFile();
			tp.sensors.get(Constants.sensor_Rotation).createNopenFile();
			tp.sensors.get(Constants.sensor_Gravity).createNopenFile();
			Thread fin = new Thread(new Runnable(){
				    public void run() {
				    // TODO Auto-generated method stub
				    while(!finished)
				    {
				       try {
						Thread.sleep(500);
						Log.d(TAG, "finished: "+finished);
						finished = tp.sensors.get(Constants.sensor_Accelerometer).checkFinished();
						movement = tp.sensors.get(Constants.sensor_Accelerometer).checkMovement();
//						if(movement){
//							sendBroadcast(new Intent("stopRinging"));
//							movement=false;
//						}
						String[] vals = tp.sensors.get(Constants.sensor_Accelerometer).getSensorValues();
						sendBroadcast(IntentHelper.generateSensorValIntent(Constants.sensor_Accelerometer, vals));

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				  
				    }
				    int sampleIndex = tp.sensors.get(Constants.sensor_Accelerometer).getSampleIndex();
				    Intent itn = new Intent();
					if(sampleIndex<= maxSamples){
						if(finished){
							tp.sensors.get(Constants.sensor_Accelerometer).closeFile();
							tp.sensors.get(Constants.sensor_Rotation).closeFile();
							tp.sensors.get(Constants.sensor_Gravity).closeFile();
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
		
		if(intent.getAction().equals("stopRecording")){
			Log.d(TAG,"in Stop recording");
			tp.sensors.get(Constants.sensor_Accelerometer).stopRecord();
			tp.sensors.get(Constants.sensor_Rotation).stopRecord();
			tp.sensors.get(Constants.sensor_Gravity).stopRecord();
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
