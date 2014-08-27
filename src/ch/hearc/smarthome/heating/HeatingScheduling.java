package ch.hearc.smarthome.heating;

public class HeatingScheduling {
	
	private String name, date, temp;
	private boolean state;
	
	public HeatingScheduling(){
		super();
	}
	
	//
	
	public HeatingScheduling(String name, String date, String temp){
		this.name = name;
		this.date = date;
		this.temp = temp;
		this.state = false;
	}
	
	public void setState(boolean state){
		this.state = state;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getDate(){
		return this.date;
	}
	
	public String getTemp(){
		return this.temp;
	}
	
	public boolean getState(){
		return this.state;
	}
	
}
