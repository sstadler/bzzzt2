package bzzzt02.sensors;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.util.Log;
import bzzzt02.config.ConfigData;
import bzzzt02.global.Constants;
import bzzzt02.global.DisplayHelper;
import bzzzt02.participants.Participant;
import bzzzt02.participants.ParticipantHelper;



public abstract class BzzztSensor {
	
	public static final String TAG="SENSOR";
	
	private ConfigData configfile;
	private File externalStorageDir;
	private String downloadDir;
	public File parentFolder;
	String filePrefix, timeStamp;
	String gender;
	int age;
	public int countTP, maxNumSamples;
	
	
	private List<String> errors;
	
	public abstract void createNopenFile();
	public abstract void writeHaeder();
	public abstract void writeData(float x, float y, float z);
	public abstract void closeFile();
	public abstract List<Participant> getTPInfoList();
	public abstract int getSampleIndex();
	public abstract void resetSampleIndex();
	public abstract boolean checkFinished();
	public abstract boolean checkMovement();
	public abstract void stopRecord();
	public abstract String[] getSensorValues();
	public abstract void logHoldState();
	
	public BzzztSensor(){
		initParams();
	}
	
	public void initParams() {
        loadConfig();
        parentFolder = new File(configfile.getTPSamplePath());
		if(!parentFolder.exists()){parentFolder.mkdir();}
		filePrefix = configfile.getValue(Constants.config_FILEPREFIX).toString();
		timeStamp  = "";
		
		maxNumSamples = Integer.valueOf(configfile.getMaxNumberSample());
		countTP       = ParticipantHelper.getIndexTP(parentFolder); //delete
		errors        = new ArrayList<String>();
		Log.d(TAG,"inti SENSOR");
	}

	public File createFile(String filename) {

		File ftemp = new File(parentFolder
				+"/"+ filename);
			try {
				if (ftemp.createNewFile()) {
					return ftemp;
				}
			} catch (IOException e) {
				errors.add("IOExeption in create File: " + e.getMessage());
			} finally {
				System.out.println("size Erros: "+errors.size());
				DisplayHelper.displayErrorList(TAG,errors);
			}

		return null;
	}


	public BufferedWriter openFile(File currFile) {
		
			FileWriter fwOut;
			BufferedWriter bwriter = null;
			try {
				fwOut   = new FileWriter(currFile);
				bwriter = new BufferedWriter(fwOut);
			} catch (IOException e) {
				errors.add("IOExeption in open File: " + e.getMessage());
			} catch (NullPointerException e) {
				errors.add("NullPointerException in open File: " + e.getMessage());
			}
			
			return bwriter;
	}
	
	private void loadConfig(){
		externalStorageDir = Environment.getExternalStorageDirectory();
		downloadDir = "/Download/bzzzt.config";
		configfile = ConfigData.getInstance();
		if(!configfile.loaded){
			configfile.loadConfig(externalStorageDir.getAbsoluteFile() + downloadDir);
		}
	}
	

}
