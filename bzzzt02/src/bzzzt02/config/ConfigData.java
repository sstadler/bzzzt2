package bzzzt02.config;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import bzzzt02.global.Constants;
import bzzzt02.global.DisplayHelper;

public class ConfigData {
	private final static String TAG = "ConfigData";

	private static ConfigData instance;

	private List<String> errors;
	private File file;
	public boolean loaded;

	boolean debug;

	private List<ConfigObject> lstConfigObjects = new ArrayList<ConfigObject>();

	public ConfigData() {
		initParams();
	}

	public static synchronized ConfigData getInstance() {
		if (instance == null) {
			instance = new ConfigData();
		}
		return instance;
	}
	
	public void initParams(){
		instance = null;
		errors   = new ArrayList<String>();
		file     = null;
		loaded   = false;
		debug    = false;
		if (debug)
			DisplayHelper.log(TAG,"params initialized");
	}

	public boolean isConfigLoaded() {
		return loaded;
	}

	public void loadConfig(String cfile) {
		readAndProcessConfig(cfile);
		checkFileLoaded();
		if (!errors.isEmpty()) {
			DisplayHelper.log(TAG, "... Configfile errors during loading");
			displayErrorList();
			loaded = false;
			return;
		}
		loaded = true;
		debug = (Boolean) getValue(Constants.config_DEBUG);
		DisplayHelper.log(TAG, "... Configfile completly loaded");
		if (debug)
			displayConfig();
	}

	public Object getValue(String paramName) {
		Iterator<ConfigObject> itr = lstConfigObjects.iterator();
		while (itr.hasNext()) {
			ConfigObject tmp = (ConfigObject) itr.next();
			if (tmp.name.equals(paramName)) {
				return tmp.getValue(paramName);
			}
		}
		return null;
	}
	
	public int getMaxNumberSample(){
		return Integer.valueOf(getValue(Constants.config_MAXNUMBERSAMPLES).toString());
	}
	public String getTPSamplePath(){
		return getValue(Constants.config_TPFOLDERPATH).toString();
	}
	public int getWaitInSec(){
		return Integer.valueOf(getValue(Constants.config_WAITINSEC).toString());
	}

	private void displayErrorList() {
		Iterator<String> i = errors.iterator();
		DisplayHelper.log(TAG, "***************Display Errors**");
		while (i.hasNext()) {
			String o = i.next();
			DisplayHelper.log(TAG, o);
		}
		DisplayHelper.log(TAG, "*******************************");
	}

	private void displayConfig() {
		Iterator<ConfigObject> i = lstConfigObjects.iterator();
		DisplayHelper.log(TAG, "***************Display Config**");
		while (i.hasNext()) {
			ConfigObject o = (ConfigObject) i.next();
			DisplayHelper.log(TAG, String.format("%-13s = %s; \t(processed: %s)", o.name,
					o.val, o.processed));
		}
		DisplayHelper.log(TAG, "*******************************");
	}

	private void readAndProcessConfig(String cfile) {
		File configfile = new File(cfile);
		try {
			file = configfile;
			processFile();
		} catch (FileNotFoundException e) {
			errors.add("config file not found: " + configfile.getAbsolutePath());
		}
	}

	private final void processFile() throws FileNotFoundException {
		System.setProperty("file.encoding", "UTF-8");
		Scanner sc = new Scanner(file);
		if(sc!=null){
			while (sc.hasNextLine()) {
				String tmpline = sc.nextLine();
				if (tmpline.isEmpty()) {
					continue;
				}
				if (tmpline.startsWith("//")) {
					continue;
				}

				processLineByLine(tmpline);
			}
		}
	}

	private void processLineByLine(String line) {
		Scanner scanner = new Scanner(line);
		scanner.useDelimiter("=");
		if (scanner.hasNext()) {
			String name = scanner.next().trim();
			String val  = scanner.next().trim();
			lstConfigObjects.add(new ConfigObject(name, val));

		} else {
			errors.add("Empty or invalid line. Unable to process");
		}
		scanner.close();
	}

	private void checkFileLoaded() {
		Iterator<ConfigObject> i = lstConfigObjects.iterator();
		while (i.hasNext()) {
			ConfigObject o = (ConfigObject) i.next();
			if (o.processed == false) {
				errors.add(o.getErrorMsg());
			}
		}
	}

	public static void main(String[] args) {
		ConfigData cdata = ConfigData.getInstance();
		cdata.loadConfig("/tmp/bzzzt.config");
	}

}
