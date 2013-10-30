package bzzzt02.sensors;

import android.hardware.SensorManager;
import bzzzt02.global.Constants;



public class SensorFactory {
	public BzzztSensor getSensor(String sensorType, String prefixTPFile, SensorManager sm){
		if(sensorType == null){
			return null;
		}
		if(sensorType.equals(Constants.sensor_Accelerometer)){
			return new Accelerometer(prefixTPFile, sm);
		}
		if(sensorType.equals(Constants.sensor_Orientation)){
			return new Orientation(prefixTPFile, sm);
		}
		return null;
	}
}
