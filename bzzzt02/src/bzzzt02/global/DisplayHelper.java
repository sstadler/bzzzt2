package bzzzt02.global;

import java.util.Iterator;
import java.util.List;
import android.content.Intent;
import android.util.Log;

import bzzzt02.participants.Participant;


public class DisplayHelper {
	
	public static void displayErrorList(String TAG, List<String> errors) {
		if(!errors.isEmpty()) {
			Iterator<String> i = errors.iterator();
			log(TAG, "***************Display Errors**");
			while (i.hasNext()) {
				String o = i.next();
				log(TAG, o);
			}
			log(TAG, "*******************************");	
		}
	}
	
	public static void log(String tag, Object o) {
		Log.v(tag,String.valueOf(o));
		//System.out.println(tag + ": " + String.valueOf(o));
	}
	
	public static void displayTPList(String TAG, List<Participant> listtp) {
		if (!listtp.isEmpty()) {
			Iterator<Participant> i = listtp.iterator();
			log(TAG, "***************Display Participants infos**");
			while (i.hasNext()) {
				Participant o = i.next();
				if(o!=null){
				log(TAG, "---index: "+ o.indexTP);
				log(TAG, "age: "+o.age+" gender: "+o.gender);
				log(TAG, "data count: "+o.countSamples);
				}
			}
			log(TAG, "*******************************");
		}
	}
	
	public static void displayArray(String[] arr) {
		
		for(int i=0; i<arr.length;i++){
			System.out.println(arr[i]);
		}

	}
	
	public static void displayIntentExtras(Intent itn){
		System.out.println("Intent==null: "+(itn==null));
		System.out.println("Intentaction: "+itn.getAction());
		System.out.println("has Extra tp: "+itn.hasExtra("tp"));
		System.out.println("has Extra sameUsr "+itn.hasExtra("sameUsr"));
	}
}
