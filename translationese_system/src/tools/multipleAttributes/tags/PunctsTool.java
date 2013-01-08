package tools.multipleAttributes.tags;

import config.ConfigManager;
import utils.Utils;

public class PunctsTool extends AttributesTagManager
{
	protected static volatile PunctsTool instance = null;
	
	protected PunctsTool()
	{
		initSet();
	}
	
	public static PunctsTool getInstance()
	{
		if (instance == null)
		{
			System.out.println("start PunctsTool");
			instance = new PunctsTool();
			instance.doOp();
		}
		return instance;
	}
		
	@Override
	protected void compute()
	{
		for (int i=0 ; i < currTokens.length ; ++i)
		{
			String tag = currTags[i];
			String token = currTokens[i];
			if (null == token || token.isEmpty() || token.length() == 0)
			{
				continue;
			}
			String punct = String.valueOf(token.charAt(0));
			
			if (isPunct(punct, tag))
			{
				Utils.incStringIntegerMap(getIntegerMap(), punct);
			}
			if (token.length() > 1)
			{
				String punct_last = String.valueOf(token.charAt(token.length() - 1));
				if (isPunct(punct_last, tag))
				{
					Utils.incStringIntegerMap(getIntegerMap(), punct_last);
				}
			}
		}
	}
	
	@Override
	protected String getFilePath()
	{
		return ConfigManager.getInstance().getPunctutationPath();
	}

	public boolean isPunct(String punct, String tag) 
	{
		try
		{
			return (dataSet.contains(punct) && !tag.equals("POS"));
		} catch (StringIndexOutOfBoundsException e)
		{
			System.out.println("token = ---" + punct + "---");
		}
		return false;
	}
	
	public boolean isPunct(String punct) 
	{
		try{
		return dataSet.contains(punct);
		} catch (StringIndexOutOfBoundsException e)
		{
			System.out.println("token = ---" + punct + "---");
		}
		return false;
	}
}
