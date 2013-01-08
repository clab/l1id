package tools;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import utils.Utils;

public class AvgSentenceLengthTool extends MapVarianceTool
{
	protected static volatile AvgSentenceLengthTool instance = null;
	
	private int numOfSentences = 0;
	
	
	private AvgSentenceLengthTool()
	{
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
	
	public static AvgSentenceLengthTool getInstance()
	{
		if (instance == null) {
			System.out.println("start AvgSentenceLengthTool");
			instance = new AvgSentenceLengthTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void compute() throws Exception
	{
		numOfSentences++;
		dataForVariance.add((double)currTokens.length);
	}
	
	@Override
	protected void doFinalStuff() throws IOException
	{
		int numOfTokens = TokensTool.getInstance().
				getIntegerMap().get(currFileName);
		double avgSentenceLength = (double) numOfTokens / numOfSentences;
		getDoubleMap().put(currFileName, Utils.getDouble(avgSentenceLength));
		super.doFinalStuff();
	}
		
	/**
	 * Create Maps for file and reset data members
	 */
	@Override
	protected void initForFile() 
	{
		super.initForFile();
		numOfSentences = 0;
	}
}
