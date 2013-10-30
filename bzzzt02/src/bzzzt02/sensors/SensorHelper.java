package bzzzt02.sensors;

import bzzzt02.global.Constants;



public class SensorHelper {
	public static String getSenorAbb(String sensorName){
		if(sensorName.equals(Constants.sensor_Accelerometer)) return Constants.sensor_abb_Accelerometer;
		if(sensorName.equals(Constants.sensor_abb_Orientation)) return Constants.sensor_abb_Orientation;
		return "none";
	}
}
