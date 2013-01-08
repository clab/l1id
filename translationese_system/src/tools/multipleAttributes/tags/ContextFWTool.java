package tools.multipleAttributes.tags;

import java.util.HashSet;
import java.util.Set;

import utils.Utils;

import config.ConfigManager;

public class ContextFWTool extends AttributesTagManager 
{
	protected static volatile ContextFWTool instance = null;
	protected Set<String> set = new HashSet<String>();
	protected int i = 0;
	
	private ContextFWTool() throws Exception 
	{
		initSet();
		set = new HashSet<String>(dataSet);
		dataSet.clear();
	}
	
	public static ContextFWTool getInstance() throws Exception {
		if (instance == null) {
			System.out.println("start ContextFWTool");
			instance = new ContextFWTool();
			instance.doOp();
		}
		return instance;
	}
	
	/*@Override
	protected String getSrcDirUrl()
	{
		// TODO Auto-generated method stub
		return getTaggedDirUrl();
	}*/
	
	/*@Override
	protected void init()
	{
		super.init();
		initSetFromFile();
		set = new HashSet<String>(dataSet);
		dataSet.clear();
	}*/
	
	@Override
	protected void compute()
	{		
		if (currTokens.length == 0)
			return;
		String token1="";
		String token2="";
		for (i = 0; i < currTokens.length; ++i)
		{
			String token = currTokens[i].toLowerCase();
			String tag = currTags[i];
			if (token != null && !"".equals(token ))
			{
				token = getToken(token, tag);
				
				if (!token1.isEmpty() )
				{
					String trigram = token1 + " " + token2 + " "
							+ token;
					if (isContextualFWTrigram(trigram))
					{
						dataSet.add(trigram);
						Utils.incStringIntegerMap(getIntegerMap(),
									trigram);
					}
				}
			}
			token1 = token2;
			token2 = token;
		}	
	}
	
	private String getToken(String token, String tag)
	{
		if (i + 1 < currTokens.length)
		{
			String jointToken = token + currTokens[i+1];
			if (set.contains(jointToken))
			{
				++i;
				return jointToken;
			}
		}
		
		if (set.contains(token))
		{
			return token;
		}
		
		return tag;
	}
	
	private boolean isContextualFWTrigram(String trigram)
	{
		int count = 0;
		String[] tokens = trigram.split(" ");
		for (String token : tokens)
		{
			if (set.contains(token))
			{
				count++;
			}
		}
		return count >= 2;
	}

	/*@Override
	protected void doFinalStuff() throws IOException
	{
		fileWriter.close();
	}*/
	
	@Override
	protected String getFilePath()
	{
		return ConfigManager.getInstance().getFuncWordsPath();
	}
}
