package bzzzt02.sensors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import bzzzt02.config.ConfigData;
import bzzzt02.global.Constants;
import bzzzt02.global.DisplayHelper;
import bzzzt02.participants.Participant;
import bzzzt02.participants.ParticipantHelper;

public class Accelerometer extends BzzztSensor implements SensorEventListener{
	public static final String TAG = Constants.sensor_Accelerometer;

	private File currFile;
	public int indexSample;
	private BufferedWriter bwriter;
	String prefixTPFile;

	private List<String> errors;
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;
	private int cntEntries = 0;
	private boolean finished = false;
	
	public Accelerometer(){
		super.initParams();
		initParams();
		System.out.println("INIT ACCELEROMETER");
	}
	
	public Accelerometer(String prefixTPFile, SensorManager sm){
		super.initParams();
		initParams();
		this.prefixTPFile = prefixTPFile;
		this.mSensorManager = sm;
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mInitialized = false;
		System.out.println("INIT ACCELEROMETER "+this.prefixTPFile);
	}
	
	public int getSampleIndex(){
		return indexSample;
	}
	
	public boolean checkFinished(){
		return finished;
	}

	public void initParams() {
		errors = new ArrayList<String>();
		bwriter = null;
		indexSample = 0;
	}

	@Override
	public void createNopenFile() {
		String fname = ParticipantHelper.generateFileName(prefixTPFile,Constants.sensor_abb_Accelerometer, indexSample);
		currFile = createFile(fname);
		bwriter  = openFile(currFile);
		finished = false;
		cntEntries = 0;
		writeHaeder();
		indexSample++;
		boolean regei = mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		Log.d(TAG, "acc reg "+regei);
	}

	@Override
	public void writeHaeder() {
		try {
			bwriter.append("// " + currFile.getAbsolutePath() + "\n");
			bwriter.append("// timestamp: " + ParticipantHelper.getTimeStamp() + "\n");
			bwriter.append("// accelerometer data is arranged x;y;z" + "\n");
		} catch (IOException e) {
			errors.add("IOExeption in write Header: " + e.getMessage());
		} catch (NullPointerException e) {
			errors.add("NullPointerException in write Header: "
					+ e.getMessage());
		} finally {
			DisplayHelper.displayErrorList(TAG, errors);
		}
	}


	public void writeData(float x, float y, float z) {
		try {
			Log.d(TAG,"iwrite");
			bwriter.append(x + ";" + y + ";" + z + "\n");
		} catch (IOException e) {
			errors.add("IOExeption in writeAccData: " + e.getMessage());
		} finally {
			DisplayHelper.displayErrorList(TAG, errors);
		}
	}

	@Override
	public void closeFile() {
		System.out.println("file closed: " + currFile.getAbsolutePath());
		mSensorManager.unregisterListener(this);
		try {
			bwriter.close();
			if (indexSample % super.maxNumSamples == 0) {
				super.countTP++;
			}
		} catch (IOException e) {
			errors.add("IOExeption in close File: " + e.getMessage());
		} finally {
			DisplayHelper.displayErrorList(TAG, errors);
		}
	}

	public List<Participant> getTPInfoList() {
		ConfigData config = ConfigData.getInstance();
		return ParticipantHelper.getTPList(new File(config.getValue("tpFolderPath").toString()));
	}
	
	public void resetSampleIndex(){
		indexSample = 0;
	}

	@Override
	public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		Log.d(TAG, x+" "+y+" "+z);
		writeData(x, y, z);
		cntEntries++;
		Log.d(TAG, "cntEnt: "+cntEntries);
		if(cntEntries==50){
			finished = true;
			Log.d(TAG, "finished true");
		}
		
	}
}
