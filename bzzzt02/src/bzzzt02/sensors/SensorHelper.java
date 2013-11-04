package bzzzt02.sensors;

import java.util.Iterator;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;
import bzzzt02.global.Constants;
import bzzzt02.participants.ParticipantHelper;



public class SensorHelper {
	private static final String TAG = "SensorHelper";

	public static String getSenorAbb(String sensorName){
		if(sensorName.equals(Constants.sensor_Accelerometer)) return Constants.sensor_abb_Accelerometer_raw;
		if(sensorName.equals(Constants.sensor_Accelerometer_linear)) return Constants.sensor_abb_Accelerometer_linar;
		if(sensorName.equals(Constants.sensor_Orientation)) return Constants.sensor_abb_Orientation;
		if(sensorName.equals(Constants.sensor_Rotation)) return Constants.sensor_abb_Rotation;
		return "none";
	}
	
	public static void showSensorList(SensorManager sm){

		List<Sensor> slist = sm.getSensorList(Sensor.TYPE_ALL);
		Iterator<Sensor> it = slist.iterator();
		while(it.hasNext()){
			Sensor sen = (Sensor)it.next();
			Log.v(TAG,sen.getName()+": "+sen.getType());
		}
	}
	
	public static String generateHeaderString(String filepath, String sensortype) {

		String header = "// " + filepath + "\n" + "// timestamp: "
				+ ParticipantHelper.getTimeStamp() + "\n"
				+ "// "+sensortype+" data is arranged x;y;z" + "\n";
		return header;
	}
}
