package tools.multipleAttributes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import utils.Utils;

/**This class is a singleton class that creates and gives access to
 * sets data gathered from the entire corpora to be used as attributes and the actual data files per chunk
 * */
abstract public class TopAttributesManager extends AttributesManager
{
	private int top =  50;
	private boolean firstTime = true;
	
	@Override
	protected void buildObject() throws Exception
	{
		super.buildObject();
		firstTime = false;
		generateTopSet();
		map.clear();
		super.buildObject();
		
	}
	
	@Override
	protected void doForFile(File file) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(file));
		for (currLine = br.readLine(); currLine != null ; currLine = br.readLine())
		{
			currTokens = currLine.split(" ");
			if (firstTime )
			{
				computeTop();
			}
			else
			{
				compute();
			}
		}
		br.close();
		if (!firstTime )
		{
			doFinalStuff();
		}
	}
	
	protected void computeTop()
	{
		doCompute(1);
	}
	
	protected void compute()
	{
		doCompute(2);
	}
	
	protected void doStep(int step, String str)
	{
		switch (step) {
		case 1:
			incAttrCount(str);
			break;
		case 2:
			testAndSetAttr(str);
			break;

		default:
			break;
		}
	}
	
	abstract protected void doCompute(int step);

	protected void incAttrCount(String attr) 
	{
		Utils.incStringIntegerMap(getIntegerMap(), attr);
	}
	
	protected void testAndSetAttr(String attr) 
	{
		//Now that we have the top N frequent features
		//we can go over each file and count them.
		
		if (dataSet.contains(attr))
		{
			Utils.incStringIntegerMap(getIntegerMap(), attr);
		}
	}
	
	
	private void generateTopSet() 
	{
		SortedMap invMap = new TreeMap(Utils.inverseMap(map));
		for (int i = top; i > 0 ;) 
		{
			Object lastKey = invMap.lastKey();
			List<String> list = (List<String>)(invMap.get(lastKey));
			for (String str : list) 
			{
				i--;
				//System.out.println("i = " + i + ", lowerCaseToken = " + str);
				dataSet.add(str);
			}
			invMap.remove(lastKey);
			if (invMap.isEmpty())
			{
				break;
			}
		}
	}
}