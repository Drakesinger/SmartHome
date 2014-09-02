package ch.hearc.smarthome.networktester;

import java.util.Hashtable;

import ch.hearc.smarthome.CredentialManager;
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
 * 
 * @author Horia Mut
 */
public class SHCommunicationProtocol
{

	private static Hashtable<String , Integer>	functions			= new Hashtable<String , Integer>( );

	// Maximum lengths
	public static int							kUsernameMaxLength	= 8;
	public static int							kPasswordMaxLength	= 8;

	//@formatter:off
		
	private static String[ ]					functionNames		= { 
	                        					             		    "create user" ,			// 0
	                        					             		    "login" , 				// 1
	                        					             		    "a change pass" , 		// 2
	                        					             		    "a change username" , 	// 3
	                        					             		    "a get user" , 			// 4
	                        					             		    "d open" , 				// 5
	                        					             		    "d change pass" , 		// 6
	                        					             		    "send post-it" , 
	                        					             		    "delete post-it"
	                        					             		   };

	//@formatter:on

	/** Constructor, builds the {@code functions} hashtable */
	public SHCommunicationProtocol( )
	{
		// Initialize functions hashtable
		if(SHBluetoothNetworkManager.DEBUG) Log.d("Communication Protocol", "constructing Communication Protocol");

		for(int i = 0; i < functionNames.length; i++)
		{
			functions.put(functionNames[i], i);
		}

	}

	private int getFunctionID(String _function)
	{
		Log.d("Communication Protocol", "generateMessage called with " + _function);
		int funcNr = functions.get(_function);

		return funcNr;
	}

	/**
	 * Generates the data to send to the PIC.
	 * 
	 * @param _username
	 *            Username.
	 * @param _function
	 *            Name of the function to be sent.
	 * @param _params
	 *            The parameters to be sent for the function. Parameters need to
	 *            be formated.
	 * @return Formatted String. Format: username,functionID,parameters <br>
	 *         Username will have it's maximum length, filled
	 *         with '*'. If a password is sent, it is also filled up with '*'.
	 * 
	 */
	public String generateDataToSend(String _username, String _function, String _params)
	{
		String dataToSend;
		String generatedUser;
		String generatedPass;

		if(_username != null)
		{
			generatedUser = new String(_username);
			for(int i = 0; i < (kUsernameMaxLength - _username.length( )); i++)
			{
				generatedUser += "*";
			}
			Log.d("String creation", "generatedUser :" + generatedUser);
		}
		else
		{
			generatedUser = null;
			dataToSend = new String("" + getFunctionID(_function));
			return dataToSend;
		}

		if(_function.contentEquals("login"))
		{
			// Only the login function sends the password

			// _params will point to null if we already have a logon
			if(_params != null)
			{
				generatedPass = new String(_params);
				for(int i = 0; i < (kPasswordMaxLength - _params.length( )); i++)
				{
					generatedPass += "*";
				}
				Log.d("String creation", "generatedPass :" + generatedPass);

				// Password was generated, best is to add it to _params
				_params = generatedPass;
				Log.d("SHCommunicationProtocol", "Parameters redefined to generatedPass:" + _params + " =? genPass " + generatedPass);
			}
			dataToSend = new String(generatedUser + "," + getFunctionID(_function));
		}
		else
		{
			generatedPass = null;
		}

		Log.d("SHCommunicationProtocol", "Parameters generated:" + _params);

		dataToSend = new String(generatedUser + "," + getFunctionID(_function) + "," + _params);
		return dataToSend;
	}

	/**
	 * Generates the data to send to the PIC.
	 * 
	 * @param _function
	 *            Name of the function to be sent.
	 * @param _params
	 *            The parameters to be sent for the function. Parameters need to
	 *            be formated.
	 * @return Formatted String. Format: username,functionID,parameters <br>
	 *         Username is the actual user and has it's maximum length, filled
	 *         with '*'. If a password is sent, it is also filled up with '*'.
	 * 
	 */
	public String generateDataToSend(String _function, String _params)
	{
		String dataToSend;
		String generatedUser;
		String generatedPass;

		String actualUser;

		int functionID = getFunctionID(_function);

		switch(functionID)
		{
			case 0:
				Log.d("SHCommunicationProtocol", "generateDataToSend function: " +_function);
				// ask if there are user records on PIC, just send the function
				// ID
				dataToSend = new String("" + getFunctionID(_function));
				break;
			case 1:
				Log.d("SHCommunicationProtocol", "generateDataToSend function: " +_function);
				
				// Login
				actualUser = CredentialManager.getActualUser( );
				generatedUser = generate(actualUser);

				String pass = _params;
				generatedPass = generate(pass);

				_params = generatedPass;
				Log.d("SHCommunicationProtocol", "Parameters redefined to generatedPass:" + _params + " =? genPass " + generatedPass);
				dataToSend = new String(generatedUser + "," + getFunctionID(_function) + "," + _params);

				break;
			default:
				actualUser = CredentialManager.getActualUser( );
				generatedUser = generate(actualUser);
				dataToSend = new String(generatedUser + "," + getFunctionID(_function) + "," + _params);

				break;
		}

		Log.d("SHCommunicationProtocol", "Parameters generated:" + _params);

		return dataToSend;
	}

	private String generate(String _toGen)
	{

		String generatedString = new String(_toGen);
		for(int i = 0; i < (kUsernameMaxLength - _toGen.length( )); i++)
		{
			generatedString += "*";
		}
		Log.d("String creation", "generatedString :" + generatedString);

		return generatedString;
	}

}
