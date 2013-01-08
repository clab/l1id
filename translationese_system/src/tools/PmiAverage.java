package tools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import preprocess.PMI;

import tools.multipleAttributes.BigramsTool;

public class PmiAverage extends MapTool
{
	protected static volatile PmiAverage instance = null;
	
	protected PmiAverage() throws Exception 
	{
	}
	
	public static PmiAverage getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("in PmiAverage() for the first time");
			instance = new PmiAverage();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void createMap()
	{
		map = new TreeMap<String, Double>();
	}
	
	@Override
	protected void putInMap(String key, String value)
	{
		((Map<String, Double>)(map)).put(key, Double.valueOf(value));
	}
	
	@Override
	protected void doForFile(File file) throws Exception 
	{
		Map<String, Integer> fileBigrams = BigramsTool.getInstance().
			getFilesData().get(currFileName);
		Map<String, Double> corpusPmiMap = PMI.getInstance().
			getDoubleMap();
		
		double sum = 0;
		for (String bigram : fileBigrams.keySet()) 
		{
			if (bigram.startsWith(BOS) || bigram.endsWith(EOS))
			{
				continue;
			}
			
			Double val = corpusPmiMap.get(bigram);
			sum += (val == null ? 0.0 : val);
		}
		//denominator is the number of different bigrams in the chunk
		double averagePmi = sum / fileBigrams.size(); 
		getDoubleMap().put(currFileName, averagePmi);
	}

}
