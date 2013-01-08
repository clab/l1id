package utils;

import java.util.Comparator;

public class IntegerComparator implements Comparator<String>
{
	
	public int compare(String arg0, String arg1)
	{
		Integer intArg0 = Integer.valueOf(arg0);
		Integer intArg1 = Integer.valueOf(arg1);
		return intArg0.compareTo(intArg1);
	}

}
