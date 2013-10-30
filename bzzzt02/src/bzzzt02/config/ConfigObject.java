package bzzzt02.config;

import java.util.Arrays;
import java.util.List;

import bzzzt02.global.Constants;


public class ConfigObject {
	
	private List<String> lstValidParamNames = Arrays.asList(Constants.config_DEBUG,Constants.config_TPFOLDERPATH,Constants.config_FILEPREFIX);
	
	public String name;
	public Object val;
	
	public boolean processed;
	public String errorMsg;

	public ConfigObject(){
		initParams();
	}
	
	public ConfigObject(String name, String val) {
		this.name = name;
		this.val  = val;
		processData();
	}
	
	public void initParams(){
		name =null;
		val = null;
		processed = false;
		errorMsg = "";
	}
	
	public String getErrorMsg(){
		return errorMsg;
	}
	
	
	public Object getValue(String paramName){
		if(paramName.equals(Constants.config_DEBUG)){
			return Boolean.parseBoolean(val.toString());
		}
		return val;
	}
	
	public boolean isParamNameValid(String paramName){
		return lstValidParamNames.contains(paramName);
	}
	
	public boolean isParamValueValid(String paramValue, String regex){
		return paramValue.matches(regex);		
	}
	
	private void processData(){
		if(name.trim().equals(Constants.config_DEBUG)){
			
			if(val.toString().matches("true")||val.toString().matches("false")){
				val = Boolean.parseBoolean(val.toString());
				processed = true;
			}else{
				processed = false;
				errorMsg+="debug not set, value not valid: "+val;
			}
		}
		else if (name.equals(Constants.config_TPFOLDERPATH)) {
			processed = true;
		}
		
		else if(name.equals(Constants.config_FILEPREFIX)){
			processed = true;
		}
		
		else if(name.equals(Constants.config_MAXNUMBERSAMPLES)){
			processed = true;
		}
		
		else{
			errorMsg+=name+" "+val+" not known!!";
			processed = false;
		}
	}

}
