package tools.posTag;

import java.io.File;
import java.util.Map;
import java.util.SortedMap;

import tools.multipleAttributes.tags.AttributesTagManager;

public class _PunctsRatioTool extends AttributesTagManager
{
	protected static volatile _PunctsRatioTool instance = null;
	
	protected _PunctsRatioTool() throws Exception 
	{
		initSet();
		setOrder(4);
	}
	
	public static _PunctsRatioTool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start PunctsRatioTool");
			instance = new _PunctsRatioTool();
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
			SortedMap<String, Integer> puncts = entry.getValue();
			for (Map.Entry<String, Integer> pEntry : puncts.entrySet()) 
			{
				int val = normalize((double)pEntry.getValue() / puncts.size());
				getIntegerMap().put(pEntry.getKey(), val);
			}
			doFinalStuff();
		}
	}
}
