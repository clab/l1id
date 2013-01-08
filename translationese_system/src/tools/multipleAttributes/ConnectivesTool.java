package tools.multipleAttributes;

import utils.Utils;
import config.ConfigManager;

public class ConnectivesTool extends PronounsTool
{
	protected static volatile ConnectivesTool instance = null;
	protected ConnectivesTool()
	{
	}

	public static ConnectivesTool getInstance() 
    {
		if (instance == null) {
			System.out.println("start ConnectivesTool");
			instance = new ConnectivesTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void compute()
	{
		currLine = currLine.toLowerCase();
		String[] connectives = dataSet.toArray(new String[dataSet.size()]);
		//This loop has to be in reverse order of the set in order to capture furthermore BEFORE further.
		//Hence the switch to an array and the backwards loop
        for (int i = connectives.length - 1; i >= 0; i--)
        {
        	String connective = connectives[i];
        	int conLength = connective.length();
			if (currLine.contains(connective))
			{
				String newLine = currLine.replaceAll(connective, "");
				if (newLine.length() != currLine.length())
				{
					//count is actually the number of times a contraction appeared in the line.
					int count = (currLine.length() - newLine.length())/ conLength;
					Utils.addToStringIntegerMap(getIntegerMap(), connective, count );
				}
				currLine = newLine;
			}
		}
	}
	
	protected String getFilePath()
	{
		return ConfigManager.getInstance().getConnectivesPath();
	}
}
