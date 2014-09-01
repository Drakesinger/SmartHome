package ch.hearc.smarthome.networktester;

/**
 * This class holds an activity with its title, description and class name, it's
 * used to show the user the available activities in the ActionSelectActivity
 * class.
 */
public class SHAction
{
	private String action = "";
	private String description = "";
	private String packageName = "";
	private String className = "";

	/**Create a new possible action.
	 * @param _name The name of the action.
	 * @param _description A description of the action created.
	 * @param _className The java .class name.*/
	public SHAction(String _name, String _description,String _packageName, String _className)
	{
		action = _name;
		description = _description;
		packageName = _packageName;
		className = _className;
	}

	public String getAction()
	{
		return action;
	}

	public String getDescription()
	{
		return description;
	}

	public String getPackageName(){
		return packageName;
	}
	
	public String getClassName()
	{
		return className;
	}
}
