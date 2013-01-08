package oldfeatures;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import config.ConfigManager;

import tools.MapVarianceTool;
import utils.Utils;

public class _HistogramTool extends MapVarianceTool
{
	private SortedMap<Integer, Integer> binsMap = new TreeMap<Integer, Integer>();
	private SortedMap<Integer, Double> map = new TreeMap<Integer, Double>();
	static private _HistogramTool instance = getInstance();
	
	protected final static String delim = ":";
	
	private static _HistogramTool getInstance() 
	{
		if (instance == null) {
			System.out.println("start HistogramTool");
			instance = new _HistogramTool();
			instance.doOp();
		}
		return instance;
	}
	
	/*@Override
	protected void createMap() 
	{
		binsMap = new TreeMap<Integer, Integer>();
		map = new TreeMap<String, Double>();
	}*/
	
	@Override
	protected void compute() throws Exception
	{
		int length = currTokens.length;
		int key = length < 100 ? length : 100;
		Utils.incMap(binsMap, key);
	}
	
	@Override
	protected void initForFile() 
	{
		super.initForFile();
		binsMap.clear();
		map.clear();
	}
	
	@Override
	protected void doFinalStuff()
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
	}
	
	@Override
	public void writeObject() throws IOException 
	{
		writeMap(map, getMapPath());
		writeMap(binsMap, getBinsMapPath());
	}
	
	@Override
	public void readObject() throws IOException, ClassNotFoundException 
	{
		if (!map.isEmpty())
		{
			//already read it before
			return;
		}
		readMap(map, getMapPath(), false);
		readMap(binsMap, getBinsMapPath(), true);
	}
	
	@Override
	protected void putInMap(String key, String value, boolean isBins)
	{
		if (isBins)
		{
			binsMap.put(Integer.valueOf(key), Integer.valueOf(value));
		}
		else
		{
			map.put(Integer.valueOf(key), Double.valueOf(value));
		}
	}
	
	protected String getMapPath()
	{
		String str = currFileName.split(extension)[0];
		return getClass().getSimpleName().replace("Tool", "")
				+ "Avg" + "_"  + extension;
	}
	
	protected String getBinsMapPath()
	{
		return getClass().getSimpleName().replace("Tool", "")
				+ extension;
	}
	
	public static void main(String[] args) 
	{
		_HistogramTool hTool = new _HistogramTool();
		System.out.println("Done HistogramTool!");
	}
}
