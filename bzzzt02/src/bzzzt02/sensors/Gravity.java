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

public class Gravity extends BzzztSensor implements SensorEventListener {

	public static final String TAG = "Gravity";
	
	private int indexSample;
	private String prefixTPFile;
	private SensorManager mSensorManager;
	private Sensor mGravity;
	private boolean finished;
	private BufferedWriter bwriter;
	private File currFile;

	private List<String> errors;

	public Gravity() {
		super.initParams();
		initParameters();
	}
	
	public Gravity(String prefixTPFile, SensorManager sm, int type){
		super.initParams();
		mSensorManager = sm;
		initParameters();
		this.prefixTPFile   = prefixTPFile;
		
	}
	
	public void initParameters(){
		errors = new ArrayList<String>();
		prefixTPFile = "";
		indexSample = 0;
		finished = false;
		bwriter=null;
		currFile=null;
		mGravity = mSensorManager
				.getDefaultSensor(Sensor.TYPE_GRAVITY);
	}
	

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Log.d(TAG,"sensor changed!!");
		writeData(event.values);
	}

	@Override
	public void createNopenFile() {
		String fname = ParticipantHelper.generateFileName(prefixTPFile,Constants.sensor_abb_Gravity, indexSample);
		currFile = createFile(fname);
		bwriter  = openFile(currFile);
		finished = false;
		writeHaeder();
		indexSample++;
		startRecord();
	}

	@Override
	public void writeHaeder() {
		String header = SensorHelper.generateHeaderString(currFile.getAbsolutePath(), "gravity");
		try {
			bwriter.append(header+"---------------------------------");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void writeData(float x, float y, float z) {}
	
	public void writeData(float[] values){
		try {

			bwriter.append(ParticipantHelper.getTimeStamp()+";"+values[0] + ";" + values[1] + ";" + values[2] + "\n");
			
		} catch (IOException e) {
			errors.add("IOExeption in writeData: " + e.getMessage());
		} finally {
			DisplayHelper.displayErrorList(TAG, errors);
		}
	}

	@Override
	public void closeFile() {
		Log.d(TAG,"close file: " + currFile.getAbsolutePath());
		mSensorManager.unregisterListener(this);
		try {
			bwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<Participant> getTPInfoList() {
		ConfigData config = ConfigData.getInstance();
		return ParticipantHelper.getTPList(new File(config.getValue("tpFolderPath").toString()));
	}

	@Override
	public int getSampleIndex() {
		return indexSample;
	}

	@Override
	public void resetSampleIndex() {
		indexSample = 0;
	}

	@Override
	public boolean checkFinished() {
		return finished;
	}

	@Override
	public boolean checkMovement() {
		return false;
	}

	@Override
	public void stopRecord() {
		finished=true;
		mSensorManager.unregisterListener(this);
		Log.d(TAG, "stopRecord");
	}

	public void startRecord(){
		mSensorManager.registerListener(this, mGravity,
				SensorManager.SENSOR_DELAY_NORMAL);
	}
	@Override
	public String[] getSensorValues() {
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
