package tools.multipleAttributes;

/**This class is a singleton class that creates and gives access to
 * a set of bigrams gathered from the corpora
 * */
public class PosTrigramsTool extends PosBigramsTool
{
	protected static volatile PosTrigramsTool instance = null;
	protected static int n = 3;
	
	protected PosTrigramsTool()
	{
		super();
	}
	
	public static PosTrigramsTool getInstance()
	{
		if (instance == null)
		{
			System.out.println("start PosTrigramsTool");
			instance = new PosTrigramsTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected String getSrcDirUrl()
	{
		return getTaggedDirUrl();
	}
	
	@Override
	protected void doCompute(int step)
	{
		if (currTokens.length == 0)
			return;
		String token1="";
		String token2="";
		currTokens = addSentenceEdges(addSentenceEdges(currTokens));
		for (String token : currTokens)
		{
			if (!token1.isEmpty() )
			{
				String trigram = token1 + " " + token2 + " " + token;
				doStep(step, trigram);
				//dataSet.add(trigram);
				//Utils.incStringIntegerMap(getIntegerMap(), trigram);
			}
			token1 = token2;
			token2 = token;
		}
	}
}

