package bzzzt02.participants;


import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bzzzt02.global.Constants;
import bzzzt02.global.DisplayHelper;
import bzzzt02.sensors.SensorHelper;




public class ParticipantHelper {

	public static int getIndexTP(File parentFolder) {
		String[] flist = parentFolder.list();
		
		if(flist==null){return 1;}
		Pattern pattern = Pattern.compile(Constants.regex_TPfilename);

		int cntTemp = -1;

		for (int i = 0; i < flist.length; i++) {
			Matcher matcher = pattern.matcher(flist[i].trim());
			if (matcher.matches()) {
				cntTemp = Math.max(cntTemp, Integer.valueOf(matcher.group(2)));
			}
		}
		return cntTemp+1;
	}
	
	public static String getTimeStamp() {
		return String.valueOf(System.currentTimeMillis());
	}
	
	public static String generateFileName(String prefixTP, String abb_sensor, int indexSample) {

		return prefixTP+"_"+abb_sensor+"_"+
					+ (indexSample+1) + ".data";
	}
	
	public static String generateTPFilePrefix(String filePrefix, int indexTP, String gender,int age){
		DecimalFormat cntTPNbrFormater = new DecimalFormat("000");
		DecimalFormat cntTPAgeFormater = new DecimalFormat("00");
		return filePrefix + cntTPNbrFormater.format(indexTP) + gender + cntTPAgeFormater.format(age);
	}
	
	public static List<String> getParticipantIndexList(File parentFolder){
		
		String[] flist = parentFolder.list();
		Arrays.sort(flist);
		Pattern pattern = Pattern.compile(Constants.regex_TPfilename);
		
		String tp="";
		List<String> kk =  new ArrayList<String>();
		for (int i = 0; i < flist.length; i++) {
			Matcher matcher = pattern.matcher(flist[i].trim());
			if (matcher.matches()) {
				if(!tp.equals(matcher.group(2))){
					kk.add(matcher.group(2));	
				}
			}
		}
		return kk;
	}
	
	public static Participant getPropertiesTP(String index, File parentFolder){
		String[] flist = parentFolder.list();
		Arrays.sort(flist);
		Pattern pattern = Pattern.compile(Constants.regex_TPfilename);
		
		Participant tpx = null;
		int accr = 0;
		int accl = 0;
		int ori = 0;
		int rot = 0;
		for (int i = 0; i < flist.length; i++) {
			Matcher matcher = pattern.matcher(flist[i].trim());
			if (matcher.matches()) {
					
					if(matcher.group(2).equals(index)){
						if(tpx==null){
							tpx=new Participant(Integer.valueOf(matcher.group(2)), Integer.valueOf(matcher.group(4)), matcher.group(3));
						    
						}
						if(matcher.group(5).matches(Constants.sensor_abb_Accelerometer_raw)){
							accr++;
						}
						if(matcher.group(5).matches(Constants.sensor_abb_Accelerometer_linar)){
							accl++;
						}
						if(matcher.group(5).matches(Constants.sensor_abb_Orientation)){
							ori++;
						}
						if(matcher.group(5).matches(Constants.sensor_abb_Rotation)){
							rot++;
						}
						   if(i==flist.length-1){
							   Participant help = tpx;
								if(accr!=0){
									tpx.addSensorSampleCount(Constants.sensor_Accelerometer,accr);
									accr = 0;
								}
								if(accl!=0){
									tpx.addSensorSampleCount(Constants.sensor_Accelerometer_linear,accl);
									accl = 0;
								}
								if(ori!=0){
									tpx.addSensorSampleCount(Constants.sensor_Orientation, ori);
									ori=0;
								}
								if(rot!=0){
									tpx.addSensorSampleCount(Constants.sensor_Rotation, rot);
									ori=0;
								}
								tpx=null;
								return help;
						   }
					}else{
						if(tpx!=null){	
							Participant help = tpx;
							if(accr!=0){
								tpx.addSensorSampleCount(Constants.sensor_Accelerometer,accr);
								accr = 0;
							}
							if(accl!=0){
								tpx.addSensorSampleCount(Constants.sensor_Accelerometer_linear,accl);
								accl = 0;
							}
							if(ori!=0){
								tpx.addSensorSampleCount(Constants.sensor_Orientation, ori);
								ori=0;
							}
							if(rot!=0){
								tpx.addSensorSampleCount(Constants.sensor_Rotation, rot);
								rot=0;
							}
							tpx=null;
							return help;
							
						}
					}
				}
			}
		
		return tpx;
	}
	
	public static List<Participant> getTPList(File dataDir){
		List<String> indexList = ParticipantHelper.getParticipantIndexList(dataDir);
		List<Participant> participants = new ArrayList<Participant>();
		DecimalFormat formater = new DecimalFormat("000");
		for(int i=1; i<=indexList.size();i++){
			participants.add(ParticipantHelper.getPropertiesTP(formater.format(i), dataDir));
		}
		return participants;
	}
	
	public static List<Participant> getTPList02(String sensorName, File parentFolder){
		String[] flist = parentFolder.list();
		Arrays.sort(flist);
		DisplayHelper.displayArray(flist);
			
		Pattern pattern = Pattern.compile(Constants.regex_TPfilename);
		
		String tp = "";
		int cntTPSampleTemp = -1;
		
		Participant ltp = null;
		List<Participant> tlist = new ArrayList<Participant>();
		
		String sensorAbb = SensorHelper.getSenorAbb(sensorName);

		for (int i = 0; i < flist.length; i++) {
			Matcher matcher = pattern.matcher(flist[i].trim());
			if (matcher.matches()) {
				if(!tp.equals(matcher.group(2))){
					tp = matcher.group(2);
					if(ltp!=null){
						if(matcher.group(5).equals(sensorAbb)){
							ltp.addSensorSampleCount(sensorName, cntTPSampleTemp);
						}
					}
					
					ltp = new Participant(Integer.valueOf(tp),Integer.valueOf(matcher.group(4)),matcher.group(3));
					tlist.add(ltp);
					cntTPSampleTemp = 0;
				}
				cntTPSampleTemp++;
			}
		}

		for (int i = 0; i < tlist.size(); i++) {
			Participant TPTemp = (Participant)tlist.get(i);
			if(TPTemp.countSamples.isEmpty()){
				TPTemp.addSensorSampleCount(Constants.sensor_Accelerometer, cntTPSampleTemp);
			}
		}
		return tlist;
	}
	
	public static String formatGender(String gender){
		if(gender.equals("female")){
			return  "f";
		}
		
		if(gender.equals("male")){
			return "m";
		}
		
		return "none";
	}
}
