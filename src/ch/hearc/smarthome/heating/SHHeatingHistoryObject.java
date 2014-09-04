package ch.hearc.smarthome.heating;

import org.achartengine.model.XYSeries;

import android.util.Log;

public class SHHeatingHistoryObject {

	private String date;
	private XYSeries temps;
	private int color;

	public SHHeatingHistoryObject(){
		super();
	}
	
	public SHHeatingHistoryObject(String date, XYSeries temps){
		this.date = date;
		//this.temp = temp;
		this.temps = temps;
		this.color = 0;
	}
	
	public String getDate(){
		return this.date;
	}
	
	public XYSeries getTemps(){
		return this.temps;
	}
	
	public void setColor(int color){
		this.color = color;
	}
	
	public int getColor(){
		return this.color;
	}
	
	public String toString(){
		String s = date;
		for(int i = 0; i< 4; i++){
			date += " "+temps.getY(i)+"/";
		}
		return s;
	}
}
