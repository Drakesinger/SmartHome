package ch.hearc.smarthome.networktester;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;


/**
---------------
LOGIN
---------------
login
---------------
ADMIN
---------------
a change pass

[user][password][change pass][old pass][new pass]

a change user
a get users
a delete user
a add user
a block user
a unblock user
---------------
HEATING
---------------
h get temp
h get temp prog
h add temp
h del temp
---------------
DOOR
---------------
d set pass
d change pass
d open
d block user
d unblock user
d get times
---------------
POST-IT
---------------
p get postit
p add postit
p del postit
---------------
VIDEO
---------------


*/
public class SHCommunicationProtocol {


	
	public static Hashtable<String, Integer> functions = new Hashtable<String, Integer>();
	
	private static String[] functionNames = {"login", "change pass","change username", "get users"};
	private String generatedMessage;
	
	public SHCommunicationProtocol() {
		// Initialize functions hashtable
		for (int i = 0; i < functionNames.length; i++) {
			functions.put(functionNames[i], i);
		}
		
	}
	
	private String generateMessage(String _function){
		
		int funcNr = functions.get(_function);
		try {
			generatedMessage = new String("hdha".getBytes("UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return generatedMessage;
		
	}
	
}
