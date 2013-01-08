package tools.multipleAttributes;

import java.io.IOException;
import java.util.TreeMap;

import tools.multipleAttributes.AttributesManager;
import utils.IntegerComparator;
import utils.Utils;

public class SentenceLengthHistogramTool extends AttributesManager
{
	static private SentenceLengthHistogramTool instance = getInstance();
	
	static final int range = 100;
	
	
	protected SentenceLengthHistogramTool()
	{
		initSet(range);
	}
	
	public static SentenceLengthHistogramTool getInstance() 
	{
		if (instance == null) {
			System.out.println("start HistogramTool");
			instance = new SentenceLengthHistogramTool();
			instance.doOp();
		}
		return instance;
	}
	
	protected void createMap()
	{
		map = new TreeMap<String, Integer>(new IntegerComparator());
	}
	
	protected void initSet(int beginRange, int endRange)
	{
		dataSet.clear();
		for (int i = beginRange; i <= endRange; i++)
		{
			dataSet.add(String.valueOf(i));
		}
	}
	
	protected void initSet(int range)
	{
		initSet(0,range);
	}
	
	@Override
	protected void compute() throws Exception
	{
		int length = currTokens.length;
		int key = length < range ? length : range;
		Utils.incStringIntegerMap(getIntegerMap(), 
				String.valueOf(key));
	}
	
	@Override
	public void writeObject() throws IOException 
	{
		//do nothing
	}
	
	@Override
	protected void readSet(String filenameSuffix) throws IOException
	{
		initSet(range);
	}
	
	/*protected void _doFinalStuff()
			throws IOException
	{
		int numberOfFiles = 1;
		try {
			numberOfFiles = getNumOfFiles();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Map.Entry<Integer, Integer> entry : binsMap.entrySet()) 
		{
			map.put(entry.getKey(), 
					(double)entry.getValue() / numberOfFiles);
		}
	}
	
	private int getNumOfFiles() throws Exception
	{
		String testPath = ConfigManager.getInstance().getTestPath();
		File dir = Utils.getDir(testPath);
		String[] files = null;
		if (currFileName.startsWith("O"))
		{
			files = dir.list(new FilenameFilter() {
		           public boolean accept(File file, String name) {
		                return name.endsWith(extension) 
		                		&& name.startsWith("Eng");
		                }
		           });
		}
		else
		{
			files = dir.list(new FilenameFilter() {
		           public boolean accept(File file, String name) {
		                return name.endsWith(extension) 
		                		&& !name.startsWith("Eng");
		                }
		           });
		}
		return files.length;
	}*/
}
