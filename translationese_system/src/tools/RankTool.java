package tools;

import java.io.File;
import java.util.Map;

import preprocess.Rank;
import tools.multipleAttributes.UnigramsTool;
import tools.multipleAttributes.tags.PunctsTool;

public class RankTool extends MapTool 
{
	protected static volatile RankTool instance = null;
	
	protected RankTool()
	{
	}
	
	public static RankTool getInstance()
	{
		if (instance == null)
		{
			System.out.println("start RankTool");
			instance = new RankTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void doForFile(File file) throws Exception 
	{
		int numOfTokens = TokensTool.getInstance().getIntegerMap().get(currFileName);
		
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
				val = isPunct(unigram) ? 0 : 6000;
			}
			sum += val;
		}
		double averageRank = (double)sum / numOfTokens;
		
		int avg = (int)Math.round(averageRank);
		getIntegerMap().put(currFileName, avg);
	}
	
	private boolean isPunct(String token) throws Exception
	{
		return token.length() == 1 && 
			PunctsTool.getInstance().getSet().contains(String.valueOf(token.charAt(0)));
	}
}
