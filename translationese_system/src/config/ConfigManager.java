package config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import common.Attributes;

import tools.WekaTool;

public class ConfigManager 
{
	private static volatile ConfigManager instance = null;
	private Properties props = null;
	
	private boolean[] tools = new boolean[Attributes.size()];
	private boolean[] features = new boolean[Attributes.features()];
	private boolean[] varianceTools = new boolean[Attributes.features()];

	private ConfigManager() {
		init();
		initTools();
		initVarianceTools();
	}

	private void initTools()
	{
		Arrays.fill(tools, false);
		String configFeatures = props.getProperty("features");
		if (null == configFeatures)
		{
			System.err.println("No Features in config file");
		}
		String[] configFeaturesArr = configFeatures.split(",");
		if (configFeatures.startsWith("all"))
		{
			Arrays.fill(tools,0,features.length, true);  //set all features
			//Now add the tools (other than features) to the array, to be set in the following loop.
			//Note that this is only the additional tools, and the features are already set in the previous line
			configFeaturesArr = Arrays.copyOfRange(configFeaturesArr, 1, configFeaturesArr.length);
		}
		
		for (String toolStr : configFeaturesArr)
		{
			int tool = Integer.valueOf(toolStr);
			if (tool > 0 && tool <= tools.length)
			{
				tools[tool - 1] = true;
			}
		}
		features = Arrays.copyOf(tools, features.length);
	}
	
	private void initVarianceTools()
	{
		Arrays.fill(varianceTools, false);
		String varianceFeatures = props.getProperty("varianceFeatures");
		if (null == varianceFeatures)
		{
			return;
		}
		String[] varianceArr = varianceFeatures.split(",");
		
		for (String toolStr : varianceArr)
		{
			int tool = Integer.valueOf(toolStr);
			if (tool > 0 && tool <= tools.length)
			{
				varianceTools[tool - 1] = true;
			}
		}
	}

	
	public static ConfigManager getInstance() {
		if (instance == null) {
			instance = new ConfigManager();
		}
		
		return instance;
	}

