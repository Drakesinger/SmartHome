package ch.hearc.smarthome;

import ch.hearc.smarthome.network.NetworkManager;

public class CredentialManager {

	static String default_pass = "default";
	static String actual_pass = default_pass;
	static String server_pass;

	static String default_user = "user";
	static String actual_user = default_user;
	static String server_user;

	/**
	 * TODO Create either a hashtable containing our passwords with users or
	 * create a dynamic string array for each
	 */

	/**
	 * @return the actual_pass
	 */
	public static String getActualPass() {
		return actual_pass;
	}

	/**
	 * @param actual_pass
	 *            the actual_pass to set
	 */
	public static void setActualPass(String password) {
		actual_pass = password;
		NetworkManager.sendPassword(password);
	}

	public static String getActualUser() {
		return actual_user;
	}

	public static void setCredential(String username, String password) {
		actual_pass = password;
		actual_user = username;
	}

	public static String getDoorPass() {
		// TODO Auto-generated method stub
		return default_pass;
	}

}
