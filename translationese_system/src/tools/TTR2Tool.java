package tools;

import java.io.File;

import utils.Utils;


public class TTR2Tool extends MapTool 
{
	protected static volatile TTR2Tool instance = null;
	
	protected TTR2Tool() throws Exception 
	{
		setOrder(6);
	}
	
	public static TTR2Tool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start TTR2Tool");
			instance = new TTR2Tool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void doForFile(File file) throws Exception 
	{
		int numOfTokens = TokensTool.getInstance().getIntegerMap().get(currFileName);
		int numOfTypes = TypesTool.getInstance().getIntegerMap().get(currFileName);
		
		double v = Utils.log2(numOfTypes);
		double n = Utils.log2(numOfTokens);
		double ttr = v/n;
		
		getIntegerMap().put(currFileName, normalize(ttr));
	}
}
