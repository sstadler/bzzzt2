package bzzzt02.sensors;


import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.util.Log;
import bzzzt02.config.ConfigData;
import bzzzt02.global.Constants;
import bzzzt02.global.DisplayHelper;
import bzzzt02.participants.Participant;
import bzzzt02.participants.ParticipantHelper;





public class Orientation extends BzzztSensor implements SensorEventListener {
	public static final String TAG = Constants.sensor_Orientation;
	private File currFile;
	private int indexSample;
	private BufferedWriter bwriter;
	String prefixTPFile;
	private List<String> errors;
	private boolean finished = false;
	private boolean movement = false;
	private SensorManager mSensorManager;
	private Sensor mOrientation;
	private boolean mInitialized;
	private int cntEntries = 0;
	private boolean sensorStarted = false;
	
	public Orientation(String prefixTPFile, SensorManager sm){
		super.initParams();
		initParams();
		this.prefixTPFile = prefixTPFile;
		this.mSensorManager = sm;
		System.out.println("INIT ORIENTATION");
	}
	public int getSampleIndex(){
		return indexSample;
	}
	public boolean checkFinished(){
		return finished;
	}
	public boolean checkMovement(){
		return movement;
	}
	public void initParams() {
		errors  = new ArrayList<String>();
		bwriter = null;
		indexSample = 0;
	}
	@Override
	public void createNopenFile() {
		String fname = ParticipantHelper.generateFileName(prefixTPFile,Constants.sensor_abb_Orientation, indexSample);
		System.out.println(fname);
		currFile = createFile(fname);
		bwriter = openFile(currFile);
		writeHaeder();
		indexSample++;
		mOrientation = mSensorManager
				.getDefaultSensor(SensorManager.SENSOR_ORIENTATION);
		mInitialized = false;
		startRecord();
		System.out.println("INIT Orientation "+this.prefixTPFile);
		
	}

	@Override
	public void writeHaeder() {
		try {
			bwriter.append("// " + currFile.getAbsolutePath() + "\n");
			bwriter.append("// timestamp: " + ParticipantHelper.getTimeStamp() + "\n");
			bwriter.append("// orientation data is arranged x;y;z" + "\n");
		} catch (IOException e) {
			errors.add("IOExeption in write Header: " + e.getMessage());
		} catch (NullPointerException e) {
			errors.add("NullPointerException in write Header: "
					+ e.getMessage());
		} finally {
			DisplayHelper.displayErrorList(TAG, errors);
		}
		
	}

	@Override
	public void writeData(float x, float y, float z) {
		try {
			bwriter.append(x + ";" + y + ";" + z + "\n");
		} catch (IOException e) {
			errors.add("IOExeption in writeAccData: " + e.getMessage());
		} finally {
			DisplayHelper.displayErrorList(TAG, errors);
		}
	}

	@Override
	public	void closeFile() {
		try {
			bwriter.close();
			if (indexSample % super.maxNumSamples == 0) {
				super.countTP++;
				indexSample = 0;
			}
		} catch (IOException e) {
			errors.add("IOExeption in close File: " + e.getMessage());
		} finally {
			System.out.println("file closed: " + currFile.getAbsolutePath());
			DisplayHelper.displayErrorList(TAG, errors);
		}
	}

	@Override
	public List<Participant> getTPInfoList() {
		ConfigData config = ConfigData.getInstance();
		return ParticipantHelper.getTPList(new File(config.getValue("tpFolderPath").toString()));
	}
	
	public void resetSampleIndex(){
		indexSample = 0;
	}
	
	public void startRecord(){
		Log.d(TAG, "startRecord");
		sensorStarted = mSensorManager.registerListener(this, mOrientation,
				SensorManager.SENSOR_DELAY_NORMAL);
		
	}
	public void stopRecord(){
		mSensorManager.unregisterListener(this);
		Log.d(TAG, "stopRecord");
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		System.out.println("Rotation size" + event.values.length);
		for (int i=0; i<event.values.length;i++){
			System.out.println("eventOrientateion " + event.values[i]);
		}
		
		
		
	}
	@Override
	public String[] getSensorValues() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void logHoldState() {
		try {
			bwriter.append("//enter Holdstate at: "+ParticipantHelper.getTimeStamp()+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
