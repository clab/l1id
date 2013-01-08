package common;

import java.util.LinkedList;
import java.util.List;

import Exceptions.ToolsException;

public enum Classification
{
	/*TRANSLATED("+1", "Original"),
	ORIGINAL("-1", "Translated");*/
	
	TRANSLATED("+1", "Translated"),
	ORIGINAL("-1", "Original");
	
	private String classLabel;
	private String className;
	
	private Classification(String label, String name)
	{
		classLabel = label;
		className = name;
	}
	
	public static Classification getType(String type) throws ToolsException
	{
		//System.out.println("type = " + type);
		if (type.toLowerCase().startsWith("eng") ||
				type.equalsIgnoreCase("O") ||
				type.toLowerCase().startsWith("human_orig"))
		{
			//System.out.println("orig");
			return ORIGINAL;
		}
		else
			return TRANSLATED;
		//if (type.toLowerCase().startsWith("hebrew"))
		//	return TRANSLATED;
		//throw new ToolsException("Classification type of file not known: " + type);
	}
	
	public String getClassLabel()
	{
		return classLabel;
	}
	
	public String getClassName() 
	{
		return className;
	}
	
	public static Classification[] getValues()
	{
		List<Classification> values = new LinkedList<Classification>();
		
		/*if (ConfigManager.getInstance().isClassifyByLang())
		{
			values.add(ENGLISH);
			values.add(KOREAN);
			values.add(GREEK);
			values.add(HEBREW);
		}
		else*/
		{
			values.add(ORIGINAL);
			values.add(TRANSLATED);
		}
		return (Classification[])values.toArray(new Classification[0]);
	}
	
	/*public boolean isTranslated()
	{
		if (this == ENGLISH )
			return false;
		return true;
	}*/
}
