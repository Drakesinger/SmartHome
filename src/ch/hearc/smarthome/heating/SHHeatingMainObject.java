package ch.hearc.smarthome.heating;

public class SHHeatingMainObject {
	
	private int drawable;
	private String title;
	
	public SHHeatingMainObject(int drawable, String title){
		this.drawable = drawable;
		this.title = title;
	}
	
	public int getDrawable(){
		return this.drawable;
	}
	
	public String getTitle(){
		return this.title;
	}

}
