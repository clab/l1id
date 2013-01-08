package tools;

import java.io.File;
import java.util.Map;

import preprocess.Rank;
import tools.multipleAttributes.UnigramsTool;
import tools.multipleAttributes.tags.PunctsTool;

public class Rank1Tool extends MapVarianceTool 
{
	protected static volatile Rank1Tool instance = null;
	
	protected Rank1Tool()
	{
	}
	
	public static Rank1Tool getInstance()
	{
		if (instance == null)
		{
			System.out.println("start Rank1Tool");
			instance = new Rank1Tool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void doForFile(File file) throws Exception 
	{
		initForFile();
		int numOfTokens = WordsTool.getInstance().getIntegerMap().get(currFileName);
		//int numOfTokens = 0; //numOfTokens ignores punctuation marks
		
		Map<String, Integer> fileUnigrams = 
			UnigramsTool.getInstance().getFilesData().get(currFileName);
		
		Map<String, Integer> rankMap = Rank.getInstance().getIntegerMap();
		
		int sum = 0; 
		int notIn = 0;
		
		for (String unigram : fileUnigrams.keySet()) 
		{
			Integer val = rankMap.get(unigram);
			if (val == null)
			{
				notIn++;
				val = PunctsTool.getInstance().isPunct(unigram) ? 0 : 6000;
			}
			/////// added:
			/*if (val > 0)
			{
				numOfTokens+=fileUnigrams.get(unigram);
			}*/
			////////
			dataForVariance.add((double)val);
			sum += val;
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
