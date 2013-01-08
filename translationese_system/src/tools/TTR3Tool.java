package tools;

import java.io.File;
import java.util.Map;

import tools.multipleAttributes.UnigramsTool;
import weka.core.Utils;


public class TTR3Tool extends MapTool 
{
	protected static volatile TTR3Tool instance = null;
	
	protected TTR3Tool() throws Exception 
	{
	}
	
	public static TTR3Tool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start TTR3Tool");
			instance = new TTR3Tool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void doForFile(File file) throws Exception 
	{
		int numOfTokens = TokensTool.getInstance().getIntegerMap().get(currFileName);
		int numOfTypes = TypesTool.getInstance().getIntegerMap().get(currFileName);
		int v1 = 0;
		
		Map<String, Integer> unigrams = UnigramsTool.getInstance().getFilesData().get(currFileName);
		for (Integer count : unigrams.values())
		{
			if (count == 1)
			{
				v1++;
			}
		}
		double nominator = 100 * Utils.log2(numOfTokens);
		double denominator =  ((double)(1-v1))/numOfTypes;
		double ttr = nominator/denominator;
		
		getIntegerMap().put(currFileName, normalize(ttr));
	}
}
