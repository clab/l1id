package common;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import tools.AbstractTool;
import utils.Time;

import config.ConfigManager;

public class ToolsManager 
{
	
	private static ToolsManager instance = null;
	
	private Map<String, AbstractTool> map = new HashMap<String, AbstractTool>();
	
	/*public static int allMarkedFeatures = 22;
	
	public static int currFeature = 0;
	
	public static int[] features = ConfigManager.getInstance().getFeatures();*/
	
	
	private ToolsManager()
	{
		
	}
	
	public static ToolsManager getInstance()
	{
		if (instance == null)
			instance = new ToolsManager();
		return instance;
	}
	
	public static void main(String[] args) throws Exception 
	{
		System.out.println("Start registration at " + new Date(System.currentTimeMillis()));
		long start = System.currentTimeMillis();
		ToolsManager tm = ToolsManager.getInstance();
		
		tm.registerTools();
		
		//tm.writeToolDataInstances();
		
		long end = System.currentTimeMillis();
		System.out.println("\nDone at " + new Date(end) + "\nDuration: " + new Time(end - start) );
	}
	

	/*public void writeToolDataInstances() throws Exception 
	{
		//writeToolDataInstances(preprocessMap);
		if (ConfigManager.getInstance().isWriteData())
		{
			System.out.println("\nStart writing data at " + new Date(System.currentTimeMillis()));
			writeToolDataInstances(toolsMap);
		}
	}
	*/
	/*private void writeToolDataInstances(Map<String, AbstractTool> map) throws Exception 
	{
		for (AbstractTool tool : map.values())
		{
			tool.writeLibSVMDataFile();
		}
	}*/
	
	public void registerTools()
	{
		for (Attributes attr : Attributes.values())
		{
			if (ConfigManager.getInstance().isToolIn(attr.getFeatureNum() )
					&&
					isGetTool(attr.getFeatureNum()))
			{
				AbstractTool tool = null;
				String toolFullName = attr.getCannonicalName();
				try {
					/*System.out.println("Loading " + toolFullName);
					Class.forName(toolFullName.trim());
					//System.out.println("111");
					Class.forName(toolFullName.trim()).
					getMethod("getInstance", (Class<?>[])null);
					//System.out.println("222");
					Class.forName(toolFullName.trim()).
					getMethod("getInstance", (Class<?>[])null).invoke(null, new Object[] {});*/
					//System.out.println("333");
					tool = (AbstractTool)(Class.forName(toolFullName.trim()).
							getMethod("getInstance", (Class<?>[])null).invoke(null, new Object[] {}));
					map.put(toolFullName, tool);
				} catch (Exception e1) {
					//System.out.println("Error while trying to load " + toolFullName);
					System.err.println("Error while trying to load " + toolFullName);
					e1.printStackTrace();
				} 
			}
		}
	}
	
	private boolean isGetTool(int featureNum)
	{
		boolean arffExists = arffExists(featureNum);
		if (arffExists)
		{
			System.out.println("arff file for feature #" + (featureNum + 1) + 
					" already exists, exit this feature.");
		}
		return featureNum >= Attributes.features() || !arffExists;
	}

	private boolean arffExists(int featureNum)
	{
		String dataDir = ConfigManager.getInstance().getTrainPath(); 
		String featureStr = String.valueOf(featureNum + 1);
		String dataPath = dataDir + File.separator + File.separator + "arff" + File.separator + "file_" + 
				featureStr + ".arff"; 
		File file = new File(dataPath);
		return file.exists();
	}

	public AbstractTool getTool(String toolFullName)
	{
		if (!map.containsKey(toolFullName))
		{
			try {
				AbstractTool tool = (AbstractTool)(Class.forName(toolFullName).
						getMethod("getInstance", (Class<?>[])null).invoke(null, new Object[] {}));
				map.put(toolFullName, tool);
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
		}
		return map.get(toolFullName);
	}
}


