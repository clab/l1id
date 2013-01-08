package tools.multipleAttributes;


import utils.Utils;
import config.ConfigManager;

public class FWTool extends PronounsTool
{
	protected static volatile FWTool instance = null;
	protected int i = 0;
	
	public static FWTool getInstance() 
    {
		if (instance == null) {
			System.out.println("start FWTool");
			instance = new FWTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void compute()
	{		
		if (currTokens.length == 0)
			return;
		for (i = 0; i < currTokens.length; ++i)
		{
			String token = getToken();
			if (null != token && !"".equals(token ))
			{
				Utils.incStringIntegerMap(getIntegerMap(), token);
			}
		}	
	}
	
	
	private String getToken()
	{
		String token = currTokens[i].toLowerCase();
		if (i + 1 < currTokens.length)
		{
			String jointToken = token + currTokens[i+1];
			if (dataSet.contains(jointToken))
			{
				++i;
				return jointToken;
			}
		}
		
		if (dataSet.contains(token))
		{
			return token;
		}
		
		return null;
	}
	
	@Override
	protected String getFilePath()
	{
		return ConfigManager.getInstance().getFuncWordsPath();
	}
}
