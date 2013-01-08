package tools.posTag;

import tools.MapTool;
import tools.TokensTool;


public class LexicalDensityTool extends MapTool
{
	protected static volatile LexicalDensityTool instance = null;
			
	private String J 	= "J";
	private String N 	= "N";
	private String R 	= "R";
	private String V 	= "V";
	
	private int sum = 0;
	
	public LexicalDensityTool()
	{
		setOrder(4);
	}
	
	public static LexicalDensityTool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start LexicalDensityTool");
			instance = new LexicalDensityTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void initForFile()
	{
		sum = 0;
	}
		
	@Override
	protected void compute()
	{
		for (int i = 0; i < currTokens.length; i++)
		{
			String tag = currTokens[i];
			if (prefixOK(tag))
			{
				sum++;
			}
		}
	}
	
	@Override
	protected void doFinalStuff()
	{
		int numOfTokens = TokensTool.getInstance().getIntegerMap().get(currFileName);
		double res = (double)sum / numOfTokens;
		getIntegerMap().put(currFileName, normalize(res));
	}
	
	private boolean prefixOK(String tag)
	{
		return !(tag.startsWith(J) ||
				tag.startsWith(N) || 
				tag.startsWith(R) ||
				tag.startsWith(V) );
	}
	
	@Override
	protected String getSrcDirUrl()
	{
		return getTaggedDirUrl();
	}
}
