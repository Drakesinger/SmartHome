package ch.hearc.smarthome.heating;

import org.achartengine.model.XYSeries;

import android.util.Log;

public class HeatingHistoryObject {

	private String date;
	private XYSeries temps;

	public HeatingHistoryObject(){
		super();
	}
	
	public HeatingHistoryObject(String date, XYSeries temps){
		this.date = date;
		//this.temp = temp;
		this.temps = temps;
	}
	
	public String getDate(){
		return this.date;
	}
	
	public XYSeries getTemps(){
		return this.temps;
	}
	
	public String toString(){
		String s = date;
		for(int i = 0; i< 4; i++){
			date += " "+temps.getY(i)+"/";
		}
		return s;
	}
}
