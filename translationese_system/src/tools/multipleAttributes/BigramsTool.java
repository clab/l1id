package tools.multipleAttributes;

import java.io.IOException;

import common.CorpusGeneralData;

import utils.Utils;

/**This class is a singleton class that creates and gives access to
 * a set of bigrams gathered from the corpora
 * */
public class BigramsTool extends TopAttributesManager
{
	protected static volatile BigramsTool instance = null;
	protected static int n = 2;
	private int bigramsNumber = 0; //number of all bigrams
	
	protected BigramsTool() 
	{
		super();
	}
	
	public static BigramsTool getInstance()
	{
		if (instance == null)
		{
			System.out.println("start BigramsTool");
			instance = new BigramsTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void doCompute(int step)
	{
		String prevToken = "";
		currTokens = addSentenceEdges(currTokens);
		for (String token : currTokens)
		{
			String lowerCaseToken = token.toLowerCase();
			if (!prevToken.isEmpty() && !lowerCaseToken.isEmpty())
			{
				String bigram = prevToken + " " + lowerCaseToken;
				//dataSet.add(bigram);
				//Utils.incStringIntegerMap(getIntegerMap(), bigram);
				doStep(step, bigram);
				bigramsNumber++;
			}
			prevToken = lowerCaseToken;
		}
	}
	
	/**
	 * Add appropriate bigrams for beginning and ending of a sentence
	 * @param tokens The sentence separated into tokens
	 */
	protected String[] addSentenceEdges(String[] tokens)
	{
		int newLength = tokens.length + ( n - 1 ) * 2;
		String[] array = new String[newLength];
		for (int i = 0; i < n - 1 ; i++) 
		{
			array[i] = BOS;
			array[newLength - i - 1] = EOS;
		}
		for (int i = 0 ; i < tokens.length; i++)
		{
			array[i + n - 1] = tokens[i];
		}
		return array;
	}
	
	public int getBigramsNumber()
	{
		return bigramsNumber;
	}
	
	@Override
	public void writeObject() throws IOException
	{
		super.writeObject();
		CorpusGeneralData.getInstance().writeCorpusGeneralData();
	}
	
	@Override
	public void readObject() throws Exception
	{
		super.readObject();
		CorpusGeneralData.getInstance();
	}
}

