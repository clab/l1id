package tools.multipleAttributes;

import utils.Utils;


import config.ConfigManager;

public class PronounsTool extends AttributesManager
{
	protected static volatile PronounsTool instance = null;
	
	protected PronounsTool()
	{
		initSet();
	}

	public static PronounsTool getInstance() 
    {
		if (instance == null) {
			System.out.println("start PronounsTool");
			instance = new PronounsTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void compute()
	{
		for (String token : currTokens)
		{
			String lowercaseToken = token.toLowerCase();
			if (dataSet.contains(lowercaseToken))
			{
				Utils.incStringIntegerMap(getIntegerMap(), lowercaseToken);
			}
		}
		
	}

	@Override
	protected String getFilePath()
	{
		return ConfigManager.getInstance().getPronounsPath();
	}
}
