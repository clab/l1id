package tools.multipleAttributes.tags;

import java.util.Map;
import java.util.SortedMap;

import config.ConfigManager;


public class PunctsRatio2Tool extends AttributesTagManager
{
	protected static volatile PunctsRatio2Tool instance = null;
	
	protected PunctsRatio2Tool() throws Exception 
	{
		initSet();
		setOrder(4);
	}
	
	public static PunctsRatio2Tool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start PunctsRatio2Tool");
			instance = new PunctsRatio2Tool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void buildObject() throws Exception 
	{
		Map<String, SortedMap<String, Integer>> punctsFilesMap = 
				PunctsTool.getInstance().getFilesData();
		
		for (Map.Entry<String, SortedMap<String, Integer>> entry : 
			punctsFilesMap.entrySet())
		{
			int sumForFile = 0;
			SortedMap<String, Integer> puncts = entry.getValue();
			for (Map.Entry<String, Integer> pEntry : puncts.entrySet()) 
			{
				sumForFile += pEntry.getValue();
			}
			for (Map.Entry<String, Integer> pEntry : puncts.entrySet()) 
			{
				int val = normalize((double)pEntry.getValue() / sumForFile);
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
