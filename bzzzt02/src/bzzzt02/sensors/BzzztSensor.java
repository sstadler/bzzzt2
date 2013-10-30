package bzzzt02.sensors;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bzzzt02.config.ConfigData;
import bzzzt02.global.Constants;
import bzzzt02.global.DisplayHelper;
import bzzzt02.participants.Participant;
import bzzzt02.participants.ParticipantHelper;



public abstract class BzzztSensor {
	
	public static final String TAG="SENSOR";
	private ConfigData configfile;
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
	
	public BzzztSensor(){
		initParams();
	}
	
	public void initParams() {
		configfile = ConfigData.getInstance();
		configfile.loadConfig("/mnt/sdcard/Download/bzzzt.config");
		parentFolder = new File(configfile.getValue(Constants.config_TPFOLDERPATH)
				.toString());
		if(!parentFolder.exists()){parentFolder.mkdir();}
		filePrefix = configfile.getValue(Constants.config_FILEPREFIX).toString();
		timeStamp  = "";
		
		maxNumSamples = Integer.valueOf(configfile.getValue(Constants.config_MAXNUMBERSAMPLES).toString());
		countTP       = ParticipantHelper.getIndexTP(parentFolder); //delete
		errors        = new ArrayList<String>();
		System.out.println("inti SENSOR");
	}
	
//	public int getCountTP() {
//		String[] flist = parentFolder.list();
//		Pattern pattern = Pattern.compile(Globals.regex_TPfilename);
//
//		int cntTemp = -1;
//
//		for (int i = 0; i < flist.length; i++) {
//			Matcher matcher = pattern.matcher(flist[i].trim());
//			if (matcher.matches()) {
//				cntTemp = Math.max(cntTemp, Integer.valueOf(matcher.group(2)));
//			}
//		}
//		return cntTemp+1;
//	}
	

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
	

}
