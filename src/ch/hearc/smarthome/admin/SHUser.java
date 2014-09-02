package ch.hearc.smarthome.admin;

/**
 * Class to hold information about a user.
 */
public class SHUser
{
	private String	mUserName	= "";
	private String	mPassword	= "";

	public SHUser(String _username, String _password)
	{
		mUserName = _username;
		mPassword = _password;

	}

	public String getUserName( )
	{
		return mUserName;
	}

	public String getPassword( )
	{
		return mPassword;
	}

}
