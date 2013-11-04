package bzzzt02.global;

import android.content.Intent;
import android.util.Log;

public class IntentHelper {
	private static final String TAG="IntentHelper";
	public static Intent addTPInfo2Intent(Intent itn, String tpIndex, String sampleIndex){
		itn.putExtra(Constants.extra_TPindex, tpIndex);
    	itn.putExtra(Constants.extra_SAMPLEindex, sampleIndex);	
		return itn;
	}

	public static Intent generateSensorValIntent(String sensorType, String[] vals){
		Intent itn = new Intent();
		if(sensorType.equals(Constants.sensor_Accelerometer)){
			itn.setAction("accData");
			for(int i=0; i<vals.length;i++){
				Log.d(TAG,"i: "+vals[i]);
				switch(i){
					case(0):itn.putExtra("x", vals[i]);break;
					case(1):itn.putExtra("y", vals[i]);break;
					case(2):itn.putExtra("z", vals[i]);break;
					default: break;
				}
			}
		}
		return itn;
	}
}
