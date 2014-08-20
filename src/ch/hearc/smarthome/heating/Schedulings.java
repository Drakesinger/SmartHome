package ch.hearc.smarthome.heating;

public class Schedulings {
	
	public String name, date, temp;
	public boolean state;
	
	public Schedulings(){
		super();
	}
	
	//Ca c'est de la classe
	
	public Schedulings(String name, String date, String temp){
		this.name = name;
		this.date = date;
		this.temp = temp;
		this.state = false;
	}
	
}
