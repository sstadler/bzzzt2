package bzzzt02.participants;

import java.util.HashMap;

import android.hardware.SensorManager;
import android.os.Parcel;
import android.os.Parcelable;
import bzzzt02.config.ConfigData;
import bzzzt02.sensors.BzzztSensor;
import bzzzt02.sensors.SensorFactory;

public class Participant implements Parcelable {
	public int indexTP;
	public int age;
	public String prefixTPFile;
	public String gender;
	public String filePrefix, timeStamp;
	public HashMap<String, Integer> countSamples;
	public HashMap<String, BzzztSensor> sensors;
	SensorFactory sf;
	ConfigData configfile;

	public Participant(int index, int age, String gender) {
		initParams();
		this.indexTP = index;
		this.gender = gender;
		this.age = age;
		prefixTPFile = ParticipantHelper.generateTPFilePrefix("TP", indexTP,
				gender, age);
	}

	public Participant(Parcel in) {
		initParams();
		readFromParcel(in);
		prefixTPFile = ParticipantHelper.generateTPFilePrefix("TP", indexTP,
				gender, age);
	}

	public void initParams() {
		indexTP = -1;
		gender = "";
		age = -1;
		countSamples = new HashMap<String, Integer>();
		sf = new SensorFactory();
		sensors = new HashMap<String, BzzztSensor>();
	}

	public void initSensor(String sensorType, SensorManager sm, int sensorSubType) {
		if (!sensors.containsKey(sensorType)) {
			sensors.put(sensorType, sf.getSensor(sensorType, prefixTPFile, sm, sensorSubType));
		}
	}

	public void addSensorSampleCount(String sensor, int count) {
		countSamples.put(sensor, count);
	}

	public static final Parcelable.Creator<Participant> CREATOR = new Parcelable.Creator<Participant>() {
		public Participant createFromParcel(Parcel in) {
			return new Participant(in);
		}

		@Override
		public Participant[] newArray(int size) {
			return new Participant[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(indexTP);
		dest.writeInt(age);
		dest.writeString(prefixTPFile);
		dest.writeString(gender);
		dest.writeString(filePrefix);
		dest.writeString(timeStamp);
		dest.writeMap(countSamples);
		dest.writeMap(sensors);
	}

	@SuppressWarnings("unchecked")
	public void readFromParcel(Parcel in) {
		indexTP = in.readInt();
		age = in.readInt();
		prefixTPFile = in.readString();
		gender = in.readString();
		filePrefix = in.readString();
		timeStamp = in.readString();
		countSamples = in.readHashMap(Integer.class.getClassLoader());
		sensors = in.readHashMap(BzzztSensor.class.getClassLoader());
	}
}
