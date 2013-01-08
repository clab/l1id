package tools;

import java.util.HashSet;
import java.util.Set;

import tools.posTag.TagMapTool;


public class RepetitionsTool extends TagMapTool
{
	protected static volatile RepetitionsTool instance = null;
	private int repCount = 0;
			
	private String[] TagGroup = {"J", "N", "R", "V"};
	private String[] beHaveForm = {"is", "am", "are", "was", "were", "be", "been", "being", "have", "has", "had"};
	
	public RepetitionsTool()
	{
		setOrder(3); //2 is enough but 3 captures the first digit after the decimal point.
	}
	
	public static RepetitionsTool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start RepetitionsTool");
			instance = new RepetitionsTool();
			instance.doOp();
		}
		return instance;
	}
	
	
	@Override
	protected void compute()
	{
		//check for specific tag repetition
		//test the tag repetition for token repetition
		//if so - inc count
		Set<String>	set = new HashSet<String>();
		for (int i = 0; i < currTags.length; i++)
		{
			String tag = currTags[i];
			String token = currTokens[i];
			if (!set.contains(token) && prefixOK(tag))
			{
				boolean repeated = isRepeted(i + 1, tag, token);
				if (repeated)
				{
					repCount++;
					set.add(token);
				}
			}
		}
		//System.out.println(currTokens.toString() + ", count = " + repCount);
		set.clear();
	}
	
	private boolean isRepeted(int begin, String tag, String token)
	{
		for (int i = begin; i < currTokens.length; i++)
		{
			if (currTokens[i].equals(token) &&
				currTags[i].startsWith(tag.substring(0,1)) && 
				notBe(currTokens[i], currTags[i]))
			{
				return true;
			}
		}
		return false;
	}

	private boolean notBe(String token, String tag) 
	{
		if (tag.startsWith("V"))
		{
			return isBeHaveForm(token);
		}
		return true;
	}

	@Override
	protected void initForFile()
	{
		repCount = 0;
	}
	
	@Override
	protected void doFinalStuff()
	{
		int numOfTokens = TokensTool.getInstance().getIntegerMap().get(currFileName);
		double res = (double)repCount / numOfTokens;
		//System.out.println("res = " + res);
		getIntegerMap().put(currFileName, normalize(res));
	}
	
	private boolean prefixOK(String tag)
	{
		for (String tagPrefix : TagGroup) 
		{
			if (tag.startsWith(tagPrefix))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean isBeHaveForm(String token)
	{
		for (String specialToken : beHaveForm) 
		{
			if (token.equals(specialToken))
			{
				return true;
			}
		}
		return false;
	}
	
	
	/* *
	 * Previously:
	 * Go over the corpus and get the tokens that appear 
	 * twice in the same sentence.
	 * next go over each chunk and count appearances of the above tokens.
	 * if encounter the same word in one sentence raise a flag.
	 * last - for each word the flag wasn't raised, set the value to zero.
	 * For those it was raised for - divide by the number of tokens 
	 * for the chunk and normalize
	 */
}
