package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import common.Attributes;
import common.Classification;
import common.ToolsManager;

import tools.multipleAttributes.AttributesManager;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ArffSaver;
import config.ConfigManager;



public class WekaTool extends AbstractTool
{

	private static volatile WekaTool instance = null;

	static final String DUMMY_CLASS = "dummy";
	static final String ORIGIN_CLASS = "ORIGIN";
	
	private FastVector atts = null;
	private static Instances data = null;
	private Attribute classAttribute;
	
	
	public static boolean[] features = ConfigManager.getInstance().getFeatures();
	public static boolean[] tools = ConfigManager.getInstance().getAllTools();

	
	
	private Map<String, Attribute> attrMap = new HashMap<String, Attribute>();
	
	public static int currFeature = 0;
	public static boolean dataStageOn = false;
	
	public WekaTool()
	{
		super();
		System.out.println("WekaTool c'tor");
	}
	
	public static WekaTool getInstance() throws Exception
	{
		System.out.println("WekaTool.getInstance");
		if (instance == null)
		{
			System.out.println("start WekaTool");
			instance = new WekaTool();
			instance.doOp();
		}
		return instance;
	}

	/**
	 * When running features 5 or 6 with cross classification,
	 * execute them one at a time or else there will be a lot of garbage after the
	 * first feature. This will be caused by the reloading of the GramsManager for the training corpus.
	 */
	@Override
	protected void doOp() 
	{
		try 
		{
			for (int i = 0 ; i < features.length ; ++i)
			{
				if (features[i])
				{
					currFeature = i;
					int size;
					
					 System.out.println(new Date() + "  start with arff file. File number " + (currFeature + 1));
					 String path = ConfigManager.getInstance().getArffFolder() + File.separator + "file_" + (currFeature  + 1) + ".arff";
					 String res = checkFile(path);
					 if (res == null || res.equalsIgnoreCase("yes") || res.equalsIgnoreCase("y"))
					 {
						 size = getAttributesNumber();
						 //System.out.println("dataStageOn = " + dataStageOn);
						 //System.out.println("attr number = " + size);
						 atts = new FastVector(size);
						 generateAttributes();  //Once for all files
						 data = new Instances("MyRelation", atts, 0);
						 data.setClassIndex(atts.size() - 1);
						 dataStageOn = true;
						 generateDataForCorpus();  //Go over corpus and generate data per file
						 writeArffFile();
					 }
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return The number of attributes ( or feature vector's size ) we have
	 * @throws Exception
	 */
	private int getAttributesNumber() throws Exception
	{
		int count = 0;
		for (Attributes attr : Attributes.values())
		{
			if (ConfigManager.getInstance().isIn(attr) )
			{
				AbstractTool tool = null;
				String toolFullName = attr.getCannonicalName();
				try {
					tool = ToolsManager.getInstance().getTool(toolFullName);
					//tool = (AbstractTool)(Class.forName(toolFullName).getMethod("getInstance", (Class<?>[])null).invoke(null, new Object[] {}));
					count += tool.getAttrCount();
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
			}
		}
		return count;
	}
	
	/**
	 * Do op - part 1
	 * Generate the attributes according to training data in the arff instances
	 */
	private void generateAttributes() 
	{
		System.out.println(new Date(System.currentTimeMillis()) + "  Getting attributes for arff file. File number " + (currFeature + 1));
		try {
			generateCommonAttributes();
			generateComplexAttributes();
			generateClassAttribute();
		} catch (Exception e) {
		}
	}

	/**
	 * The classification attribute is a special one, generate it here
	 */
	private void generateClassAttribute() {
		FastVector fvClassVal = new FastVector(3);
		fvClassVal.addElement(DUMMY_CLASS);
		for (Classification cls : Classification.getValues())
		{
			fvClassVal.addElement(cls.getClassName());
		}
		classAttribute = new Attribute(ORIGIN_CLASS,
				fvClassVal);
		atts.addElement(classAttribute);
		attrMap.put(ORIGIN_CLASS, classAttribute);
	}

	/**
	 * Complex attributes are the multiple ones - the for each feature there are several attributes.
	 * Add them all here.
	 * @throws Exception
	 */
	
	private void generateComplexAttributes() throws Exception 
	{
		//System.out.println("dataStageOn = " + dataStageOn);
		for (Attributes attr : Attributes.values())
		{
			if (ConfigManager.getInstance().isIn(attr) )
			{
				AbstractTool tool = null;
				String toolFullName = attr.getCannonicalName();
				try {
					tool = ToolsManager.getInstance().getTool(toolFullName);
					if (tool.isComplex())
					{
						Set<String> set = ((AttributesManager)tool).getSet();
						//System.out.println("attributes set size = " + set.size());
						generateComplexAttributes(set, attr);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
			}
		}
	}

	/**
	 * Complex attributes are the multiple ones - for each feature there are several attributes.
	 * Add them all here based on the given set containing the data for the feature
	 * @throws Exception
	 */
	private void generateComplexAttributes(Set<String> set, Attributes attr) 
	{
		for (String attFeature : set)
		{
			String attrName = attr.getPrefix() + attFeature;
			Attribute att = new Attribute(attrName);
			atts.addElement(att);
			attrMap.put(attrName, att);
		}
	}

	/**
	 * common attributes are simple one (double) value attributes, only one for each attribute type.
	 * Add them here to the arff training data instance.
	 */
	private void generateCommonAttributes()
	{
		for (Attributes attr : Attributes.values())
		{
			if (ConfigManager.getInstance().isIn(attr) )
			{
				AbstractTool tool = null;
				String toolFullName = attr.getCannonicalName();
				try {
					tool = ToolsManager.getInstance().getTool(toolFullName);
					if (!tool.isComplex())
					{
						addAttribute(attr);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
			}
		}
	}

	/**
	 * adds an attribute to the arff instance
	 * @param attr the attribute to be added
	 * Add it only if it's enables in the configuration file.
	 * @param force can force the adding of the attribute if this argument it true 
	 */
	private void addAttribute(Attributes attr) {
		String feature = attr.getPrefix();
		if ( ConfigManager.getInstance().isIn(feature, attr.getFeatureNum())) {
			Attribute att = new Attribute(feature);
			atts.addElement(att);
			attrMap.put(feature, att);
		}
	}
	
	/**
	 * Do op - part 2
	 * inserts the data obtained on all files in corpus to the arff instance
	 * @throws Exception
	 */
	private void generateDataForCorpus() throws Exception
	{
		try{
		System.out.println(new Date(System.currentTimeMillis()) + "  Getting data for arff file. File number " + (currFeature + 1));
		getReadyForCrossClassification();
		//sentence length is arbitrary, only needed for file names. Change sometime to better code.
		//Set<String> fileNamesSet = AvgSentenceLengthTool.getInstance().getMap().keySet();
		init();
		String[] fileNames = srcDir.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".txt");
            }
		} );
		System.out.println("srcDir = " + srcDir.getAbsolutePath());
		System.out.println("fileNames size = " + fileNames.length);
		for (String fileName : fileNames) 
		{
			double[] vals = new double[atts.size()];
			//System.out.println("1111");
			for (Attributes attr : Attributes.values())
			{
				//System.out.println("222");
				if (ConfigManager.getInstance().isIn(attr) )
				{
					//System.out.println("333");
					AbstractTool tool = null;
					String toolFullName = attr.getCannonicalName();
					try {
						tool = ToolsManager.getInstance().getTool(toolFullName);
						//tool = (AbstractTool)(Class.forName(toolFullName).getMethod("getInstance", (Class<?>[])null).invoke(null, new Object[] {}));
						if (isCurrentFeature(attr))
						{
							//System.out.println("444");
							if (tool.isComplex())
							{
								//System.out.println("555");
								AttributesManager complexTool = (AttributesManager)tool;
								addComplexData(attr, fileName, complexTool.getFilesData(), vals);
							}
							else
							{
								addData(attr, fileName, tool.getMap(), vals);
							}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					} 
				}
			}
			data.add(new SparseInstance(1.0, vals));
			
			Classification cls = Classification.getType(fileName);
			data.lastInstance().setValue(atts.size() - 1, cls.getClassName());
		}
		} catch (Exception e)
		{
			System.out.println("Exception:");
			e.printStackTrace();
		}
	}
	
	private void getReadyForCrossClassification() throws Exception
	{
		//System.out.println("iscross = " + ConfigManager.getInstance().getIsCrossClassify());
		//if arff file for cross-corpus validation
		if (ConfigManager.getInstance().getIsCrossClassify()
				//&& (isCurrentFeature(Attributes.TOKEN_UNIGRAMS) ||
				//		isCurrentFeature(Attributes.TOKEN_BIGRAMS)))
				)
		{
			for (Attributes attr : Attributes.values())
			{
				if (ConfigManager.getInstance().isIn(attr) )
				{
					AbstractTool tool = null;
					String toolFullName = attr.getCannonicalName();
					try {
						tool = ToolsManager.getInstance().getTool(toolFullName);
						if (tool.isComplex())
						{
							System.out.println("resetting for tool " + toolFullName);
							(/*(AttributesManager)*/tool).reset();
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					} 
				}
			}
			//crossFlag = true;
			//////////////////////////////////////
			//This was the whole method before
			//////////////////////////////////////
			//GramsManager.clear();
			//NGramsPerChunk.clear();
			//_FileStatistics.clear();
			//////////////////////////////////////
			//Train path should be the corpus path with the *data* for the arff file.
			//The attributes should have already been gathered from the source path.
			//Dest path should be the matching training path output folder with the actual data
			//that has been gathered.
			
			//Where the arff is written in configured by the arffPath property.
			//There's no need to do anything special for the attributes gathering stage, 
			//it's done on the regular path properties.
			
			/*GramsManager.getInstance().srcDirUrl = ConfigManager.getInstance().getTrainPath();
			GramsManager.getInstance().destDirUrl = ConfigManager.getInstance().getTrainPath() +
			     File.separator + "output";
			GramsManager.getInstance().srcDir = Utils.getDir(GramsManager.getInstance().srcDirUrl);
			GramsManager.getInstance().destDir = Utils.getDir(GramsManager.getInstance().destDirUrl);
			
			NGramsPerChunk.getInstance().srcDirUrl = ConfigManager.getInstance().getTrainPath();
			NGramsPerChunk.getInstance().destDirUrl = ConfigManager.getInstance().getTrainPath()  +
		     File.separator + "output";;
			NGramsPerChunk.getInstance().srcDir = Utils.getDir(NGramsPerChunk.getInstance().srcDirUrl);
			NGramsPerChunk.getInstance().destDir = Utils.getDir(NGramsPerChunk.getInstance().destDirUrl);
			*/
		}
	}
	
	/**
	 * Add data from a map of values - to each (sub) attribute
	 * @param commonMap
	 * @param vals
	 */
	private void addComplexData(Attributes attr, String fileName,
			Map<String, SortedMap<String, Integer>> map, double[] vals)
	{
		System.out.println("files data map size = " + map.size());
		System.out.println("fileName = " + fileName);
		if (isFeatureIn(attr.getFileName(), attr.getFeatureNum()) && atts != null) 
		{
			String attrPrefix = attr.getPrefix();
			if (map == null)
			{
				System.out.println("map == null");
			}
			Map<String, Integer> fileMap = map.get(fileName);
			if (fileMap == null)
			{
				System.out.println("fileMap == null");
			}
			for (Map.Entry<String, Integer> entry : fileMap.entrySet()) 
			{
				String attrName = attrPrefix + entry.getKey().toString();
				assignValueByAttribute(attrName, entry.getValue(), vals);
			}
		}
	}
	
	private void addData(Attributes attr, String fileName,
			Map<String,? extends Number> map, double[] vals) 
	{
		try
		{
		System.out.println("files data map size = " + map.size());
		System.out.println("fileName = " + fileName);
		if (map == null){ System.out.println("map = null");}
		Number value = map.get(fileName);
		if (value == null) {System.out.println("value = " + value);}
		assignValueByAttribute(attr.getFileName(), value, vals);
		} catch (Exception e)
		{
			System.out.println("in exception");
			System.out.println(e.getMessage());
			System.out.println(e.getClass());
		}
	}
	
	/**
	 * Get the actual value for the specific attribute
	 * @param attrName
	 * @param value
	 * @param vals
	 */
	private void assignValueByAttribute(String attrName, Number value,
			double[] vals) {
		Attribute att = attrMap.get(attrName);
		if (att != null) {
			int i = att.index();
			if (i != -1) {
				if (value instanceof Integer)
				{
					vals[i] = ((double)(Integer)value);
				}
				else
				{
					vals[i] = (Double)value;
				}
			}
		}
	}
	
	/**
	 * Checks if the feature is enabled
	 * @param feature
	 * @return
	 */
	private boolean isFeatureIn(String feature, int featureNum) {
		return ConfigManager.getInstance().isIn(feature, featureNum);
	}
		
	/**
	 * Checks if the feature is enabled
	 * @param feature
	 * @return
	 */
	private boolean isCurrentFeature(Attributes attr) 
	{
		return isFeatureIn(attr.getFileName(), attr.getFeatureNum());
	}

	protected void writeArffFile() throws Exception 
	{
		 System.out.println(new Date() + ": writing to arff file. File number " + (currFeature + 1));
		 String path = ConfigManager.getInstance().getArffFolder() + File.separator + "file_" + (currFeature  + 1) + ".arff";
		 ArffSaver dataSaver = new ArffSaver(); 
		 dataSaver.setInstances(data);
		 dataSaver.setFile(new File(path)); 
		 dataSaver.writeBatch();
		 
		 removeDummyFromArffFile();
	     System.out.println(new Date() + ": Done writing to arff file. File number " + (currFeature + 1));
	}
	
	
	private String checkFile(String path) throws IOException 
	{
		File arffFile = new File(path);
		if (arffFile.exists())
		{
			System.err.println("arff file " + arffFile + " already exists.\nOverride (y / yes for yes)?");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			return in.readLine();
		}
		return null;
	}

	private void removeDummyFromArffFile() throws IOException 
	{
		String path = ConfigManager.getInstance().getArffFolder() + File.separator + "file_" + (currFeature + 1) + ".arff";
		String tmpPath = path.replace(".arff", "_tmp.arff");
		BufferedReader reader = new BufferedReader(new FileReader(path));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tmpPath));
		
		String line = reader.readLine();
		
		while (line != null)
		{
			line = line.replace("ORIGIN {dummy,", "ORIGIN {");
			writer.write(line);
			writer.newLine();
			line = reader.readLine();
		}
		reader.close();
		writer.close();
		
		File file = new File(path);
		File tmpFile = new File(tmpPath);
		
		try{
			if (file.isFile() && tmpFile.isFile())
			{
				file.delete();
				tmpFile.renameTo(file);
			}
		} catch (Exception e)
		{
			System.out.println("Error removing dummy");
			e.printStackTrace();
		}
	}
}

/**
 * 
 * Example for cross classification configuration:
 * sourcePath=C:/vered/NLP/tokenizedCorpora/Test   (combined)
trainPath=C:/vered/NLP/tokenizedCorpora/subTest2
testPath=C:/vered/NLP/tokenizedCorpora/subTest2
destPath=C:/vered/NLP/tokenizedCorpora/Test/output
#libSVMPath=C:/vered/NLP/tokenizedCorpora/Herald_2000_2000/libSVM
arffPath=C:/vered/NLP/tokenizedCorpora/subTest2_from_Test/arff
 * */
