package tools.multipleAttributes;

import java.io.IOException;

/**This class is a singleton class that creates and gives access to
 * a set of unigrams gathered from the corpora
 * */
public class PosUnigramsTool extends UnigramsTool
{
	protected static volatile PosUnigramsTool instance = null;
	
	protected PosUnigramsTool() 
	{
		super();
	}
	
	public static PosUnigramsTool getInstance()
	{
		if (instance == null)
		{
			System.out.println("start UnigramsTool");
			instance = new PosUnigramsTool();
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