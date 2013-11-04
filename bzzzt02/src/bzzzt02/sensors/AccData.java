package bzzzt02.sensors;

public class AccData {
	public static final String TAG = "AccData";
	
	private float x=0f;
	private float y=0f;
	private float z=0f;
	
	private float[] data = new float[3];
	
	public AccData(float[] fa){
		this.x = fa[0];
		this.y = fa[1];
		this.z = fa[2];
	}
	
	public boolean checkCompareZ(float zt1){
		if(z<zt1){return true;}
		return false;
	}
	
	public boolean checkCompareX(float xt1){
		if(x<xt1){return true;}
		return false;
	}
	
	public boolean checkCompareY(float yt1){
		if(y<yt1){return true;}
		return false;
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public float getZ(){
		return z;
	}
}
