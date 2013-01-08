package tools.posTag;

import tools.MapTool;

public class MultipleNNPTool extends MapTool
{
	protected static volatile MultipleNNPTool instance = null;
			
	private String NNP 	= "NNP";
	private String NNPS = "NNPS";
	
	private double average = 0.0;
	private int sum = 0;
	private int count = 0;
	
	private MultipleNNPTool()
	{
		setOrder(3);
	}
	
	public static MultipleNNPTool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start MultipleNNPTool");
			instance = new MultipleNNPTool();
			instance.doOp();
		}
		return instance;
	}
	
	/*@Override
	protected void createMap()
	{
		map = new HashMap<String, Double>();
	}*/
	
	/*@Override
	protected void putInMap(String key, String value)
	{
		getIntegerMap().put(key, normalize(Double.valueOf(value)));
	}*/
	
	@Override
	protected void initForFile()
	{
		average = 0.0;
		sum = 0;
		count = 0;
	}
		
	@Override
	protected void compute()
	{
		boolean inSequence = false;
		for (int i = 0; i < currTokens.length; i++)
		{
			String tag = currTokens[i];
			if (isNNP(tag))
			{
				sum++;
				if (!inSequence)
				{
					count++;
					inSequence = true;
				}
			}
			else
			{
				inSequence = false;
			}
		}
	}
	
	@Override
	protected void doFinalStuff()
	{
		average = (double)sum / count;
		getIntegerMap().put(currFileName, normalize(average));
	}
	
	private boolean isNNP(String tag)
	{
		return tag.equalsIgnoreCase(NNP) ||
				tag.equalsIgnoreCase(NNPS);
	}
	
	@Override
	protected String getSrcDirUrl()
	{
		return getTaggedDirUrl();
	}
}
