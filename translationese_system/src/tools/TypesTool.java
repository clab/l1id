package tools;

import java.util.HashSet;
import java.util.Set;

public class TypesTool extends TokensTool
{
	protected static volatile TypesTool instance = null;
	protected Set<String> set = null;
	protected TypesTool()
	{
		set = new HashSet<String>();
	}
	
	public static TypesTool getInstance()
	{
		if (instance == null) {
			System.out.println("start TypesTool");
			instance = new TypesTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void compute() throws Exception
	{
		for (String token : currTokens)
		{
			set.add(token);
		}
	}
	
	@Override
	protected void doFinalStuff()
	{
		getIntegerMap().put(currFileName, set.size());
		set.clear();
	}
}
