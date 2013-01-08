package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import config.ConfigManager;

abstract public class MapTool extends AbstractTool
{
	
	protected static String BOS = "<BOS>";
	protected static String EOS = "<EOS>";
	protected static String BOW = "<BOW>";
	protected static String EOW = "<EOW>";
	
	protected String 	 currLine		= null;
	protected String[]   currTokens 	= null;
	protected String 	 currFileName	= null;
	
	protected SortedMap<String, ? extends Number> map = null;
	
	protected int order = 1;
	
	protected final static String delim = ":";
	
	protected MapTool()
	{
		createMap();
	}
	
	protected void createMap()
	{
		map = new TreeMap<String, Integer>();
	}
	
	@Override
	protected void doOp()
	{
		try { //try to read object from file
			readObject();
		} catch (Exception e) {
			try {
				buildObject();
				writeObject();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		setSize();
	}
	
	/*public void reset() throws Exception
	{
		clear();
		init();
		readObject();
	}*/
		
	protected void buildObject() throws Exception {
		try {
			File[] files = srcDir.listFiles(new FilenameFilter() {
		           public boolean accept(File file, String name) {
		                return name.endsWith(extension);
		                }
		           });
			int count = 0;
			for (File file : files) 
			{
				if (count++ % 50 == 0) System.out.println("Done " + count + " files");
				currFileName = file.getName();
				//System.out.println(currFileName);
				doForFile(file);
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void doForFile(File file) throws Exception
	{
		initForFile();
		BufferedReader br = new BufferedReader(new FileReader(file));
		for (currLine = br.readLine(); currLine != null ; currLine = br.readLine())
		{
			currTokens = currLine.split(" ");
			compute();
		}
		br.close();
		doFinalStuff();
	}
	
	protected void initForFile() 
	{
		//do nothing
	}
	
	protected void setSize()
	{
		//do nothing
	}
	
	@Override
	public void writeObject() throws IOException 
	{
		writeMap(getMapPath());
	}
	
	@Override
	public void readObject() throws Exception
	{
		if (!map.isEmpty())
		{
			//already read it before
			return;
		}
		readMap(getMapPath());
	}

	@Override
	public Map<String, ? extends Number> getMap() {
		return map;
	}
	
	//abstract protected void initFile(String fileName);
	protected void compute() throws Exception
	{
		//do nothing
	}
	
	protected void doFinalStuff()
		throws IOException
	{
		//do nothing
	}
	
	protected String getMapPath()
	{
		return getClass().getSimpleName().replace("Tool", "")
			+ extension;
	}
	
	/*protected void writeMap(String filenameSuffix) throws IOException
	{
		String outputFileName = destDirUrl + File.separator + filenameSuffix;
		fileWriter = new BufferedWriter(new FileWriter(outputFileName));

		for (Map.Entry<String, ? extends Number> entry : map.entrySet()) 
		{
			fileWriter.write(entry.getKey() + delim + entry.getValue() );
			fileWriter.newLine();
		}
		fileWriter.close();
	}*/
	
	protected void writeMap(String filenameSuffix) throws IOException
	{
		String outputFileName = destDirUrl + File.separator + filenameSuffix;
		fileWriter = new BufferedWriter(new FileWriter(outputFileName));

		for (Map.Entry<String, ? extends Number> entry : map.entrySet()) 
		{
			fileWriter.write(entry.getKey() + delim + entry.getValue() );
			fileWriter.newLine();
		}
		fileWriter.close();
	}
	
	public void _writeNormalizedMap(String filenameSuffix) throws IOException
	{
		/*for (Map.Entry<String, ? extends Number> entry : map.entrySet()) 
		{
			normalize(entry.getKey(), (Map<?, Integer>)(entry.getValue()));
		}
		writeMap(filenameSuffix);*/
		
		//System.out.println("filenameSuffix = " + filenameSuffix);
		//System.out.println("delim = " + delim);
		//String[] filenameSplit = filenameSuffix.split("\\\\");
		//if (filenameSplit.length <= 1)
		//{
		//	filenameSplit = filenameSuffix.split(File.separator);
		//}
		//String filename = filenameSuffix;
		//if (filenameSplit.length > 1)
		//{
		//	filename = filenameSplit[filenameSplit.length - 1];
		//}
		//System.out.println("filenameSuffix.indexOf(delim) = " + filenameSuffix.indexOf(delim));
		
		//System.out.println("delim = " + delim);
		//String[] filenameSplit = filenameSuffix.split(delim);
		//String filename = filenameSuffix;
		//int end = filenameSplit.length - 1;
		//System.out.println("filenameSplit.length = " + filenameSplit.length);
		//if (end > 0)
		//{
		//	filename = filenameSplit[end];
		//}
		normalize(currFileName, getIntegerMap());
		writeMap(filenameSuffix);
	}
	
	protected void readMap(String filenameSuffix) throws IOException
	{
		String outputFileName = destDirUrl + File.separator +
			filenameSuffix;
		BufferedReader br = new BufferedReader(new FileReader(
				outputFileName));
		map.clear();
		for (String line = br.readLine(); line != null ; 
			line = br.readLine())
		{
			int index = line.lastIndexOf(delim);
			String key = line.substring(0, index);
			String value = line.substring(index + 1, line.length());
			putInMap(key, value);
		}
		br.close();
	}
	
	/*protected void putInMap(String key, String value, boolean isVariance)
	{
		putInMap(key, value);
	}*/
	
	protected void putInMap(String key, String value)
	{
		(getIntegerMap()).put(key, Integer.valueOf(value));
	}
	
	protected String getTaggedDirUrl()
	{
		String url = super.getSrcDirUrl();
		int i = url.lastIndexOf('\\');
		if (i == -1 )
		{
			i = url.lastIndexOf('/');
		}
		url = url.substring(0,i+1) + "Tagged_" + url.substring(i+1, url.length());
		return  url;
	}
	
	public Map<String, Integer> getIntegerMap()
	{
		return (Map<String, Integer>)map;
	}
	
	public Map<String, Double> getDoubleMap()
	{ 
		return (Map<String, Double>)map;
	}
	
	protected int normalize(double val)
	{
		int mul = (int) Math.pow(10, order);
		return (int)Math.round(val * mul);
	}
	
	@Override
	protected void clear()
	{
		map.clear();
	}
	
	protected void setOrder(int order)
	{
		if (ConfigManager.getInstance().isDoNormalize())
		{
			this.order = order;
		}
	}
}
