package tools.posTag;

import config.ConfigManager;
import tools.WordsTool;


public class SingleNNPTool extends ExplicitNamingTool
{
	protected static volatile SingleNNPTool instance = null;
			
	
	public static SingleNNPTool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start SingleNNPTool");
			instance = new SingleNNPTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void compute()
	{
		for (int i = 0; i < currTokens.length; i++)
		{
			String tag = currTokens[i];
		
			if (isNNP(tag) && testNNPSigularity(i))
			{
				nnp++;
			}
		}
	}
	
	
	private boolean testNNPSigularity(int i)
	{
		if (i != 0)
		{
			if (isNNP(currTokens[i - 1]))
			{
				return false;
			}
		}
		int nextIndex = i + 1;
		if (nextIndex < currTokens.length)
		{
			if (isNNP(currTokens[nextIndex]))
			{
				return false;
			}
		}
		return true;	
	}

	@Override
	protected void doFinalStuff()
	{
		int numOfTokens = WordsTool.getInstance().
				getIntegerMap().get(currFileName);
		int wordsNum = ConfigManager.getInstance().getWordNumber();
		int normalizedVal = (int)Math.round( ((double) nnp / numOfTokens) * wordsNum);
		getIntegerMap().put(currFileName, normalizedVal);
	}
	
}
