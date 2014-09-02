package ch.hearc.smarthome.networktester;

/**
 * This class holds an activity with its title, mDescription and class name,
 * it's
 * used to show the user the available activities in the ActivityList class.
 */
public class SHActivityC
{
	private String	mActivity		= "";
	private String	mDescription	= "";
	private String	mPackageName	= "";
	private String	mClassName		= "";

	/**
	 * Create a new possible mActivity.
	 * 
	 * @param _name
	 *            The name of the mActivity.
	 * @param _description
	 *            A mDescription of the mActivity created.
	 * @param _className
	 *            The java .class name.
	 */
	public SHActivityC(String _name, String _description, String _packageName, String _className)
	{
		mActivity = _name;
		mDescription = _description;
		mPackageName = _packageName;
		mClassName = _className;
	}

	public String getCActivity( )
	{
		return mActivity;
	}

	public String getDescription( )
	{
		return mDescription;
	}

	public String getPackageName( )
	{
		return mPackageName;
	}

	public String getClassName( )
	{
		return mClassName;
	}
}
