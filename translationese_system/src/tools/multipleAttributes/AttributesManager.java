package tools.multipleAttributes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import config.ConfigManager;

import Exceptions.ToolsException;

import tools.MapTool;
import utils.Utils;

/**This class is a singleton class that creates and gives access to
 * sets data gathered from the entire corpora to be used as attributes and the actual data files per chunk
 * */
abstract public class AttributesManager extends MapTool
{
	protected SortedSet<String> dataSet = new TreeSet<String>();
	
	//filesData contains the fileName as key and a map containing the relevant data as map.
	//It doesn't have a direct representation in the files system
	protected Map<String, SortedMap<String, Integer>> filesData =
			new HashMap<String, SortedMap<String, Integer>>();
	
	protected int size = 0;
	
	public static String delim = ":";
			
	protected AttributesManager() 
	{
		super();
	}
	
	protected void initSet()
	{
		try
		{
			String filePath = getFilePath();
			File file = new File(filePath);
			BufferedReader br = new BufferedReader(new FileReader(file));
			for (String line = br.readLine() ; line != null ;
			line = br.readLine())
			{
				dataSet.add(line);
			}
			br.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void setSize() 
	{
		size = dataSet.size(); 
	}
	
	public int getSetSize()
	{
		return size;
	}

	public SortedSet<String> getSet()
	{
		return dataSet;
	}
	
	public Map<String, SortedMap<String, Integer>> getFilesData()
	{
		return filesData;
	}
	
	@Override
	public void writeObject() throws IOException 
	{
		writeSet(getFileNamePrefix() +"Attributes.txt");
		/*for (Map.Entry<String, SortedMap<String, Integer>> currMap : filesData.entrySet())
		{
			currFileName = currMap.getKey();
			map = currMap.getValue();
			writeMap(getMapPath());
			map.clear();
		}*/
	}
	
	protected String getFileNamePrefix()
	{
		return getClass().getSimpleName().
			replace("Manager", "").
			replace("Tool", "");
	}
	
	protected void writeSet(String filenameSuffix) throws IOException
	{
		String outputFileName = destDirUrl + File.separator + filenameSuffix;
		fileWriter = new BufferedWriter(new FileWriter(outputFileName));

		for (String str : dataSet) 
		{
			fileWriter.write(str);
			fileWriter.newLine();
		}
		fileWriter.close();
	}
	
	@Override
	public void readObject() throws Exception 
	{
		if (!isWrittenBefore() && !ConfigManager.getInstance().getIsCrossClassify()){
			throw new ToolsException("");
		}
		if (!filesData.isEmpty())
		{
			//already read it before
			return;
		}
		readSet(getFileNamePrefix() + "Attributes.txt");
		setSize();
		
		String[] fileNames = srcDir.list(new FilenameFilter() {
	           public boolean accept(File file, String name) {
	                return name.endsWith(extension);
	                }
	           });
		
		for (String fileName : fileNames) 
		{
			currFileName = fileName;
			readMap(getMapPath());
			loadMap();
			map.clear();
		}
	}
	
	private void loadMap()
	{
		filesData.put(currFileName, 
				new TreeMap<String, Integer>(
						(getIntegerMap())));
		
	}
	
	@Override
	protected String getMapPath()
	{
		String newFolder = getFolderPath();
		
		String currDir = destDirUrl + File.separator +
				newFolder;
		try
		{
			Utils.getDir(currDir);
			String newFile = currDir + currFileName;
			File file = new File(newFile);
			file.createNewFile();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newFolder + currFileName; 
	}
	
	protected String getFolderPath()
	{
		return getClass().getSimpleName().
			replace("Tool", "") + File.separator;
	}
	
	protected boolean isWrittenBefore()
	{
		String outputFolder = getClass().getSimpleName().
			replace("Tool", "") + File.separator;
		
		File outputDir = new File(destDirUrl + File.separator +
				outputFolder);
		return outputDir.exists();
	}
	
	protected void readSet(String filenameSuffix) throws IOException
	{
		String outputFileName = destDirUrl + File.separator + filenameSuffix;
		BufferedReader br = new BufferedReader(new FileReader(outputFileName));
		dataSet.clear();
		for (String line = br.readLine(); line != null ; line = br.readLine())
		{
			String attribute = line.trim();
			dataSet.add(attribute);
		}
		br.close();
	}
	
	@Override
	protected void doFinalStuff() throws IOException
	{
		writeMap(getMapPath());
		loadMap();
		map.clear();
	}
	
	protected String getFilePath()
	{
		return null;
	}
	
	@Override
	public int getAttrCount() 
	{
		//System.out.println("Attr count = " + dataSet.size());
		return dataSet.size();
	}
	
	@Override
	protected void clear()
	{
		super.clear();
		dataSet.clear();
		for (Map<String,Integer> map : filesData.values())
		{
			map.clear();
		}
		filesData.clear();
	}
	
	@Override
	protected void writeMap(String filenameSuffix) throws IOException
	{
		//writeNormalizedMap(filenameSuffix);
		normalize(currFileName, getIntegerMap());
		super.writeMap(filenameSuffix);
	}
}
