package tools.multipleAttributes;

public class WordFreqByPositionTool extends TopAttributesManager
{
	protected static volatile WordFreqByPositionTool instance = null;
			
	public static WordFreqByPositionTool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start WordFrepByPositionTool");
			instance = new WordFreqByPositionTool();
			instance.doOp();
		}
		return instance;
	}
		
	@Override
	protected void doCompute(int step)
	{
		if (currTokens.length < 5)
		{
			return;
		}
		for (int i = 0, j = currTokens.length; i < 3; i++)
		{
			if (currTokens[i].equals(""))
			{
				continue;
			}
			if (i < 2)
			{
				processToken(currTokens[i].toLowerCase() 
						+ "_" + i, step);
			}
			int position = 5 - (5 - i - 1);
			processToken(currTokens[j - i - 1].toLowerCase()
					 + "_" + position, step);
		}
	}

	private void processToken(String token, int step)
	{
		//dataSet.add(token);
		//Utils.incStringIntegerMap(getIntegerMap(), token);
		doStep(step, token);
	}
}
