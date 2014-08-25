package ch.hearc.smarthome.networktester;

/**
 * Class to hold information about a device in order to show it in a ListView:
 * name, MAC address and the signal strength.
 */
public class Device
{
	private String name = "";
	private String address = "";
	

	public Device(String name, String address)
	{
		this.name = name;
		this.address = address;
		
	}

	public String getName()
	{
		return name;
	}

	public String getAddress()
	{
		return address;
	}


}
