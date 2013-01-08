package tools;

import java.io.File;
import java.util.Map;

import preprocess.Rank;
import tools.multipleAttributes.UnigramsTool;
import tools.multipleAttributes.tags.PunctsTool;

/**
 * Same as Rank only without the compensation for words not in the list
 */
public class Rank2Tool extends MapVarianceTool 
{
	protected static volatile Rank2Tool instance = null;
	
	protected Rank2Tool()
	{
	}
	
	public static Rank2Tool getInstance()
	{
		if (instance == null)
		{
			System.out.println("start Rank2Tool");
			instance = new Rank2Tool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void doForFile(File file) throws Exception 
	{
		initForFile();
		int numOfTokens = WordsTool.getInstance().getIntegerMap().get(currFileName);
		 
		Map<String, Integer> fileUnigrams = 
			UnigramsTool.getInstance().getFilesData().get(currFileName);
		
		Map<String, Integer> rankMap = Rank.getInstance().getIntegerMap();
		
		int sum = 0; 
		int notIn = 0;
		for (String unigram : fileUnigrams.keySet()) 
		{
			Integer val = rankMap.get(unigram);
			if (val != null)
			{
				sum += val;
				dataForVariance.add((double)val);
				//numOfTokens+=fileUnigrams.get(unigram);
			}
			else
			{
				notIn++;
			}
		}
		/*System.out.println("numOfTokens = " + numOfTokens);
		System.out.println("notIn = " + notIn);
		System.out.println("sum = " + sum);*/
		double averageRank = (double)sum / numOfTokens;
		
		int avg = (int)Math.round(averageRank);
		getIntegerMap().put(currFileName, avg);
		doFinalStuff();
	}
	
}
