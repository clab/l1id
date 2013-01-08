package tools.posTag;

import config.ConfigManager;
import tools.multipleAttributes.tags.AttributesTagManager;
import utils.Utils;

public class _PunctsTool extends AttributesTagManager
{
	protected static volatile _PunctsTool instance = null;
	
	protected _PunctsTool() throws Exception 
	{
		initSet();
	}
	
	public static _PunctsTool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start PunctsTool");
			instance = new _PunctsTool();
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
			if (token.isEmpty())
			{
				continue;
			}
			String punct = String.valueOf(token.charAt(0));
			if (dataSet.contains(punct)) 
			{
				if (!tag.equals("POS"))
				{
					Utils.incStringIntegerMap(getIntegerMap(), punct);
				}
			}
		}
	}
	
	@Override
	protected String getFilePath()
	{
		return ConfigManager.getInstance().getPunctutationPath();
	}
}
