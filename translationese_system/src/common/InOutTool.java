package common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import config.ConfigManager;

import tools.AbstractTool;
import tools.multipleAttributes.AttributesManager;
import utils.Time;
import utils.Utils;

public class InOutTool extends AbstractTool
{
	
	private static InOutTool instance = null;
	
	private InOutTool()
	{
		
	}
	
	public static InOutTool getInstance()
	{
		if (instance == null)
			instance = new InOutTool();
		return instance;
	}
	
	public static void main(String[] args) throws Exception 
	{
		System.out.println("Start InOutTool at " + new Date(System.currentTimeMillis()));
		long start = System.currentTimeMillis();
		InOutTool.getInstance().doOp();
		
		long end = System.currentTimeMillis();
		System.out.println("\nDone at " + new Date(end) + "\nDuration: " + new Time(end - start) );
	}
	
	@Override
	protected void doOp() 
	{
		for (Attributes attr : Attributes.values())
		{
			if (attr.isComplex())
			{
				String toolFullName = attr.getCannonicalName();
				try {
					AttributesManager tool = (AttributesManager)(Class.forName(toolFullName.trim()).
							getMethod("getInstance", (Class<?>[])null).invoke(null, new Object[] {}));
					processTool(attr, tool);
				} catch (Exception e1) {
					System.err.println("Error while trying to load " + toolFullName);
					e1.printStackTrace();
				} 
			}
		}
	}

	private void processTool(Attributes attr, AttributesManager tool)
	{
		SortedMap<String, Integer> oMap = tool.getFilesData().get("English.txt");
		SortedMap<String, Integer> tMap = tool.getFilesData().get("T.txt");
		
		
		SortedSet<String> inOonly = getMinusMap(oMap.keySet(), tMap.keySet());
		SortedSet<String> inTonly = getMinusMap(tMap.keySet(), oMap.keySet());
		
		print(inOonly, attr, "inO");
		print(inTonly, attr, "inT");
	}

	private SortedSet<String> getMinusMap(
			Set<String> set1, Set<String> set2)
	{
		SortedSet<String> minusSet = new TreeSet<String>();
		
		for (String token : set1) 
		{
			if (!set2.contains(token))
			{
				minusSet.add(token);
			}
		}
		
		return minusSet;
	}
	
	private void print(SortedSet<String> set, Attributes attr, String suffix)
	{
		try {
			String dir = ConfigManager.getInstance().getInOutPath();
			Utils.getDir(dir);
			String path = dir + File.separator + attr.getFileName() + "_" + suffix + ".txt";
			fileWriter = new BufferedWriter(new FileWriter(path));
			for (String token : set) 
			{
				fileWriter.write(token);
				fileWriter.newLine();
			}
			fileWriter.close();
		} catch (Exception e) {
		}
	}

	
	
}


