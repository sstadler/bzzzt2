package bzzzt02.global;


public class Constants {
	public static final String config_DEBUG            = "debug";
	public static final String config_TPFOLDERPATH     = "tpFolderPath";
	public static final String config_FILEPREFIX       = "filePrefix";
	public static final String config_MAXNUMBERSAMPLES = "maxNumberSamples";
	
	public static final String regex_true       = "true";
	public static final String regex_false      = "false";
	public static final String regex_path       = "^(.*/)([^/]*)$";
	public static final String regex_TPfilename = "(.{2})(\\d{1,3})(m|f)(\\d{2})\\_([a-z]{2})\\_(\\d*)\\.data";
	public static final String regex_age        = "\\d{1,2}";
	
	public static final String sensor_Accelerometer = "ACCELEROMETER";
	public static final String sensor_Orientation   = "ORIENTATION";
	
	public static final String sensor_abb_Accelerometer = "ad";
	public static final String sensor_abb_Orientation   = "od";
	
	public static final String extra_TPindex     = "TPINDEX";
	public static final String extra_SAMPLEindex = "SAMPLEINDEX";
	public static final String extra_NEWTP       = "NEWTP";
	public static final String extra_PARTICIPANT = "TP";
	
	
	//public final static Charset ENCODING_utf8 = StandardCharsets.UTF_8;
}
