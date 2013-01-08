package tools;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import preprocess.PMI;

import tools.multipleAttributes.BigramsTool;


public class PmiThreshold extends MapTool 
{
	protected static volatile PmiThreshold instance = null;
	
	protected static int threshold = 0;
	
	protected PmiThreshold() throws Exception 
	{
	}
	
	public static PmiThreshold getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("in PMIThreshold() for the first time");
			instance = new PmiThreshold();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void doForFile(File file) throws Exception 
	{
		Map<String, Integer> fileBigrams = BigramsTool.getInstance().
				getFilesData().get(currFileName);
		Map<String, Double> corpusPmiMap = PMI.getInstance().
				getCorpusPMImap();
		int count = 0;
		
		for (String bigram : fileBigrams.keySet()) 
		{
			if (bigram.startsWith(BOS) || bigram.endsWith(EOS))
			{
				continue;
			}
			Double val = corpusPmiMap.get(bigram);
			if (null != val && val > threshold)
			{
				count++;
			}
		}
		getIntegerMap().put(currFileName, normalize(currFileName, count));
	}
}
