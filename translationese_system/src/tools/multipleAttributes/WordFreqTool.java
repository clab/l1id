package tools.multipleAttributes;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import tools.multipleAttributes.tags.PunctsTool;
import utils.Utils;

public class WordFreqTool extends AttributesManager
{
	protected static volatile WordFreqTool instance = null;
	private int[] thresholds =  {5,10,50};
	private Map<Integer, SortedSet<String>> thresholdsMap = null;
	private boolean firstTime = true;
	private Set<String> punctsSet = null;
	
	private WordFreqTool()
	{
		initSet();
		try {
			punctsSet = PunctsTool.getInstance().getSet();
		} catch (Exception e) {
			punctsSet = new HashSet<String>();
		}
	}
	
	public static WordFreqTool getInstance() 
    {
		if (instance == null) {
			System.out.println("start WordFreqTool");
			instance = new WordFreqTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void initSet()
	{
		try
		{
			thresholdsMap = new HashMap<Integer, SortedSet<String>>();
			for (int i = 0; i < thresholds.length; i++) 
			{
				dataSet.add(String.valueOf(thresholds[i]));
				thresholdsMap.put(thresholds[i], new TreeSet<String>());
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	

	@Override
	protected void buildObject() throws Exception
	{
		super.buildObject();
		firstTime = false;
		getTopNFrequentWords();
		map.clear();
		super.buildObject();
		
	}
	
	/**
	 * generates the thresholds map: key is a threshold, value is a list of the top <key> words in the corpus
	 */
	private void getTopNFrequentWords() 
	{
		Integer[] valuesArray = map.values().toArray(new Integer[] {} );
		Arrays.sort(valuesArray);
		
		
		/*for (int i = 0; i < thresholds.length; i++) 
		{
			int threshold = thresholds[i];
			for (int j = 0; j < threshold; j++) 
			{
				int index = valuesArray.length - 1 - j;
				int ocurrences = valuesArray[index];
				for (Map.Entry<String, Integer> entry : getIntegerMap().entrySet())
				{
					if (entry.getValue() == ocurrences && !isFull(threshold))
					{
						thresholdsMap.get(threshold).add(entry.getKey());
					}
				}
			}
		}*/
		
		int topThreshold = thresholds[thresholds.length - 1];
		for (int j = 0; j < topThreshold; j++) 
		{
			int index = valuesArray.length - 1 - j;
			int ocurrences = valuesArray[index];
			for (Map.Entry<String, Integer> entry : getIntegerMap().entrySet())
			{
				if (entry.getValue() == ocurrences )
				{
					for (int i = 0; i < thresholds.length; i++) 
					{
						int threshold = thresholds[i];
						if (!isFull(threshold))
						{
							thresholdsMap.get(threshold).add(entry.getKey());
						}
					}
				}
			}
		}
	}
	
	private boolean isFull(int max)
	{
		return thresholdsMap.get(max).size() == max;
	}
	
	@Override
	protected void doForFile(File file) throws Exception
	{
		
		_doForFile(file);
		
	}
	
	private void _doForFile(File file) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(file));
		for (currLine = br.readLine(); currLine != null ; currLine = br.readLine())
		{
			currTokens = currLine.split(" ");
			if (firstTime )
			{
				compute_1();
			}
			else
			{
				compute_2();
			}
		}
		br.close();
		if (!firstTime )
		{
			doFinalStuff();
		}
	}

	private void compute_1() 
	{
		for (String token : currTokens)
		{
			if (token != null && !"".equals(token) && !punctsSet.contains(token))
			{
				String lowerCaseToken = token.toLowerCase();
				Utils.incStringIntegerMap(getIntegerMap(),
								lowerCaseToken);
			}
		}
	}
	
	private void compute_2() 
	{
		//Now that we have the top N frequent words
		//we can go over each file and count them.
		for (String token : currTokens)
		{
			if (token != null && !"".equals(token))
			{
				for (int i = 0; i < thresholds.length; i++)
				{
					int threshold = thresholds[i];
					Set<String> words = thresholdsMap.get(threshold);
					if (words.contains(token.toLowerCase()))
					{
						Utils.incStringIntegerMap(getIntegerMap(),
								String.valueOf(threshold));
					}
				}
			}
		}
	}
}
