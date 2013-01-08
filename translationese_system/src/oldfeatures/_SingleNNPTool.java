package oldfeatures;

import tools.multipleAttributes.tags.AttributesTagManager;
import utils.Utils;


public class _SingleNNPTool extends AttributesTagManager
{
	protected static volatile _SingleNNPTool instance = null;
			
	private String NNP 	= "NNP";
	private String NNPS = "NNPS";
	
	public static _SingleNNPTool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start _SingleNNPTool");
			instance = new _SingleNNPTool();
			instance.doOp();
		}
		return instance;
	}
		
	@Override
	protected void compute()
	{
		for (int i = 0; i < currTags.length; i++)
		{
			String tag = currTags[i];
		
			if (isNNP(tag))
			{
				if (testNNPSigularity(i))
				{
					String lowerCaseToken = currTokens[i];
					dataSet.add(lowerCaseToken);
					Utils.incStringIntegerMap(getIntegerMap(),
							lowerCaseToken);
				}
			}
		}
	}
	
	
	private boolean testNNPSigularity(int i)
	{
		if (i != 0)
		{
			if (isNNP(currTags[i - 1]))
			{
				return false;
			}
		}
		int nextIndex = i + 1;
		if (nextIndex < currTags.length)
		{
			if (isNNP(currTags[nextIndex]))
			{
				return false;
			}
		}
		return true;	
	}
	
	private boolean isNNP(String tag)
	{
		return tag.equalsIgnoreCase(NNP) ||
				tag.equalsIgnoreCase(NNPS);
	}
}
