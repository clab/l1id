package tools;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import tools.multipleAttributes.tags.PunctsTool;
import utils.Utils;

public class AvgWordLengthTool extends MapVarianceTool
{
	protected static volatile AvgWordLengthTool instance = null;
	
	private int wordLengthSum =0;
	
	private AvgWordLengthTool()
	{
	}
	
	public static AvgWordLengthTool getInstance()
	{
		if (instance == null) {
			System.out.println("start AvgWordLengthTool");
			instance = new AvgWordLengthTool();
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
	protected void compute() throws Exception
	{
		for (String token : currTokens)
		{
			if (PunctsTool.getInstance().isPunct(token)) //ignore punctuation marks
			{
				continue;
			}
			wordLengthSum += token.length();
			dataForVariance.add((double)token.length());
		}
	}
	
	@Override
	protected void doFinalStuff() throws IOException
	{
		int numOfWords = WordsTool.getInstance().
				getIntegerMap().get(currFileName);
		double avgWordLength = (double) wordLengthSum / numOfWords;
		getDoubleMap().put(currFileName, Utils.getDouble(avgWordLength));
		super.doFinalStuff();
	}
		
	/**
	 * Create Maps for file and reset data members
	 */
	@Override
	protected void initForFile() 
	{
		super.initForFile();
		wordLengthSum = 0;
	}
}
