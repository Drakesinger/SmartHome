package ch.hearc.smarthome;

import ch.hearc.smarthome.network.NetworkManager;

public class PasswordManager {

	static String default_pass = "default";
	static String actual_pass;
	static String server_pass;

	/**
	 * @return the actual_pass
	 */
	public static String getActual_pass() {
		return actual_pass;
	}

	/**
	 * @param actual_pass
	 *            the actual_pass to set
	 */
	public static void setActual_pass(String pass) {
		PasswordManager.actual_pass = pass;
		NetworkManager.send_password();
	}

}
