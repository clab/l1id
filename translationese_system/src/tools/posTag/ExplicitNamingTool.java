package tools.posTag;

import tools.MapTool;


public class ExplicitNamingTool extends MapTool
{
	protected static volatile ExplicitNamingTool instance = null;
			
	private String NNP 	= "NNP";
	private String NNPS = "NNPS";
	private String PRP 	= "PRP";
	
	protected double ratio = 0.0;
	protected int nnp = 0;
	protected int prp = 0;
	
	
	protected ExplicitNamingTool()
	{
		setOrder(3);
		
	}
	
	public static ExplicitNamingTool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start ExplicitNamingTool");
			instance = new ExplicitNamingTool();
			instance.doOp();
		}
		return instance;
	}
	
	/*@Override
	protected void createMap()
	{
		map = new HashMap<String, Double>();
	}
	*/
	/*@Override
	protected void putInMap(String key, String value)
	{
		((Map<String, Double>)(map)).put(key, Double.valueOf(value));
	}*/
	
	@Override
	protected void initForFile()
	{
		ratio = 0.0;
		nnp = 0;
		prp = 0;
	}
		
	@Override
	protected void compute()
	{
		for (int i = 0; i < currTokens.length; i++)
		{
			String tag = currTokens[i];
			if (isNNP(tag))
			{
				nnp++;
			}
			if (isPRP(tag))
			{
				prp++;
			}
		}
	}
	
	@Override
	protected void doFinalStuff()
	{
		ratio = (double)prp / nnp;
		getIntegerMap().put(currFileName, normalize(ratio));
	}
	
	protected boolean isNNP(String tag)
	{
		return tag.equalsIgnoreCase(NNP) ||
				tag.equalsIgnoreCase(NNPS);
	}
	
	protected boolean isPRP(String tag)
	{
		return tag.equals(PRP);
	}
	
	@Override
	protected String getSrcDirUrl()
	{
		return getTaggedDirUrl();
	}
}
