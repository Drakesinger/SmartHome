package ch.hearc.smarthome.bluetooth;

/**
 * Class to hold information about a device in order to show it in a ListView:
 * name, MAC address and the signal strength.
 */
public class SHDevice
{
	private String name = "";
	private String address = "";
	

	public SHDevice(String name, String address)
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
