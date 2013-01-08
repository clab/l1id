package preprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import tools.MapTool;

import config.ConfigManager;

public class Rank extends MapTool
{	
	static volatile private Rank instance = null;
	
	private Rank()
	{}
	
	static public Rank getInstance()
	{
		if (instance == null)
		{
			instance = new Rank();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void buildObject() throws Exception
	{
		try {
			File file = new File(ConfigManager.getInstance().getFreqWordsPath());
			BufferedReader reader = new BufferedReader(new FileReader(file));
			for (String line = reader.readLine(); null != line ; line = reader.readLine())
			{
				String[] str = line.split("\t");
				getIntegerMap().put(str[1].trim(), Integer.parseInt(str[0].trim()));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
