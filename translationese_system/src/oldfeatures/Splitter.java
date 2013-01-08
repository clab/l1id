package oldfeatures;

import java.util.LinkedList;
import java.util.List;

public class Splitter 
{

	String extra;
	String input;
	char[] delims;
	
	public Splitter(String input) 
	{
		this.input = input;
		extra = null;
	}

	public String[] split(String _delims)
	{
		delims = _delims.toCharArray();
		String[] currArray = input.split(System.getProperty("line.separator"));
		List<String> list = null;
		//Go over all delimiters and split by them, not deleting them
		for (char delim : delims)
		{
			 list = splitByDelim(currArray, String.valueOf(delim));
			 currArray = (String[])list.toArray(new String[0]);
		}
		return handleExtra(list, currArray);
		
	}

	/**
	 *  If the input didn't end with one of the delimiters, this last section is the extra
	 * @param list
	 * @param currArray
	 * @return
	 */
	private String[] handleExtra(List<String> list, String[] currArray) 
	{
		if (list.size() == 0)
		{
			return currArray;
		}
		String last = list.get(list.size() - 1);
		for (char delim : delims) 
		{
			if (last.endsWith(String.valueOf(delim)))
			{
				return currArray;
			}
		}
		list.remove(list.size() - 1);
		return (String[])list.toArray(new String[0]);
	}

	/**
	 * Get a delimiter and split by it. Return the string array with the original delimiter.
	 * @param sentences
	 * @param delim
	 * @return
	 */
	protected List<String> splitByDelim(String[] stringArray, String delim) 
	{
		List<String> currArray = new LinkedList<String>();
		
		for (String str : stringArray) 
		{
			System.out.println("str = " + str + "\n delim = " + delim);
			
			String[] splitStrArray = str.split(delim);
			for (String split : splitStrArray) 
			{
				currArray.add(split + delim );
			}
		}
				
		return currArray;
	}

	public String extra() 
	{
		return extra;
	}
	
	
}
