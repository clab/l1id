package oldfeatures;

import tools.multipleAttributes.tags.AttributesTagManager;
import utils.Utils;

/**
 * @author Vered Volansky
 *
 */
public class _ContextFWTool extends AttributesTagManager
{
	protected static volatile _ContextFWTool instance = null;
	
	private _ContextFWTool() 
	{
		super();
	}
	
	@Override
	protected void init()
	{
		super.init();
		//dataSet = KoppelFunctionWordsTool.getInstance().getSet();
	}
	
	public static _ContextFWTool getInstance()
	{
		if (instance == null) {
			System.out.println("start ContextFWPerChunk");
			instance = new _ContextFWTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void compute()
			throws Exception 
	{
		if (currTags.length != currTokens.length)
		{
			System.out.println("tags.length != tokens.length");
		}
		
		for (int i=0 ; i < currTags.length ; ++i)
		{
			String tag = currTags[i];
			String token = currTokens[i];
			
			//System.out.println("tag = " + tags[i] + ", token = " + tokens[i]);
			
			//Add token_pos of the form (pos,token) (pos of previous token).
			if (i + 1 < currTokens.length)
			{
				String nextTok = currTokens[i+1];
				if (dataSet.contains(nextTok))
				{
					String posToken = tag + " " + nextTok;
					Utils.incStringIntegerMap(getIntegerMap(), posToken);
				}
			}
			
			//Add token_pos of the form (token,pos) (pos of next token). 
			if (i + 1 < currTags.length &&
					dataSet.contains(token))
			{
				String tokenPos = token + " " + currTags[i+1];
				Utils.incStringIntegerMap(getIntegerMap(), tokenPos);
			}
		}
	}
	
	/*public static void clear() 
	{
		instance = null;
	}*/
}
