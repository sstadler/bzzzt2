package bzzzt02.infos;

import java.util.List;

import bzzzt02.global.R;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class SensorList extends Activity implements SensorEventListener{
	private SensorManager slSensorManager;
	private TextView slTextView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensorlist_bzzzt02);
		slSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		slTextView = (TextView) findViewById(R.id.tv_sensorList);

		List<Sensor> mList
        = slSensorManager.getSensorList(Sensor.TYPE_ALL);
		for (int i = 1; i < mList.size(); i++) {
			slTextView.append("\n" + mList.get(i).getName());
			}
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	protected void onPause(){
		super.onStop();
	}
	protected void onStop() {
		super.onStop();
	}
	protected void onResume()
	{
		super.onResume();
	}

}
