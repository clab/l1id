package tools;

import java.io.IOException;

public class TokensTool extends MapTool
{
	protected static volatile TokensTool instance = null;
	
	private int numOfTokens = 0;
	
	
	protected TokensTool()
	{
	}
	
	public static TokensTool getInstance()
	{
		if (instance == null) {
			System.out.println("start TokensTool");
			instance = new TokensTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void compute() throws Exception
	{
		numOfTokens += currTokens.length;
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
