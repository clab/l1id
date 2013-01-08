package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import utils.Utils;

abstract public class MapVarianceTool extends MapTool
{
	protected Map<String, Double> varianceMap = new HashMap<String, Double>();
	protected List<Double> dataForVariance = new LinkedList<Double>();
	
	protected void initForFile() 
	{
		dataForVariance.clear();
	}
	
	protected void setSize()
	{
		//do nothing
	}
	
	@Override
	public void writeObject() throws IOException 
	{
		writeMap(map, getMapPath());
		writeMap(varianceMap, getVarianceMapPath());
	}
	
	@Override
	public void readObject() throws IOException, ClassNotFoundException 
	{
		if (!map.isEmpty())
		{
			//already read it before
			return;
		}
		readMap(getMapPath());
		readMap(varianceMap, getVarianceMapPath(), true);
	}

	public Map<String, Double> getVarianceMap() {
		return varianceMap;
	}
	
	protected void doFinalStuff()
		throws IOException
	{
		getVarianceMap().put(currFileName, Utils.getVariance(dataForVariance.toArray()));
	}
	
	protected String getVarianceMapPath()
	{
		return getClass().getSimpleName().replace("Tool", "")
				+ "Variance" + extension;
	}
	
	@Override
	protected void writeMap(String filenameSuffix) throws IOException
	{
		writeMap(map, filenameSuffix);
	}
	
	protected void writeMap(Map<?, ? extends Number> map, String filenameSuffix) throws IOException
	{
		String outputFileName = destDirUrl + File.separator + filenameSuffix;
		fileWriter = new BufferedWriter(new FileWriter(outputFileName));

		for (Map.Entry<?, ? extends Number> entry : map.entrySet()) 
		{
			fileWriter.write(entry.getKey() + delim + entry.getValue() );
			fileWriter.newLine();
		}
		fileWriter.close();
	}
	
	@Override
	protected void readMap(String filenameSuffix) throws IOException
	{
		readMap(map, filenameSuffix, false);
	}
	
	protected void readMap(Map<?, ? extends Number> map, String filenameSuffix, boolean isVariance) throws IOException
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
			putInMap(key, value, isVariance);
		}
		br.close();
	}
	
	protected void putInMap(String key, String value, boolean isVariance)
	{
		if (isVariance)
		{
			varianceMap.put(key, Double.valueOf(value));
		}
		else
		{
			putInMap(key, value);
		}
	}
	
	protected void clear()
	{
		map.clear();
		varianceMap.clear();
	}
}
