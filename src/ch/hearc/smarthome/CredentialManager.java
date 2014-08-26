package ch.hearc.smarthome;

import java.util.Hashtable;

import ch.hearc.smarthome.network.NetworkManager;
import ch.hearc.smarthome.networktester.BluetoothActivity;

public class CredentialManager extends BluetoothActivity {
	
	static String default_pass = "default";
	static String actual_pass = default_pass;
	static String server_pass;

	static String default_user = "user";
	static String actual_user = default_user;
	static String server_user;

	static Hashtable<String,String> cptJackSparrow = new Hashtable<String, String>();
	
	/**
	 * TODO Create either a hashtable containing our passwords with users or
	 * create a dynamic string array for each
	 */

	/**
	 * @return the actual_pass
	 */
	public static  String getActualPass() {
		return actual_pass;
	}

	/**
	 * @param actual_pass
	 *            the actual_pass to set
	 */
	public static void setActualPass(String _password) {
		actual_pass = _password;
		NetworkManager.sendPassword(_password);
		
	}

	public static  String getActualUser() {
		return actual_user;
	}

	public static void setCredential(String _username, String _password) {
		cptJackSparrow.put(_username, _password);
	}
	
	public static String getCredential(String _username) {
		if(cptJackSparrow.containsValue(_username)){
			return (_username +"," + cptJackSparrow.get(_username));
		}else{
			return "no such user";
		}
	}

	public static boolean bUserExists(String _username){
		if(cptJackSparrow.containsValue(_username)){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean bPasswordCorrect(String _username, String _password){
		if (cptJackSparrow.containsValue(_username)) {
			String pass = cptJackSparrow.get(_username);
			if (_password.compareTo(pass) == 0) {
				return true;
			}
		}
		return false;
	}
	
	public static  String getDoorPass() {
		// TODO Auto-generated method stub
		return default_pass;
	}

}
