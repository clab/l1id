package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import Exceptions.ToolsException;


import utils.Utils;

import common.Classification;
import common.ITool;

import config.ConfigManager;

public abstract class AbstractTool implements ITool 
{
	protected String srcDirUrl = null;
	protected String destDirUrl = null;
	protected String datalibSVMUrl = null;
	protected File srcDir = null;
	protected File destDir = null;
	protected File dataFileDir = null;
	protected BufferedWriter fileWriter = null;
	protected String extension = ConfigManager.getInstance().getExtension();
	
	public static final String lineSeparator = System.getProperty ( "line.separator" ); 
	
	public AbstractTool()
	{
		//System.out.println("AbstractTool c'tor");
		init();
		//System.out.println("AbstractTool end c'tor");
	}
	
	protected void init()
	{
		//There's need for cross and we *are* on the data stage.
		if (ConfigManager.getInstance().getIsCrossClassify() && WekaTool.dataStageOn)
		{
			//System.out.println("in init for data stage");
			srcDirUrl = ConfigManager.getInstance().getTrainPath();
			destDirUrl = ConfigManager.getInstance().getTrainPath() +
			     File.separator + "output";
			//System.out.println("srcDirUrl = " + srcDirUrl);
			//System.out.println("destDirUrl = " + destDirUrl);
		}
		else
		{
			//System.out.println("here, good");
			srcDirUrl = getSrcDirUrl();
			destDirUrl = getDestDirUrl();
		}
		datalibSVMUrl = getLibSVMDirUrl();
		try {
			srcDir = Utils.getDir(srcDirUrl);
			destDir = Utils.getDir(destDirUrl);
		} catch (Exception e)
		{
		}
	}
	
	abstract protected void doOp();
	
	protected String getLibSVMDirUrl() {
		return ConfigManager.getInstance().getLibSVMFolder();
	}
	
	protected String getarffUrl() {
		return ConfigManager.getInstance().getArffFolder();
	}

	protected String getSrcDirUrl()
	{
		return ConfigManager.getInstance().getSourceFolder();
	}
	
	protected String getDestDirUrl()
	{
		return ConfigManager.getInstance().getDestFolder();
	}
	
	
	public void readObject()
	throws Exception
	{
		// do nothing
	}
	
	public void writeObject()
	throws IOException
	{
		// do nothing
	}
	
	public void writeLibSVMDataFile() throws Exception 
	{
		String fileName = getClass().getSimpleName();
		File outFile = new File(datalibSVMUrl + File.separator + fileName + extension);
		outFile.createNewFile();
		fileWriter =  new BufferedWriter(new FileWriter(outFile));
		writeDataFile(getMap());
		fileWriter.close();
	}
	
	protected Map<String, ? extends Number> getMap()
	{
		return null;
	}
	
	protected void writeDataFile(Map<String, ? extends Number> map) throws Exception
	{
		if (map == null)
		{
			System.err.println("map is null in writeDataFile, " + this.getClass().getSimpleName());
			return;
		}
		for (Map.Entry<String, ?> entry : map.entrySet()) 
		{
			String label= null;
			try
			{
				label = getLabel(entry.getKey()) ;
			} catch (ToolsException e) 
			{
				System.out.println("File: " + entry.getKey());
				continue;
			}
			String instances = getValueAsInstance(entry.getValue());
			fileWriter.write(label + instances);
			fileWriter.newLine();
		}
	}
	
	protected String getLabel(String filename) throws ToolsException
	{
		String d = ConfigManager.getInstance().getFileClassificationDelim();
		Classification cls = Classification.getType(filename.split(d)[0]);
		return cls.getClassLabel();
	}

	protected String getValueAsInstance(Object value) throws Exception 
	{
		return " 1:" + value.toString();
	}
	
	protected void normalize(String filename, Map<?, Integer> map)
	{
		if (ConfigManager.getInstance().isDoNormalize())
		{
			//System.out.println("In normalize, filename = " + filename);
			int tokensNum = getTokensNum(filename);
			for (Map.Entry<?, Integer> entry : map.entrySet()) 
			{
				int val = entry.getValue();
				int normalizedVal = (int)Math.round(
						((double)val / tokensNum ) * ConfigManager.getInstance().getWordNumber());
				entry.setValue(normalizedVal);
			}
		}
	}
	
	protected int normalize(String filename, int val)
	{
		if (ConfigManager.getInstance().isDoNormalize())
		{
			int tokensNum = getTokensNum(filename);
			int normalizedVal = (int)Math.round(
						((double)val / tokensNum ) * ConfigManager.getInstance().getWordNumber());
			return normalizedVal;
		}
		return val;
	}

	private int getTokensNum(String filename)
	{
		return TokensTool.getInstance().getIntegerMap().get(filename);
	}

	public int getAttrCount() 
	{
		return 1;
	}
	
	public boolean isComplex() 
	{
		return getAttrCount() > 1;
	}
	
	public void reset() throws Exception
	{
		clear();
		init();
		readObject();
	}
	
	protected void clear()
	{
	}
}
