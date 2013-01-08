package tools.multipleAttributes.tags;

import java.util.Map;
import java.util.SortedMap;

import tools.TokensTool;

import config.ConfigManager;


public class PunctsRatioTool extends AttributesTagManager
{
	protected static volatile PunctsRatioTool instance = null;
	
	protected PunctsRatioTool() throws Exception 
	{
		initSet();
		setOrder(4);
	}
	
	public static PunctsRatioTool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start PunctsRatioTool");
			instance = new PunctsRatioTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void buildObject() throws Exception 
	{
		Map<String, SortedMap<String, Integer>> punctsFilesMap = 
				PunctsTool.getInstance().getFilesData();
		
		Map<String, Integer> tokensMap = TokensTool.getInstance().getIntegerMap();
		
		for (Map.Entry<String, SortedMap<String, Integer>> entry : 
			punctsFilesMap.entrySet())
		{
			String filename = entry.getKey();
			int tokens = tokensMap.get(filename);
			SortedMap<String, Integer> puncts = entry.getValue();
			for (Map.Entry<String, Integer> pEntry : puncts.entrySet()) 
			{
				int val = normalize((double)pEntry.getValue() / tokens);
				getIntegerMap().put(pEntry.getKey(), val);
			}
			currFileName = entry.getKey();
			doFinalStuff();
		}
	}
	
	@Override
	protected String getFilePath()
	{
		return ConfigManager.getInstance().getPunctutationPath();
	}
}
