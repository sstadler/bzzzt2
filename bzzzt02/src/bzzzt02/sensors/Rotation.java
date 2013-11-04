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
	public static final int RAW = 0;
	public static final int ORIENTATION = 1;
	public static final int MAGNETIC = 2;
	public static final int ALL = 3;

	int type = -1;

	private float[] mValuesMagnet = new float[3];
	private float[] mValuesOrient = new float[3];
	private float[] mValuesRot    = new float[3];

	private float[] mRotationMatrix = new float[9];

	private File currFileR, currFileO, currFileM;
	private int indexSample;
	private BufferedWriter bwriterR, bwriterO, bwriterM;
	private boolean sensorStarted = false;
	private boolean finished = false;
	private boolean movement = false;
	String prefixTPFile;

	private SensorManager mSensorManager = null;
	private boolean mInitialized;

	private List<String> errors;

	private Sensor mMagnet, mRotation;

	public Rotation(String prefixTPFile, SensorManager sm, int sensorSubType) {
		super.initParams();
		type = sensorSubType;
		initParams();
		mSensorManager = sm;
		switch (sensorSubType) {
		case RAW:
			mRotation = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			break;
		case ORIENTATION:
			mRotation = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			mMagnet = mSensorManager
					.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			break;
		case MAGNETIC:
			mMagnet = mSensorManager
					.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			break;
		case ALL:
			mRotation = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			mMagnet = mSensorManager
					.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			break;
		}
		this.prefixTPFile = prefixTPFile;
		Log.d(TAG, "INIT ROTATION " + prefixTPFile);
	}

	public void initParams() {
		errors = new ArrayList<String>();
		bwriterR = null;
		bwriterO = null;
		bwriterM = null;
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
		case Sensor.TYPE_MAGNETIC_FIELD:
			System.arraycopy(event.values, 0, mValuesMagnet, 0, 3);
			break;
		case Sensor.TYPE_ROTATION_VECTOR:
			System.arraycopy(event.values, 0, mValuesRot, 0, 3);
			SensorManager.getRotationMatrixFromVector(mRotationMatrix,
					event.values);
			SensorManager.getOrientation(mRotationMatrix, mValuesOrient);
			break;
		}

		switch (type) {
		case RAW:
			writeData(mValuesRot, RAW);
			break;
		case ORIENTATION:
			writeData(mValuesOrient, ORIENTATION);
			break;
		case MAGNETIC:
			writeData(mValuesMagnet, MAGNETIC);
			break;
		case ALL:
			writeData(mValuesRot, RAW);
			writeData(mValuesOrient, ORIENTATION);
			writeData(mValuesMagnet, MAGNETIC);
			break;
		}
	}

	@Override
	public void createNopenFile() {
		String fname = "";
		currFileO = createFile(fname);
		switch (type) {
		case RAW:
			fname = ParticipantHelper.generateFileName(prefixTPFile,
					Constants.sensor_abb_Rotation, indexSample);
			currFileR = createFile(fname);
			bwriterR = openFile(currFileR);
			break;
		case ORIENTATION:
			fname = ParticipantHelper.generateFileName(prefixTPFile,
					Constants.sensor_abb_Orientation, indexSample);
			currFileO = createFile(fname);
			bwriterO = openFile(currFileO);
			break;
		case MAGNETIC:
			fname = ParticipantHelper.generateFileName(prefixTPFile,
					Constants.sensor_abb_Magnatic, indexSample);
			currFileM = createFile(fname);
			bwriterM = openFile(currFileM);
			break;
		case ALL:
			fname = ParticipantHelper.generateFileName(prefixTPFile,
					Constants.sensor_abb_Rotation, indexSample);
			currFileR = createFile(fname);
			bwriterR = openFile(currFileR);
			fname = ParticipantHelper.generateFileName(prefixTPFile,
					Constants.sensor_abb_Orientation, indexSample);
			currFileO = createFile(fname);
			bwriterO = openFile(currFileO);
			fname = ParticipantHelper.generateFileName(prefixTPFile,
					Constants.sensor_abb_Magnatic, indexSample);
			currFileM = createFile(fname);
			bwriterM = openFile(currFileM);
			break;
		}

		writeHaeder();
		indexSample++;
		mInitialized = false;
		startRecord();

	}

	public String generateHeaderString(String filepath) {

		String header = "// "
				+ filepath
				+ "\n"
				+ "// timestamp: "
				+ ParticipantHelper.getTimeStamp()
				+ "\n"
				+ "// rotation data is arranged x=azimuth;y=pitch;z=roll"
				+ "\n"
				+ "// azimuth=rotation around Zaxis;pitch=rotation around Xaxis;roll=rotation around Yaxis"
				+ "\n";
		return header;
	}

	@Override
	public void writeHaeder() {
		String header = "";
		try {
			switch (type) {
			case RAW:
				header = generateHeaderString(currFileR.getAbsolutePath());
				bwriterR.append(header);
				break;
			case ORIENTATION:
				header = generateHeaderString(currFileO.getAbsolutePath());
				bwriterO.append(header);
				break;
			case MAGNETIC:
				header = generateHeaderString(currFileM.getAbsolutePath());
				bwriterM.append(header);
				break;
			case ALL:
				header = generateHeaderString(currFileR.getAbsolutePath());
				bwriterR.append(header);
				header = generateHeaderString(currFileO.getAbsolutePath());
				bwriterO.append(header);
				header = generateHeaderString(currFileM.getAbsolutePath());
				bwriterM.append(header);
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

	public void writeData(float[] values, int mode) {
		try {
			String tsp = ParticipantHelper.getTimeStamp();
			switch (mode) {
			case RAW:
				bwriterR.append(tsp + ";" + values[0] + ";" + values[1] + ";"
						+ values[2] + "\n");
				break;
			case MAGNETIC:
				bwriterM.append(tsp + ";" + values[0] + ";" + values[1] + ";"
						+ values[2] + "\n");
				break;
			case ORIENTATION:
				bwriterO.append(tsp + ";" + values[0] + ";" + values[1] + ";"
						+ values[2] + "\n");
				break;
			}

		} catch (IOException e) {
			errors.add("IOExeption in writeAccData: " + e.getMessage());
		} finally {
			DisplayHelper.displayErrorList(TAG, errors);
		}
	}

	@Override
	public void writeData(float x, float y, float z) {
		// try {
		// bwriter.append(x + ";" + y + ";" + z + "\n");
		// } catch (IOException e) {
		// errors.add("IOExeption in writeAccData: " + e.getMessage());
		// } finally {
		// DisplayHelper.displayErrorList(TAG, errors);
		// }
		//
	}

	@Override
	public void closeFile() {
		try {
			stopRecord();
			switch (type) {
			case RAW:
				bwriterR.close();
				break;
			case ORIENTATION:
				bwriterO.close();
				break;
			case MAGNETIC:
				bwriterM.close();
				break;
			case ALL:
				bwriterR.close();
				bwriterO.close();
				bwriterM.close();
				break;
			}

			if (indexSample % super.maxNumSamples == 0) {
				super.countTP++;
				indexSample = 0;
			}
		} catch (IOException e) {
			errors.add("IOExeption in close File: " + e.getMessage());
		} finally {
			System.out.println("file closed: " + currFileO.getAbsolutePath());
			DisplayHelper.displayErrorList(TAG, errors);
		}
	}

	@Override
	public List<Participant> getTPInfoList() {
		ConfigData config = ConfigData.getInstance();
		return ParticipantHelper.getTPList(new File(config.getValue(
				"tpFolderPath").toString()));
	}

	@Override
	public int getSampleIndex() {
		return indexSample;
	}

	@Override
	public boolean checkFinished() {
		return finished;
	}

	@Override
	public void resetSampleIndex() {
		indexSample = 0;

	}

	public void startRecord() {
		Log.d(TAG, "startRecord");
		finished = false;
		switch (type) {
		case RAW:
			mSensorManager.registerListener(this, mRotation,
					SensorManager.SENSOR_DELAY_NORMAL);
			break;
		case ORIENTATION:
			mSensorManager.registerListener(this, mRotation,
					SensorManager.SENSOR_DELAY_NORMAL);
			mSensorManager.registerListener(this, mMagnet,
					SensorManager.SENSOR_DELAY_NORMAL);
			break;
		case MAGNETIC:
			mSensorManager.registerListener(this, mMagnet,
					SensorManager.SENSOR_DELAY_NORMAL);
			break;
		case ALL:
			mSensorManager.registerListener(this, mRotation,
					SensorManager.SENSOR_DELAY_NORMAL);
			mSensorManager.registerListener(this, mMagnet,
					SensorManager.SENSOR_DELAY_NORMAL);
			break;
		}
	}

	public void stopRecord() {
		finished = true;
		mSensorManager.unregisterListener(this);
		Log.d(TAG, "stopRecord");
	}

	@Override
	public String[] getSensorValues() {
		return null;
	}

	@Override
	public boolean checkMovement() {
		return movement;
	}

	@Override
	public void logHoldState() {
		try {
			switch (type) {
			case RAW:
				bwriterR.append("//enter Holdstate at: "
						+ ParticipantHelper.getTimeStamp() + "\n");
				break;
			case ORIENTATION:
				bwriterO.append("//enter Holdstate at: "
						+ ParticipantHelper.getTimeStamp() + "\n");
				break;
			case MAGNETIC:
				bwriterM.append("//enter Holdstate at: "
						+ ParticipantHelper.getTimeStamp() + "\n");
				break;
			case ALL:
				bwriterR.append("//enter Holdstate at: "
						+ ParticipantHelper.getTimeStamp() + "\n");
				bwriterO.append("//enter Holdstate at: "
						+ ParticipantHelper.getTimeStamp() + "\n");
				bwriterM.append("//enter Holdstate at: "
						+ ParticipantHelper.getTimeStamp() + "\n");
				break;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
