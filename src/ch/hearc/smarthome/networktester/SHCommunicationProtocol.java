package ch.hearc.smarthome.networktester;

import java.util.Hashtable;

import ch.hearc.smarthome.bluetooth.SHBluetoothNetworkManager;

import android.util.Log;

/**
 * This class contains a Hashtable and a String array containing all the
 * functions used in the application.The hashtable is generated when the class
 * constructor is called.
 * <p>
 * In order to send data through bluetooth, the following format must be
 * respected:
 * 
 * <pre>
 * [user],[function ID],[params]
 * </pre>
 * <p>
 * The function ID can be obtained using:
 * <li>{@code getFunctionID(function name)}
 */
public class SHCommunicationProtocol
{

	private static Hashtable<String , Integer>	functions		= new Hashtable<String , Integer>( );

	private static String[ ]					functionNames	= {
						"alreadyLoggedIn" , "login" , "a change pass" ,
						"a change username" , "a get users" , "d open" ,
						"d change pass"						};

	/** Constructor, builds the {@code functions} hashtable */
	public SHCommunicationProtocol( )
	{
		// Initialize functions hashtable
		if(SHBluetoothNetworkManager.DEBUG)Log.d("Communication Protocol", "constructing Communication Protocol");

		for(int i = 0; i < functionNames.length; i++)
		{
			functions.put(functionNames[i], i);
		}

	}

	public int getFunctionID(String _function)
	{
		Log.d("Communication Protocol", "generateMessage called with " + _function);
		int funcNr = functions.get(_function);

		return funcNr;
	}

}
