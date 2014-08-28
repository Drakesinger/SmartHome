package ch.hearc.smarthome;

import java.util.Hashtable;

import android.util.Log;

/**
 * Class that handles all the credentials entered with the app
 * TODO finish documentation
 * 
 * @author Horia Mut
 */
public class CredentialManager
{

	private static final String			TAG					= "CredentialManager";

	public static int					kUsernameMaxLength	= 8;
	public static int					kPasswordMaxLength	= 8;

	static String						default_pass		= "default";
	public static String				actual_pass			= default_pass;
	static String						server_pass;

	static String						default_user		= "user";
	static String						actual_user			= default_user;
	static String						server_user;

	static Hashtable<String , String>	cptJackSparrow		= new Hashtable<String , String>( );

	/**
	 * @return the actual_pass
	 */
	public static String getActualPass( )
	{
		return actual_pass;
	}

	public static void setActualUser(String _username)
	{
		actual_user = _username;
	}

	/**
	 * @param actual_pass
	 *            the actual_pass to set
	 */
	public static void setActualPass(String _password)
	{
		actual_pass = _password;
	}

	public static String getActualUser( )
	{
		return actual_user;
	}

	public static void setCredential(String _username, String _password)
	{
		Log.d(TAG, "setCredential " + _username + " with pass " + _password);

		if(_username.length( ) <= kUsernameMaxLength)
		{
			if(_password.length( ) <= kPasswordMaxLength)
			{
				cptJackSparrow.put(_username, _password);
			}
		}
	}

	public static String getCredential(String _username, boolean bAlreadyLoggedOnce)
	{
		if(cptJackSparrow.containsKey(_username))
		{

			// Need to generate a username and password of the max length
			String generatedUser = new String(_username);

			for(int i = 0; i < (kUsernameMaxLength - _username.length( )); i++)
			{
				generatedUser += "*";
			}
			Log.d("String creation", "generatedUser :" + generatedUser);

			if(!bAlreadyLoggedOnce)
			{
				String generatedPass = new String(cptJackSparrow.get(_username));
				for(int i = 0; i < (kPasswordMaxLength - cptJackSparrow.get(_username).length( )); i++)
				{
					generatedPass += "*";
				}
				Log.d("String creation", "generatedPass :" + generatedPass);
				return(generatedUser + "," + generatedPass);
			}

			return(generatedUser);
		}
		else
		{
			return "no such user";
		}
	}

	public static boolean bIsValid(String _username, String _password)
	{
		if(_username.length( ) <= kUsernameMaxLength)
		{
			if(_password.length( ) <= kPasswordMaxLength)
			{
				return true;
			}
		}
		return false;
	}

	/** Returns true if the user exits */
	public static boolean bUserExists(String _username)
	{
		if(cptJackSparrow.containsKey(_username))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/** Returns true if the password for the user is correct. */
	public static boolean bPasswordCorrect(String _username, String _password)
	{
		if(cptJackSparrow.containsKey(_username))
		{
			String pass = cptJackSparrow.get(_username);
			if(_password.compareTo(pass) == 0)
			{
				return true;
			}
		}
		return false;
	}

	public static String getDoorPass( )
	{
		// TODO Auto-generated method stub
		return default_pass;
	}

}
