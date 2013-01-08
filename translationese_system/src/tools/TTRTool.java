package tools;

import java.io.File;


public class TTRTool extends MapTool 
{
	protected static volatile TTRTool instance = null;
	
	protected TTRTool() throws Exception 
	{
		setOrder(6);
	}
	
	public static TTRTool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start TTRTool");
			instance = new TTRTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void doForFile(File file) throws Exception 
	{
		int numOfTokens = TokensTool.getInstance().getIntegerMap().get(currFileName);
		int numOfTypes = TypesTool.getInstance().getIntegerMap().get(currFileName);
		
		double ttr = ((double)numOfTypes/numOfTokens);
		
		getIntegerMap().put(currFileName, normalize(ttr));
	}
}
