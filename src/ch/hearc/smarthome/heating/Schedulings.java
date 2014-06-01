package ch.hearc.smarthome.heating;

public class Schedulings {

	public String name, date, temp;
	
	public Schedulings(){
		super();
	}
	
	public Schedulings(String name, String date, String temp){
		this.name = name;
		this.date = date;
		this.temp = temp;
	}
	
}
