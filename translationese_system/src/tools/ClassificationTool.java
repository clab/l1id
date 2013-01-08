package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.Range;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils.DataSource;
import config.ConfigManager;

public class ClassificationTool extends AbstractTool
{
	private static volatile ClassificationTool instance = null;
	
	/**
	 * The features for this execution by their numbers as interpreted by Attributes.java
	 */
	public static boolean[] features = ConfigManager.getInstance().getFeatures();
	
	public ClassificationTool()
	{
		super();
	}
	
	public static ClassificationTool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start ClassificationTool");
			instance = new ClassificationTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void doOp() 
	{
		if (ConfigManager.getInstance().getIsCrossClassify())
		{
			System.out.println("23:10 cross");
			crossClassify();
		}
		else
		{
			classify();
		}
	}
	
	private void crossClassify()
	{
		String trainDir = ConfigManager.getInstance().getTrainPath(); 
		String testDir = ConfigManager.getInstance().getTestPath();
		System.out.println("train: " + trainDir);
		System.out.println("test: " + testDir);
		String outDir = getOutPath(trainDir, testDir);
		createOutDir(outDir);
		for (int i = 0; i < features.length; i++) 
		{
			/*int feature = features[i];
			if ( feature == 11)  //PMI Threshold
			{
				for (int j = 0; j <= 4; j+=2)
				{
					crossClassify(String.valueOf(feature) + "_" + j, trainDir, testDir, outDir);
				}
			}
			else*/
			if (features[i])
			{
				crossClassify(String.valueOf(i + 1), trainDir, testDir, outDir);
			}
		}
	}
		
	
	private void crossClassify(String feature, String trainDir, 
			String testDir, String outDir) 
	{
		String featureStr = String.valueOf(feature);
		String trainPath = trainDir + File.separator + "arff" + File.separator + "file_" 
				+ featureStr + ".arff"; 
		String testPath = testDir + File.separator + "arff" + File.separator + "file_"
				+ featureStr + ".arff"; ;
		String outputPath = outDir + File.separator + "smo_linear_" + featureStr;
		crossClassify(trainPath, testPath, outputPath);
	}

