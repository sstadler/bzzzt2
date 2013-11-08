package bzzzt02.sensors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
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
	
	public static final int RAW            = 0;
	public static final int LINEAR         = 1;
	public static final int RAW_AND_LINEAR = 2;
	
	private int type;
	private File currFileACCR, currFileACCL;
	public int indexSample;
	private BufferedWriter bwriterACCR, bwriterACCL;
	String prefixTPFile;

	private List<String> errors;
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer, mAccelerometerLinear;
	private float mLastXaccR, mLastYaccR, mLastZaccR;
	private float mLastXaccL, mLastYaccL, mLastZaccL;
	
	private int cntEntries = 0;
	private boolean finished = false;
	private boolean movement = false;
	private LinkedList<AccData> hold = new LinkedList();
	
	public Accelerometer(){
		super.initParams();
		initParams();
	}
	
	public Accelerometer(String prefixTPFile, SensorManager sm, int type){
		super.initParams();
		initParams();
		this.prefixTPFile = prefixTPFile;
		this.mSensorManager = sm;
		this.type = type;
		switch (type) {
		case RAW:
			mAccelerometer = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			bwriterACCR = null;
			break;
		case LINEAR:
			mAccelerometerLinear = mSensorManager
					.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			bwriterACCL = null;
			break;
		case RAW_AND_LINEAR:
			mAccelerometer = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			mAccelerometerLinear = mSensorManager
					.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			bwriterACCR = null;
			bwriterACCL = null;
			break;
		}
		SensorHelper.showSensorList(sm);
		Log.d(TAG, "... init ");
	}
	
	public int getSampleIndex(){
		return indexSample;
	}
	
	public boolean checkFinished(){
		return finished;
	}

	public void initParams() {
		errors = new ArrayList<String>();
		indexSample = 0;
		mLastXaccR = 0.0f;
		mLastYaccR = 0.0f;
		mLastZaccR = 0.0f;
		mLastXaccL = 0.0f;
		mLastYaccL = 0.0f;
		mLastZaccL = 0.0f;
	}

	@Override
	public void createNopenFile() {
		String fname = "";
		switch(type){
		case RAW:
			fname = ParticipantHelper.generateFileName(prefixTPFile,Constants.sensor_abb_Accelerometer_raw, indexSample);
			currFileACCR = createFile(fname);
			bwriterACCR  = openFile(currFileACCR);
			break;
		case LINEAR:
			fname = ParticipantHelper.generateFileName(prefixTPFile,Constants.sensor_abb_Accelerometer_linar, indexSample);
			currFileACCL = createFile(fname);
			bwriterACCL  = openFile(currFileACCL);
			break;
		case RAW_AND_LINEAR:
			fname = ParticipantHelper.generateFileName(prefixTPFile,Constants.sensor_abb_Accelerometer_raw, indexSample);
			currFileACCR = createFile(fname);
			bwriterACCR  = openFile(currFileACCR);
			
			fname = ParticipantHelper.generateFileName(prefixTPFile,Constants.sensor_abb_Accelerometer_linar, indexSample);
			currFileACCL = createFile(fname);
			bwriterACCL  = openFile(currFileACCL);
		}

		finished = false;
		cntEntries = 0;
		writeHaeder();
		indexSample++;
		startRecord();
	}

	@Override
	public void writeHaeder() {
		String header = "";
		try {
			switch (type){
			case RAW:
				header = SensorHelper.generateHeaderString(currFileACCR.getAbsolutePath(), "accelerometer_raw");
				bwriterACCR.append(header);
				break;
			case LINEAR:
				header = SensorHelper.generateHeaderString(currFileACCL.getAbsolutePath(), "accelerometer_linear");
				bwriterACCL.append(header);
				break;
			case RAW_AND_LINEAR:
				header = SensorHelper.generateHeaderString(currFileACCR.getAbsolutePath(),"accelerometer_raw");
				bwriterACCR.append(header);
				
				header = SensorHelper.generateHeaderString(currFileACCL.getAbsolutePath(), "accelerometer_linear");
				bwriterACCL.append(header);
				break;
			}
			
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
//		try {
//			String tsp = ParticipantHelper.getTimeStamp();
//			switch(type){
//			case RAW:
//				bwriterACCR.append(tsp+";"+x + ";" + y + ";" + z + "\n");
//				break;
//			case LINEAR:
//				bwriterACCL.append(tsp+";"+x + ";" + y + ";" + z + "\n");
//				break;
//			case RAW_AND_LINEAR:
//				bwriterACCR.append(tsp+";"+x + ";" + y + ";" + z + "\n");
//				bwriterACCL.append(tsp+";"+x + ";" + y + ";" + z + "\n");
//				break;
//			}
//			
//		} catch (IOException e) {
//			errors.add("IOExeption in writeAccData: " + e.getMessage());
//		} finally {
//			DisplayHelper.displayErrorList(TAG, errors);
//		}
	}
	
	public void writeData(float[] values, int type){
		try {
			String tsp = ParticipantHelper.getTimeStamp();
			switch(type){
			case RAW:
				bwriterACCR.append(tsp+";"+values[0] + ";" + values[1] + ";" + values[2] + "\n");
				break;
			case LINEAR:
				bwriterACCL.append(tsp+";"+values[0] + ";" + values[1] + ";" + values[2] + "\n");
				break;
			}
			
		} catch (IOException e) {
			errors.add("IOExeption in writeAccData: " + e.getMessage());
		} finally {
			DisplayHelper.displayErrorList(TAG, errors);
		}
	}

	@Override
	public void closeFile() {
		System.out.println("file closed: " + currFileACCR.getAbsolutePath());
		mSensorManager.unregisterListener(this);
		try {
			switch(type){
			case RAW:
				bwriterACCR.close();
				break;
			case LINEAR:
				bwriterACCL.close();
				break;
			case RAW_AND_LINEAR:
				bwriterACCR.close();
				bwriterACCL.close();
				break;
			}
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
	public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		
		
		switch(event.sensor.getType()){
		case Sensor.TYPE_ACCELEROMETER:
			writeData(event.values, RAW);
			mLastXaccR = x;
			mLastYaccR = y;
			mLastZaccR = z;
			Log.d(TAG, "raw: "+x+" "+y+" "+z);
			break;
		case Sensor.TYPE_LINEAR_ACCELERATION:
			writeData(event.values, LINEAR);
			mLastXaccL = x;
			mLastYaccL = y;
			mLastZaccL = z;
			Log.d(TAG,"Linear: "+x+" "+y+" "+z);
			break;
		}
//		AccData ac = new AccData(event.values);
//		writeData(x, y, z);
//		hold.add(ac);
//		


//		cntEntries++;
//		Log.d(TAG, "cntEnt: "+cntEntries);
//		if(hold.size()==5){
//			Iterator i = hold.listIterator();
//			while(i.hasNext()){
//				AccData ad = (AccData)i.next();
//				Log.d(TAG,"AccData Z: "+ad.getZ());
//			}
//			hold.removeFirst();
//		}
		
	}
	public void startRecord(){
		Log.d(TAG, "startRecord");
		finished=false;
		switch(type){
		case RAW:
			mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
			break;
		case LINEAR:
			mSensorManager.registerListener(this, mAccelerometerLinear,
					SensorManager.SENSOR_DELAY_NORMAL);
		case RAW_AND_LINEAR:
			mSensorManager.registerListener(this, mAccelerometer,
					SensorManager.SENSOR_DELAY_NORMAL);
			mSensorManager.registerListener(this, mAccelerometerLinear,
					SensorManager.SENSOR_DELAY_NORMAL);
		}		
	}
	
	public void stopRecord(){
		finished=true;
		mSensorManager.unregisterListener(this);
		Log.d(TAG, "stopRecord");
	}

	@Override
	public String[] getSensorValues(int type) {
		String[] a = {"0.0","0.0","0.0","0.0","0.0","0.0"}; 			
		switch(type){
		case RAW:
			a[0] = String.valueOf(mLastXaccR);
			a[1] = String.valueOf(mLastYaccR);
			a[2] = String.valueOf(mLastZaccR);
			break;
		case LINEAR:
			a[0] = String.valueOf(mLastXaccL);
			a[1] = String.valueOf(mLastYaccL);
			a[2] = String.valueOf(mLastZaccL);
			break;
		case RAW_AND_LINEAR:
			a[0] = String.valueOf(mLastXaccR);
			a[1] = String.valueOf(mLastYaccR);
			a[2] = String.valueOf(mLastZaccR);
			a[3] = String.valueOf(mLastXaccL);
			a[4] = String.valueOf(mLastYaccL);
			a[5] = String.valueOf(mLastZaccL);
			break;
		default:
			a[0] = "0.0";
			a[1] = "0.0";
			a[2] = "0.0";
		}
		
		return a;
	}

	@Override
	public boolean checkMovement() {
		return movement;
	}

	@Override
	public void logHoldState() {
		try {
			switch(type){
			case RAW:
				bwriterACCR.append("//enter Holdstate at: "+ParticipantHelper.getTimeStamp()+"\n");
			case LINEAR:
				bwriterACCL.append("//enter Holdstate at: "+ParticipantHelper.getTimeStamp()+"\n");
			case RAW_AND_LINEAR:
				bwriterACCR.append("//enter Holdstate at: "+ParticipantHelper.getTimeStamp()+"\n");
				bwriterACCL.append("//enter Holdstate at: "+ParticipantHelper.getTimeStamp()+"\n");
			}
			bwriterACCR.append("//enter Holdstate at: "+ParticipantHelper.getTimeStamp()+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
