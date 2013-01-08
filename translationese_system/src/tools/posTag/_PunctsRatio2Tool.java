package tools.posTag;

import java.io.File;
import java.util.Map;
import java.util.SortedMap;

import tools.multipleAttributes.tags.AttributesTagManager;

public class _PunctsRatio2Tool extends AttributesTagManager
{
	protected static volatile _PunctsRatio2Tool instance = null;
	
	protected _PunctsRatio2Tool() throws Exception 
	{
		initSet();
		setOrder(4);
	}
	
	public static _PunctsRatio2Tool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start PunctsRatio2Tool");
			instance = new _PunctsRatio2Tool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void buildObject() throws Exception 
	{
		Map<String, SortedMap<String, Integer>> punctsFilesMap = 
				_PunctsTool.getInstance().getFilesData();
		
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
			doFinalStuff();
		}
	}
}
