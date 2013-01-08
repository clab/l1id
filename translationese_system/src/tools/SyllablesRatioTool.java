package tools;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import utils.Utils;

public class SyllablesRatioTool extends MapVarianceTool
{
	protected static volatile SyllablesRatioTool instance = null;
	
	private final String VOWELS_REGEXP = "[aAiIuUeEoOyY]";
	private double syllablesNum = 0;
	
	private SyllablesRatioTool()
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
	
	public static SyllablesRatioTool getInstance()
	{
		if (instance == null) {
			System.out.println("start SyllablesRatioTool");
			instance = new SyllablesRatioTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void compute() throws Exception
	{
		//long i = 0;
		for (String token : currTokens) 
		{
			char[] word = token.toCharArray();
			int syllables = 0;
			boolean lastIsVowel  = false;
			for (char letter : word) 
			{
				if (isVowel(letter))
				{
					if (!lastIsVowel)
					{
						syllables++;
						lastIsVowel = true;
					}
				}
				else
				{
					lastIsVowel = false;
				}
			}
			int wordLength = word.length;
			if (	wordLength > 2 &&
					word[wordLength-1] == 'e' && 
					!isVowel(word[wordLength - 2]) &&
					isVowel(word[wordLength - 3]) )
			{
				syllables--;
			}
			//System.out.println("token = " + token + ", syllables = " + syllables);
			syllablesNum += syllables;
			/*if (i++ < 20)
			{
				System.out.println("syllables = " + syllables);
				System.out.println("wordLength = " + wordLength);
				System.out.println("((double)syllables) / wordLength = " + ((double)syllables) / wordLength);
			}*/
			dataForVariance.add(((double)syllables) / wordLength);
		}
	}
	
	private boolean isVowel(char letter)
	{
		char[] vowels = VOWELS_REGEXP.toCharArray();
		for (char vowel : vowels) 
		{
			if (vowel == letter)
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void doFinalStuff() throws IOException
	{
		int numOfTokens = TokensTool.getInstance().
				getIntegerMap().get(currFileName);
		double syllablesRatio = 0;
		if (syllablesNum != 0)
		{
			syllablesRatio = (double) syllablesNum / numOfTokens;
		}
		getDoubleMap().put(currFileName, Utils.getDouble(syllablesRatio));
		super.doFinalStuff();
	}
		
	/**
	 * Create Maps for file and reset data members
	 */
	@Override
	protected void initForFile() 
	{
		super.initForFile();
		syllablesNum = 0;
	}
}
