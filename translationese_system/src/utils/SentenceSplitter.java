package utils;

import java.util.LinkedList;
import java.util.List;

import oldfeatures.Splitter;


public class SentenceSplitter extends Splitter 
{

	public SentenceSplitter(String line) 
	{
		super(line);
	}

	/**
	 * Get a delimiter and split by it. Return the string array with the original delimiter.
	 * Sepecialise for the dase a sentence contains an abbreviation - don't split by it. 
	 * @param sentences
	 * @param delim
	 * @return
	 */
	protected List<String> splitByDelim(String[] stringArray, String delim) 
	{
		//If the delimiter is not "." habdle as before
		if (!".".equals(delim))
		{
			return super.splitByDelim(stringArray, delim);
		}


		List<String> currArray = new LinkedList<String>();
		
		for (String str : stringArray) 
		{
			int beginIndex = 0;
			int lastIndex = stringArray[0].indexOf(".");
			do
			{
				int nextIndex = str.indexOf(".", lastIndex);
				if (nextIndex == -1) //No more "." occurrences
				{
					currArray.add(str);
					break;
				}
				if (nextIndex - lastIndex > 2 && (lastIndex > 0 && str.charAt(nextIndex - 2) != ' '))
				{
					currArray.add(str.substring(beginIndex, nextIndex) + delim );
					beginIndex = nextIndex + 1;
				}
				lastIndex = nextIndex + 1;
				
			} while (lastIndex < str.length());
		}
		
		return currArray;
	}
}
