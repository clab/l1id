package tools;

import tools.multipleAttributes.tags.PunctsTool;

public class WordsTool extends MapTool
{
	protected static volatile WordsTool instance = null;
	
	private int numOfTokens = 0;
	
	
	protected WordsTool()
	{
	}
	
	public static WordsTool getInstance()
	{
		if (instance == null) {
			System.out.println("start TokensTool");
			instance = new WordsTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void compute() throws Exception
	{
		for (int i=0 ; i < currTokens.length ; ++i)
		{
			String token = currTokens[i];
			if (token.isEmpty())
			{
				continue;
			}
			if (!PunctsTool.getInstance().isPunct(token)) 
			{
				numOfTokens ++;
			}
		}
	}
	
	@Override
	protected void doFinalStuff()
	{
		getIntegerMap().put(currFileName, numOfTokens);
	}
		
	@Override
	protected void initForFile() 
	{
		numOfTokens = 0;
	}
	
}
