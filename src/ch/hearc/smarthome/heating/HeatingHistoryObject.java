package ch.hearc.smarthome.heating;

public class HeatingHistoryObject {

	private String date, temp;
	
	public HeatingHistoryObject(){
		super();
	}
	
	public HeatingHistoryObject(String date, String temp){
		this.date = date;
		this.temp = temp;
	}
	
	public String getDate(){
		return this.date;
	}
	
	public String getTemp(){
		return this.temp;
	}
}
