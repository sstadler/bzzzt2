package bzzzt02.sensors;

import android.hardware.SensorManager;
import android.util.Log;
import bzzzt02.global.Constants;



public class SensorFactory {
	private static final String TAG="SensorFactory";
	
	public BzzztSensor getSensor(String sensorType, String prefixTPFile, SensorManager sm){
		Log.d(TAG, "sensorTYPE: "+sensorType+" sm is !=null "+(sm!=null));
		if(sensorType == null){
			return null;
		}
		if(sensorType.equals(Constants.sensor_Accelerometer)){
			return new Accelerometer(prefixTPFile, sm);
		}
		if(sensorType.equals(Constants.sensor_Orientation)){
			return new Orientation(prefixTPFile, sm);
		}
		if(sensorType.equals(Constants.sensor_Rotation)){
			Log.d(TAG, "sensorTYPE: "+sensorType+" sm is !=null "+(sm!=null));
			return new Rotation(prefixTPFile, sm);
		}
		return null;
	}
}
