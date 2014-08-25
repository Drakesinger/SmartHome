package ch.hearc.smarthome.networktester;

/**
 * This class holds an activity with its title, description and class name, it's
 * used to show the user the available activities in the ActionSelectActivity
 * class.
 */
public class Action
{
	private String action = "";
	private String descripiton = "";
	private String className = "";

	public Action(String name, String descripiton, String className)
	{
		this.action = name;
		this.descripiton = descripiton;
		this.className = className;
	}

	public String getAction()
	{
		return action;
	}

	public String getDescripiton()
	{
		return descripiton;
	}

	public String getClassName()
	{
		return className;
	}
}
