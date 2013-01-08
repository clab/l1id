package tools.multipleAttributes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import utils.Utils;

import config.ConfigManager;

public class ContractionsTool extends AttributesManager
{
	protected static volatile ContractionsTool instance = null;
	private Map<String, Integer[]> occurencesMap = 
		new HashMap<String, Integer[]>();
	private Map<String, String[]> contractionsMap = new HashMap<String, String[]>();
	
	protected ContractionsTool()
	{
		initSet();
	}

	public static ContractionsTool getInstance() 
    {
		if (instance == null) {
			System.out.println("start ContractionsTool");
			instance = new ContractionsTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void createMap()
	{
		map = new TreeMap<String, Double>();
	}
	
	@Override
	protected void putInMap(String key, String value)
	{
		getDoubleMap().put(key, Double.valueOf(value));
	}
	
	@Override
	protected void compute()
	{
		for (int i = 0; i < currTokens.length - 1; i++)
		{
			String nonSpaceBigram = currTokens[i] + currTokens[i+1];
			String spaceBigram = currTokens[i] + " " + currTokens[i+1];
			if (dataSet.contains(nonSpaceBigram))
			{
				int oldVal = occurencesMap.get(nonSpaceBigram)[0];
				occurencesMap.get(nonSpaceBigram)[0] = oldVal + 1;
			}
			for (Map.Entry<String, String[]> entry : contractionsMap.entrySet())
			{
				for (String full : entry.getValue())
				{
					//System.out.println("full = " + full + ", spaceBigram = " + spaceBigram);
					if (full.equals(spaceBigram))
					{
						String contracted = entry.getKey();
						int oldVal = occurencesMap.get(contracted)[1];
						occurencesMap.get(contracted)[1] = oldVal + 1;
						//System.out.println("oldVal = " + oldVal + ", full = " + full);
					}
				}
			}
		}
		
	}
	
	protected String getFilePath()
	{
		return ConfigManager.getInstance().getContractionsPath();
	}
	
	@Override
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
				String[] splitLine = line.split(":");
				String key = splitLine[0];
				dataSet.add(key);
				occurencesMap.put(key, new Integer[] {0,0});
				contractionsMap.put(key, splitLine[1].split(","));
			}
			br.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void initForFile()
	{
		for (Integer[] fullForms : occurencesMap.values())
		{
			fullForms[0] = fullForms[1] = 0;
		}
		getDoubleMap().clear();
	}
	
	@Override
	protected void doFinalStuff() throws IOException
	{
		for (Map.Entry<String, Integer[]> entry : occurencesMap.entrySet())
		{
			String key = entry.getKey();
			int nominator = entry.getValue()[0];
			int denominator = entry.getValue()[1];
			double value = nominator;
			if (denominator != 0)
			{
				value = (double)nominator / denominator;
			}
			getDoubleMap().put(key, Utils.getDouble(value));
		}
		super.doFinalStuff();
	}
	
	@Override
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
	
}
