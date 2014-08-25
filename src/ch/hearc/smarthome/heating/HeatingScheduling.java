package ch.hearc.smarthome.heating;

public class HeatingScheduling {
	
	public String name, date, temp;
	public boolean state;
	
	public HeatingScheduling(){
		super();
	}
	
	//Ca c'est de la classe
	
	public HeatingScheduling(String name, String date, String temp){
		this.name = name;
		this.date = date;
		this.temp = temp;
		this.state = false;
	}
	
}
