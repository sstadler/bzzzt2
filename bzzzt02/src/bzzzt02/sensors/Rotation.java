package bzzzt02.sensors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bzzzt02.config.ConfigData;
import bzzzt02.global.Constants;
import bzzzt02.global.DisplayHelper;
import bzzzt02.participants.Participant;
import bzzzt02.participants.ParticipantHelper;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class Rotation extends BzzztSensor implements SensorEventListener {
	public final String TAG = "Rotation";
	private float[] mValuesMagnet = new float[3];
	private float[] mValuesAcc    = new float[3];
	private float[] mValuesOrient = new float[3];
	private float[] mValuesRot    = new float[3];
	
	private float[] mRotationMatrix = new float[9];
	
	private File currFile;
	private int indexSample;
	private BufferedWriter bwriter;
	private boolean sensorStarted = false;
	private boolean finished = false;
	private boolean movement = false;
	String prefixTPFile;
	
	private SensorManager mSensorManager = null;
	private boolean mInitialized;
	
	private List<String> errors;
	
	private Sensor mAccelerometer, mMagnet, mRotation;
	
	public Rotation(String prefixTPFile, SensorManager sm){
		super.initParams();
		initParams();
		mSensorManager = sm;
		mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
//		mMagnet = mSensorManager
//				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//		mAccelerometer = mSensorManager
//				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		
		this.prefixTPFile = prefixTPFile;
		Log.d(TAG,"INIT ROTATION "+prefixTPFile);
	}
	
	public void initParams(){
		errors  = new ArrayList<String>();
		bwriter = null;
		indexSample = 0;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		 // Handle the events for which we registered
        switch (event.sensor.getType()) {           
//            case Sensor.TYPE_ACCELEROMETER: 
//                System.arraycopy(event.values, 0, mValuesAcc, 0, 3); 
//                
//                break; 
//
//            case Sensor.TYPE_MAGNETIC_FIELD: 
//                System.arraycopy(event.values, 0, mValuesMagnet, 0, 3); 
//                break; 
        	 case Sensor.TYPE_ROTATION_VECTOR:
        		 System.arraycopy(event.values, 0, mValuesRot, 0, 3);
    }
        //SensorManager.getRotationMatrix(mRotationMatrix, null, mValuesAcc, mValuesMagnet);
        SensorManager.getRotationMatrixFromVector(
                mRotationMatrix , event.values);
        SensorManager.getOrientation(mRotationMatrix, mValuesRot);  
//		
//        Log.d(TAG, mValuesOrient[0]+" "+mValuesOrient[1]+" "+mValuesOrient[2]);
//        writeData(mValuesOrient[0], mValuesOrient[1],mValuesOrient[2]);
        Log.d(TAG, mValuesRot[0]+" "+mValuesRot[1]+" "+mValuesRot[2]);
        writeData(mValuesRot[0], mValuesRot[1],mValuesRot[2]);
	}

	@Override
	public void createNopenFile() {
		String fname = ParticipantHelper.generateFileName(prefixTPFile,Constants.sensor_abb_Rotation, indexSample);
		System.out.println(fname);
		currFile = createFile(fname);
		bwriter = openFile(currFile);
		writeHaeder();
		indexSample++;
		mInitialized = false;
		startRecord();
		
	}

	@Override
	public void writeHaeder() {
		try {
			bwriter.append("// " + currFile.getAbsolutePath() + "\n");
			bwriter.append("// timestamp: " + ParticipantHelper.getTimeStamp() + "\n");
			bwriter.append("// rotation data is arranged x=azimuth;y=pitch;z=roll" + "\n");
			bwriter.append("// azimuth=rotation around Zaxis;pitch=rotation around Xaxis;roll=rotation around Yaxis" + "\n");
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
	public void closeFile() {
		try {
			stopRecord();
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

	@Override
	public int getSampleIndex(){
		return indexSample;
	}
	@Override
	public boolean checkFinished(){
		return finished;
	}

	@Override
	public void resetSampleIndex() {
		indexSample = 0;
		
	}

	public void startRecord(){
		Log.d(TAG, "startRecord");
		finished = false;
//		mSensorManager.registerListener(this, mAccelerometer,
//				SensorManager.SENSOR_DELAY_NORMAL);
//		mSensorManager.registerListener(this, mMagnet,
//				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_NORMAL);
		
	}
	public void stopRecord(){
		finished = true;
		mSensorManager.unregisterListener(this);
		Log.d(TAG, "stopRecord");
	}

	@Override
	public String[] getSensorValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkMovement() {
		return movement;
	}

}