	private void init() {
		props = new Properties();
		FileInputStream in;
		try {
			String path = "." + File.separator + "src" + File.separator
					+ "config" + File.separator + "config.properties";
			System.out.println(path);
			in = new FileInputStream(path);
			props.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getWordNumber()
	{
		return Integer.parseInt(props.getProperty("words"));
	}
	
	public String getSourceFolder()
	{
		//System.out.println("source: " + props.getProperty("sourcePath"));
		return props.getProperty("sourcePath");
	}
	
	public String _getCorporaPath()
	{
		//return props.getProperty("corporaPath");
		return getSourceFolder();
	}
	
	public String getDestFolder() 
	{
		//System.out.println("dest: " + props.getProperty("destPath"));
		return getAndMakeDir("destPath");
	}
	
	public String getLibSVMFolder() {
		return getAndMakeDir("libSVMPath");
	}
	
	public String getArffFolder() {
		return getAndMakeDir("arffPath");
	}

	public String getSentencePerLineFolder()
	{
		return getAndMakeDir("sentencePerLinePath");
	}
	
	public String getFolder(String dir)
	{
		return getAndMakeDir(dir);
	}
	
	public String getPOSTagsPath() {
		return props.getProperty("posTagsPath");
	}

	public String getTagdictPath() {
		return props.getProperty("tagdict");
	}

	public String getDictFilePath() {
		return props.getProperty("dictFile");
	}
	
	private String getAndMakeDir(String name)
	{
		String dirPath = props.getProperty(name);
		dirPath = null == dirPath ?  dirPath= "." + File.separator + name : dirPath;
		File dir = new File(dirPath);
		if (!dir.exists())
		{
			dir.mkdir();
		}
		return dirPath;
	}
	
	public Integer getNumOfFilesToMove()
	{
		String prop = props.getProperty("numOfFilesToMove");
		if (prop == null)
		{
			return null;
		}
		return Integer.parseInt(prop);
	}
	
	public String getLangToMove()
	{
		return props.getProperty("langToMove");
	}
	
	public String getExtension()
	{
		return props.getProperty("extension");
	}
	
	public String getOutputDirectory() {
		String dir = props.getProperty("outputDir");
		if (dir != null)
		{
			File fsDir = new File("." + File.separator + dir);
			if (!fsDir.exists())
			{
				fsDir.mkdir();
			}
		}
		else 
		{
			dir= ".";
		}
		return dir;
	}
	
	public boolean[] getFeatures() 
	{
		return features;
	}
	
	public boolean[] getVarianceTools() 
	{
		return varianceTools;
	}
	
	public boolean[] getAllTools() 
	{
		return tools;
	}
	
	public boolean getPMIFromSourceFile() 
	{
		return !Boolean.parseBoolean(props.getProperty("generatePMIFile"));
	}
	
	public String getTrainPath() {
		return props.getProperty("trainPath");
	}
	
	public String getTestPath() {
		return props.getProperty("testPath");
	}
	
	public String getInOutPath() {
		return props.getProperty("inoutPath");
	}
	
	public String getFuncWordsPath() 
	{
		return props.getProperty("funcWordsPath");
	}
	
	public String getConnectivesPath() 
	{
		return props.getProperty("connectivesPath");
	}
	
	public String getContractionsPath()
	{
		return props.getProperty("contractionsPath");
	}
	
	public String getPronounsPath() 
	{
		return props.getProperty("pronounsPath");
	}
	
	public String getPunctutationPath() {
		return props.getProperty("punctuationMarks");
	}

	
	public String getProperty(String prop) 
	{
		return props.getProperty(prop);
	}
	
	public String getFreqWordsPath()
	{
		return props.getProperty("freqWordsPath");
	}
	
	public String[] getPreprocess()
	{
		return splitProp("preprocess");
	}

	public String[] getMadatoryPreprocess() {
		return splitProp("mandatoryPreprocess");
	}

	public String[] getConfigTools() {
		return splitProp("features");
	}
	
	private String[] splitProp(String propKey)
	{
		String propVal = props.getProperty(propKey);
		String[] res = null;
		if (propVal != null)
		{
			res = propVal.split(",");
		}
		return res;
	}
	
	public String getFileClassificationDelim() {
		String delim = props.getProperty("fileClassificationDelim");
		if (delim.equals(""))
		{
			delim = " ";
		}
		return delim;
	}

	public boolean isWriteData() 
	{
		return Boolean.parseBoolean(props.getProperty("writeData"));
	}

	public boolean getIsCrossClassify() 
	{
		return Boolean.parseBoolean(props.getProperty("crossClassify"));
	}
		
	public boolean isIn(String feature, int featureNum) 
	{
		//If we're doing only one feature at a time
		if (WekaTool.currFeature != Attributes.size() )
		{
			return (WekaTool.currFeature == featureNum );
		}
		//If we're doing a feature combination
		String prop = props.getProperty(feature);
		return prop == null ? false : prop.equalsIgnoreCase("YES");
		
	}
	
	public boolean isIn(Attributes attr) 
	{
		return isIn(attr.getFileName(), attr.getFeatureNum());
	}
	
	public boolean isToolIn(int toolNum)
	{
		/*if (toolNum > WekaTool.allMarkedFeatures)
		{
			toolNum = toolNum - 1000 +  WekaTool.allMarkedFeatures;
		}*/
		if (toolNum >= tools.length || toolNum < 0)
		{
			return false;
		}
		return tools[toolNum];
	}
	
	public boolean isDoVariance(int toolNum)
	{
		if (toolNum >= varianceTools.length || toolNum < 0)
		{
			return false;
		}
		return varianceTools[toolNum];
	}

	public String getTokenizerPath() 
	{
		return props.getProperty("tokenizerPath");
	}
	
	public int getN(Integer maxFiles)
	{
		String prop = props.getProperty("takeEveryNFile");
		System.out.println("prop n = " + prop);
		if (prop == null )
		{
			return 1;
		}
		int n = Integer.parseInt(prop);
		//if (maxFiles == null)
		//{
			return n;
		//}
		//return maxFiles> n ? 1 : n;
	}
	
	public Integer getMaxFiles()
	{
		String prop = props.getProperty("maxFiles");
		if (prop == null)
		{
			return null;
		}
		return Integer.parseInt(prop);
	}

	public int[] getNForLetterNGrams() 
	{
		String[] nStr = splitProp("nForLetterNGrams");
		if (null == nStr )
		{
			return new int[] {3};
		}
		int[] res = new int[nStr.length];
		for (int i = 0; i < nStr.length; i++) 
		{
			res[i] = Integer.valueOf(nStr[i]);
		}
		return res;
	}
	
	public boolean excludePunctsForLetterNGrams()
	{
		String prop = props.getProperty("excludePuncts");
		if (prop == null)
		{
			return false;
		}
		return Boolean.valueOf(prop);
	}
	
	public boolean retainAlphaLetterNGrams()
	{
		String prop = props.getProperty("alpha");
		if (prop == null)
		{
			return false;
		}
		return Boolean.valueOf(prop);
	}

	public boolean isDoNormalize() 
	{
		String prop = props.getProperty("normalize");
		if (prop == null)
		{
			return true;
		}
		return Boolean.parseBoolean(prop);
	}
}