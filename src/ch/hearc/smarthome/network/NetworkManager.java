package ch.hearc.smarthome.network;

import ch.hearc.smarthome.PasswordManager;

;

public class NetworkManager {


	public static boolean send_password() {
		// TODO Auto-generated method stub
		String passToSend = PasswordManager.getActual_pass();
		
		return true;
	}

}
