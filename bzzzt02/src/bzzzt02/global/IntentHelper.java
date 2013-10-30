package bzzzt02.global;

import android.content.Intent;

public class IntentHelper {
	public static Intent addTPInfo2Intent(Intent itn, String tpIndex, String sampleIndex){
		itn.putExtra(Constants.extra_TPindex, tpIndex);
    	itn.putExtra(Constants.extra_SAMPLEindex, sampleIndex);	
		return itn;
	}

}
