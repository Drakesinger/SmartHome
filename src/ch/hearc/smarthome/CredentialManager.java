package ch.hearc.smarthome;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import ch.hearc.smarthome.FileUtil;

import android.util.Log;

/**
 * Class that handles all the credentials entered with the application.
 * 
 * 
 * @author Horia Mut
 */
public class CredentialManager
{

	// Debugging
	private static final String			TAG					= "CredentialManager";

	// Maximum lengths
	public static int					kUsernameMaxLength	= 8;
	public static int					kPasswordMaxLength	= 8;


	// The file where we will write all users and passwords 	//@formatter:off
	private static String SAVE_NAME = "credentials_save.txt";
	private static File SAVE_FILEPATH = new File(FileUtil.SMARTHOME_DIR.getAbsolutePath()
			+ File.separator + SAVE_NAME); //@formatter:on

	// Hashtable containing the users and their passwords
	private static Hashtable<String , String>	cptJackSparrow;
	// = new Hashtable<String , String>( );

	// private static String default_user = "user";
	// private static String default_pass = "default";

	// The Actual connected user and it's password
	private static String						actual_pass			= "user";
	private static String						actual_user			= "default";

	private static String						door_pass			= "1111";

	/**
	 * @return String the actual_pass.
	 */
	public static String getActualPass( )
	{
		return actual_pass;
	}

	/**
	 * @return The actual (application wide) username.
	 */
	public static String getActualUser( )
	{
		return actual_user;
	}

	/**
	 * Sets the actual (application wide) username to the one supplied.
	 * 
	 * @param _username
	 *            the actual username to set.
	 */
	public static void setActualUser(String _username)
	{
		actual_user = _username;
	}

	/**
	 * Sets the actual (application wide) password to the one supplied.
	 * 
	 * @param _password
	 *            the actual password to set.
	 */
	public static void setActualPass(String _password)
	{
		actual_pass = _password;
	}

	/**
	 * Stores the Credential in our hashtable and sets the actual user.<br>
	 * Note: The parameters are saved in the hashtable at their initial length.
	 * 
	 * @param _username
	 *            The username to save.
	 * @param _password
	 *            The password to save.
	 */
	public static void setCredential(String _username, String _password)
	{
		Log.d(TAG, "setCredential " + _username + " with pass " + _password);

		if(_username.length( ) <= kUsernameMaxLength)
		{
			if(_password.length( ) <= kPasswordMaxLength)
			{
				cptJackSparrow.put(_username, _password);
				setActualUser(_username);
				setActualPass(_password);
			}
		}
	}

	/**
	 * Retrieves the credential of a user.
	 * The username and optional password will be returned at their maximal
	 * length. If the length is not reached, the remaining characters will be
	 * filled with '*'.
	 * 
	 * @param _username
	 *            The username to retrieve.
	 * @param bAlreadyLoggedOnce
	 *            Has the user already logged in with the PIC,
	 *            this changed the return value.
	 * @return
	 *         A String containing: [user] if bAlreadyLoggedOnce is True. <br>
	 *         A String containing: [user,pass] if bAlreadyLoggedOnce is False.
	 *         </br>
	 */
	public static String getCredential(String _username, boolean bAlreadyLoggedOnce)
	{

		// Check on SD if there is a hashtable containing everything already
		// written
		if(cptJackSparrow == null)
		{
			return "Captain Jack is in Davy Jones' Locker!";
		}

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

	/**
	 * Checks if the username and password are of the correct length.
	 * 
	 * @param _username
	 * @param _password
	 * @return
	 *         True if the length of both the username and password is
	 *         respected. <br>
	 *         False otherwise.
	 */
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

	/** @return true if the user exits */
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

	/** @return true if the password for the user is correct. */
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

	/**
	 * Sets the door's password for this session (in case the user wants to
	 * change it afterwards).
	 * 
	 * @param _pass
	 *            The password to set.
	 */
	public static void setDoorPass(String _pass)
	{
		door_pass = _pass;
	}

	/**
	 * Used only during door password administration.
	 * 
	 * @returns The password that has been set for the door.
	 */
	public static String getDoorPass( )
	{
		return door_pass;
	}

	/**
	 * Write the credential to disk.
	 * 
	 * @param _username
	 *            the credentials of this username will be appended to the file
	 */
	public static void saveCredential(String _username)
	{
		String textToSave = _username + "," + getActualPass( ) + "\n";
		if(FileUtil.isMediaMounted( ))
		{
			try
			{
				FileUtil.writeTextFile(SAVE_FILEPATH, textToSave, true);
			}
			catch(IOException e)
			{
				e.printStackTrace( );
			}
		}
	}

	/**
	 * Checks if there is already a hashtable written on the SD containing the
	 * user credentials. If there is, it will update the existing hashtable with
	 * the contents of the file, otherwise it will create an empty file and an
	 * empty hashtable.
	 */
	public static void update( )
	{
		// TODO Auto-generated method stub
		String content = null;

		if(FileUtil.isMediaMounted( ))
		{
			if(FileUtil.SMARTHOME_DIR.exists( ))
			{
				if(SAVE_FILEPATH.exists( ))
				{
					try
					{
						content = FileUtil.readTextFile(SAVE_FILEPATH);
					}
					catch(IOException e)
					{
						Log.e("Credential Manager", "File reading error: " + e.getMessage( ));
					}
				}
				else
				{
					// Create File and restart function
					FileUtil.createFile(SAVE_FILEPATH);
					update( );
					return;
				}
			}
			else
			{
				// Create Tree and restart function
				FileUtil.createTree(FileUtil.SMARTHOME_DIR);
				update( );
				return;
			}
		}
		else
		{
			Log.e("Credential Manager", "No SD found");
		}

		if(content.equals(""))
		{
			// No data found
			// Generate the hashtable
			// TODO Check if this is the best way to do this
			cptJackSparrow = new Hashtable<String , String>( );
			return;
		}
		else
		{
			// get all lines in file
			String lines[] = content.split("\n"); // lines[x] =
													// [user];[password]

			// Update the hashtable
			for(String l : lines)
			{
				String s[] = l.split(";");
				cptJackSparrow.put(s[0], s[1]);
			}

		}
	}

}
