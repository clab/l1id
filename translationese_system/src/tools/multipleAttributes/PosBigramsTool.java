package tools.multipleAttributes;


/**This class is a singleton class that creates and gives access to
 * a set of bigrams gathered from the corpora
 * */
public class PosBigramsTool extends BigramsTool
{
	protected static volatile PosBigramsTool instance = null;
	
	protected PosBigramsTool()
	{
		super();
	}
	
	public static PosBigramsTool getInstance()
	{
		if (instance == null)
		{
			System.out.println("start PosBigramsTool");
			instance = new PosBigramsTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected String getSrcDirUrl()
	{
		return getTaggedDirUrl();
	}
}