	private void crossClassify(String trainPath, String testPath, String outputPath) 
	{
		
		System.out.println("trainPath: " + trainPath);
		System.out.println("testPath: " + testPath);
		
		BufferedWriter writer = null;
		File output = new File(outputPath + ".txt");
		
		try {
			writer = createNewWriter(output);
			
			writer.write("start classifying at "
					+ (new Date(System.currentTimeMillis())).toString() + "\n");

			System.out.println("arffFile: " + output.getName());
			
			DataSource trainSource = new DataSource(trainPath);
			Instances trainData = trainSource.getDataSet();
			trainData.setClassIndex(trainData.numAttributes() - 1);
			
			DataSource testSource = new DataSource(testPath);
			Instances testData = testSource.getDataSet();
			testData.setClassIndex(testData.numAttributes() - 1);
			
			System.out.println("train size: " + trainData.numAttributes());
			System.out.println("test size: " + testData.numAttributes());
			SMO smo = new SMO();
			smo.setC(1.0);//which is default
			smo.setFilterType(new SelectedTag(SMO.FILTER_NONE, SMO.TAGS_FILTER));
				
			smo.buildClassifier(trainData);
			writer.write(smo.toString());
			System.out.println("start classifying " + new Date());
			writer.write("********************************************");
			writer.write("********************************************");
			writer.write("********************************************");
			Evaluation eval = new Evaluation(trainData);
			//System.out.println("train size: " + trainData.numAttributes());
			StringBuffer buff = new StringBuffer();
			Range range = new Range(Integer.toString(trainData.classIndex()));
			Boolean print = true;
			//System.out.println("test size: " + testData.numAttributes());
			eval.evaluateModel(smo, testData, buff, range, print);
							
			WriteClassficationResults(writer, eval);
			//writer.write("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			//writer.write("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			//writer.write("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			
		} catch (Exception e) {
			if (writer != null) {
				try {
					writer.write("in exception when classifying with arff file" + output.getName());
					writer.write(":\n" + e.getMessage());
					e.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	private void WriteClassficationResults(BufferedWriter writer,
			Evaluation eval) throws IOException, Exception 
	{
		int n = 0;
		
		String accuracy = String.valueOf(eval.pctCorrect());
		if (accuracy.length() > 5)
		{
			accuracy = accuracy.substring(0,5);
		}
		
		writer.write(eval.toSummaryString("\nResults\n======\n\n", true));
		writer.write("************ eval data : *****************\n");
		writer.write("accuracy = " + accuracy);
		writer.write("recall = " + String.valueOf(eval.recall(n)));
		writer.write("\nprecision = " + String.valueOf(eval.precision(n)));
		writer.write("\nfalseNegativeRate = " + eval.falseNegativeRate(n));
		writer.write("\nfalsePositiveRate = " + eval.falsePositiveRate(n));
		writer.write("\nerror rate = " + eval.errorRate());
		writer.write("\ncorrect = " + eval.correct());
		writer.write("\nnumFalseNegatives = " + eval.numFalseNegatives(n));
		writer.write("\nnumFalsePositives = " + eval.numFalsePositives(n));
		writer.write("\nnumTrueNegatives = " + eval.numTrueNegatives(n));
		writer.write("\nnumTruePositives = " + eval.numTruePositives(n));
		writer.write("\npercent correct = " + eval.pctCorrect());
		writer.write("\npercent incorrect = " + eval.pctIncorrect());
		writer.write("\npercent unclassified = " + eval.pctUnclassified());
		writer.write("\ntrueNegativeRate = " + eval.trueNegativeRate(n));
		writer.write("\ntruePositiveRate = " + eval.truePositiveRate(n));
		writer.write("\nunclassified = " + eval.unclassified());
		writer.write("\nF measure = " + eval.fMeasure(n));
		
		writer.write("\n\n" + eval.toMatrixString());
		writer.write("\n*******************************************\n");
		
		writer.close();
	}
	

	private void classify(String dataPath, String outputPath)
	{
		outputPath = outputPath + ".txt";
		BufferedWriter writer = null;
		File output = new File(outputPath);
		
		try {
			writer = createNewWriter(output);
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			writer.write("start classifying at "
					+ dateFormat.format(new Date()) + "\n");

			System.out.println("arffFile: " + dataPath);
			
			DataSource source = new DataSource(dataPath);
			Instances data = source.getDataSet();
			data.setClassIndex(data.numAttributes() - 1);
			
			SMO smo = new SMO();
			//J48 j48 = new J48();
			/*BayesNet bayes = new BayesNet();
			NaiveBayesUpdateable bayesUpdatable = new NaiveBayesUpdateable();
			NaiveBayesMultinomial bayesMulti = new NaiveBayesMultinomial();
			DMNBtext dmnb = new DMNBtext();
			IBk ibk = new IBk(7);
			Logistic logistic = new Logistic();
			LibSVM libSVM = new LibSVM();
			AODE aode = new AODE();
			LWL lwl = new LWL();*/
			
			//smo.setC(1.0);//which is default
			////no normalization of the attributes
			//smo.setFilterType(new SelectedTag(SMO.FILTER_NONE, SMO.TAGS_FILTER));
					
			Evaluation eval = new Evaluation(data);
//			StringBuffer buff = new StringBuffer();
//			Range range = new Range(Integer.toString(data.classIndex()));
//			Boolean print = true;
//			eval.crossValidateModel(smo, data, 10, new Random(123),
//					buff, range, print);
			System.out.println("writing attribute weights");
			smo.buildClassifier(data);
			writer.write(smo.toString());
			
			
			eval.crossValidateModel(smo, data, 10, new Random(1),
					new Object[0]);
			//System.out.println("2222");
			WriteClassficationResults(writer, eval);
			
			/*System.out.println("start classifying bayes at" + new Date());
			writer = createNewWriter(new File(outputPath.replace("smo_linear_", "bayes_")));
			eval.crossValidateModel(bayes, data, 10, new Random(1),
					new Object[0]);
			WriteClassficationResults(writer, eval);*/
			
			/*System.out.println("start classifying ibk at " + new Date());
			writer = createNewWriter(new File(outputPath.replace("smo_linear_", "ibk_")));
			eval.crossValidateModel(ibk, data, 10, new Random(1),
					new Object[0]);
			WriteClassficationResults(writer, eval);*/
			
			/*System.out.println("start classifying aode at " + new Date());
			writer = createNewWriter(new File(outputPath.replace("smo_linear_", "aode")));
			eval.crossValidateModel(aode, data, 10, new Random(1),
					new Object[0]);
			WriteClassficationResults(writer, eval);*/
			
			/*System.out.println("start classifying lwl at " + new Date());
			writer = createNewWriter(new File(outputPath.replace("smo_linear_", "lwl")));
			eval.crossValidateModel(lwl, data, 10, new Random(1),
					new Object[0]);
			WriteClassficationResults(writer, eval);*/
			
			/*System.out.println("start classifying bayesMulti at " + new Date());
			writer = createNewWriter(new File(outputPath.replace("smo_linear_", "bayesMulti")));
			eval.crossValidateModel(bayesMulti, data, 10, new Random(1),
					new Object[0]);
			WriteClassficationResults(writer, eval);
			
			System.out.println("start classifying bayesUpdatable at " + new Date());
			writer = createNewWriter(new File(outputPath.replace("smo_linear_", "bayesUpdatable")));
			eval.crossValidateModel(bayesUpdatable, data, 10, new Random(1),
					new Object[0]);
			WriteClassficationResults(writer, eval);
			
			System.out.println("start classifying dmnb at " + new Date());
			writer = createNewWriter(new File(outputPath.replace("smo_linear_", "dmnb")));
			eval.crossValidateModel(dmnb, data, 10, new Random(1),
					new Object[0]);
			WriteClassficationResults(writer, eval);
			
			*/
			
			/*System.out.println("start classifying libSVM at " + new Date());
			writer = createNewWriter(new File(outputPath.replace("smo_linear_", "libSVM_")));
			eval.crossValidateModel(libSVM, data, 10, new Random(1),
					new Object[0]);
			WriteClassficationResults(writer, eval);	
			*/
			/*System.out.println("start classifying logistic at" + new Date());
			writer = createNewWriter(new File(outputPath.replace("smo_linear_", "logistic_")));
			writer = createNewWriter(output);
			eval.crossValidateModel(logistic, data, 10, new Random(1),
					new Object[0]);
			WriteClassficationResults(writer, eval);*/
		} catch (Exception e) {
			if (writer != null) {
				try {
					writer.write("in exception when classifying with arff file: " + output.getName());
					writer.write(":\n" + e.getMessage());
					e.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private BufferedWriter createNewWriter(File output) throws IOException
	{
		return new BufferedWriter(new FileWriter(output));
	}
	
	private void classify()
	{
		String dataDir = ConfigManager.getInstance().getTrainPath(); 
		String outDir = dataDir + File.separator + "Classify";
		createOutDir(outDir);
		for (int i = 0; i < features.length; i++) 
		{
			if (features[i])
			{
				classify(String.valueOf(i + 1), dataDir, outDir);
			}
		}
	}
	
	private void classify(String feature, String dataDir, String outDir)
	{
		String featureStr = String.valueOf(feature);
		String dataPath = dataDir + File.separator + File.separator + "arff" + File.separator + "file_" + 
				featureStr + ".arff"; 
		String outputPath = outDir + File.separator + "smo_linear_" + featureStr;
		classify(dataPath, outputPath);
	}
	

	private String getOutPath(String trainDir, String testDir) 
	{
		String train = getFolder(trainDir);
		String test = getFolder(testDir);
		String outDir = "Train_" + train + "_Test_" + test ;
		outDir = trainDir.replace(train, "") + outDir;
		return outDir;
	}


	private String getFolder(String url) 
	{
		String[] trainSplit = url.split("/");
		int length = trainSplit.length;
		return trainSplit[length - 1];
	}
	
	private void createOutDir(String outDir)
	{
		File dir = new File(outDir);
		if (!dir.isDirectory())
		{
			dir.mkdir();
		}
	}
}
/**
 * Example for cross classification configuration, 
 * train and test should be arff folders, the others don't matter (hence the numbers):
 * 
 * sourcePath=C:/vered/NLP/tokenizedCorpora/111
trainPath=C:/vered/NLP/tokenizedCorpora/subTest_from_Test
testPath=C:/vered/NLP/tokenizedCorpora/subTest2_from_Test
destPath=C:/vered/NLP/tokenizedCorpora/222/output
#libSVMPath=C:/vered/NLP/tokenizedCorpora/Herald_2000_2000/libSVM
arffPath=C:/vered/NLP/tokenizedCorpora/333/arff
 */