package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import tools.AbstractTool;
import utils.Utils;

/**
 * Input: a folder with files
 * Output: another folder with a subset of the files - every n'th file is taken.
 * n is determined by the properties file
 * The corresponding data for the files taken is also taken (i.e, unigrams, average sentence length etc.)
 * */
public class ImprovedFileChooser extends FileChooser 
{
	private static final String OUTPUT = "output";
	protected static volatile ImprovedFileChooser instance = null;
	Map<String, Boolean> corpusFileNamesMap = new HashMap<String, Boolean>();
	
	private ImprovedFileChooser()
	{
		init();
	}
	
	public static ImprovedFileChooser getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start ImprovedFileChooser");
			instance = new ImprovedFileChooser();
			instance.doOp();
		}
		return instance;
	}
	
	/**
	 * chops the files in the source folder to files with the number of lines as in the configuration file, 
	 * where two languages files are not combined. The new files are in the destination folder.
	 */
	@Override
	protected void doOp()
	{
		try {
			super.doOp();  //this will copy files from src to dest
			goThroughOutputFiles();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void goThroughOutputFiles() throws IOException 
	{
		String[] corpusFileNames = destDir.list(new FilenameFilter() {
	           public boolean accept(File file, String name) {
	                return name.endsWith(extension);
	        	  //return file.isFile();
	                }
	           });
		corpusFileNamesMap = getMap(corpusFileNames);
		File outputDir = new File(srcDirUrl + File.separator + OUTPUT );
		File[] dataFiles = outputDir.listFiles();
		copySimpleFiles(dataFiles);
	}
	
	private void copySimpleFiles(File[] dataFiles) throws IOException 
	{
		
		for (File dataFile : dataFiles) 
		{
			if (dataFile.isDirectory())
			{
				copyDirContents(dataFile);
				continue;
			}
			if (isDoCopySubset(dataFile.getName()))
			{
				copySubFile(dataFile, OUTPUT, false);
				continue;
			}
			if (dataFile.getName().startsWith("punctuations"))
			{
				copyPunctuationSubFile(dataFile);
				continue;
			}
		}
	}

	private void copyDirContents(File directory) throws IOException 
	{
		File[] dataFiles = directory.listFiles();
		for (File dataFile : dataFiles) 
		{
			if (isDoCopyFile(dataFile.getName()))
			{
				copySubFile(dataFile, OUTPUT + File.separator + directory.getName(), true);
			}
		}
	}

	public boolean isDoCopySubset(String name)
	{
		return name.startsWith("avgSentenceLength") || 
				name.startsWith("avgWordLenth") || 
				name.startsWith("syllablesRatio") || 
				name.startsWith("tokens") || 
				name.startsWith("types") || 
				name.startsWith("Rank") || 
				name.startsWith("TTR") ||
				name.startsWith("Single_nnp") ||
				name.startsWith("Multiple_nnp") ||
				name.startsWith("LexicalDensity") ||
				name.startsWith("ExplicitNaming") ||
				name.startsWith("Repetitions") ||
				name.startsWith("PassiveVerbRatio.txt");
	}
	
	protected void copySubFile(File dataFile, String dirName, boolean alwaysCopy) throws IOException
	{
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(dataFile));
			String dirFullName = destDirUrl + File.separator + dirName;
			Utils.getDir(dirFullName);
			File newFile = Utils.getFile(dirFullName + File.separator + dataFile.getName());
			fileWriter = new BufferedWriter(new FileWriter(newFile, true));
			for (String line = br.readLine(); line != null; line = br.readLine()) 
			{
				if (isDoCopyFile(line.split(":")[0], alwaysCopy))
				//if (corpusFileNamesMap.containsKey(line.split(":")[0]))
				{
					fileWriter.write(line);
					fileWriter.newLine();
				}
			}
		} catch (Exception e)
		{
			System.out.println(e.getMessage());
		} finally
		{
			if	(br != null)
			{
				br.close();
			}
			if	(fileWriter != null)
			{
				fileWriter.close();
			}
		}
	}
	
	private boolean isDoCopyFile(String name, boolean alwaysCopy) 
	{
		return alwaysCopy ? true : corpusFileNamesMap.containsKey(name);
	}
	
	private boolean isDoCopyFile(String name) 
	{
		return corpusFileNamesMap.containsKey(name);
	}
	
	protected void copyPunctuationSubFile(File dataFile) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(dataFile));
		File newFile = new File(destDirUrl + File.separator + "output" + File.separator + dataFile.getName());
		fileWriter = new BufferedWriter(new FileWriter(newFile, true));
		for (String line = br.readLine(); line != null; line = br.readLine()) 
		{
			if (!line.equals(AbstractTool.lineSeparator))
			{
				if (isDoCopyFile(line.split(":")[0]))
				{
					fileWriter.write(line);
					fileWriter.newLine();
					
					//next bunch of lines - punctuations
					for (line = br.readLine(); !line.isEmpty() ; line = br.readLine())
					{
						fileWriter.write(line);
						fileWriter.newLine();
					}
					fileWriter.newLine();
				}
			}
		}
		br.close();
		fileWriter.close();
	}

	private Map<String, Boolean> getMap(String[] names)
	{
		Map<String, Boolean> namesMap = new HashMap<String, Boolean>();
		for (String name : names) 
		{
			namesMap.put(name, true);
		}
		return namesMap;
	}

	@Override
	protected boolean skipLine(String line)
	{
		return false;
	}
	
	public static void main(String[] args)
	{
		try
		{
			ImprovedFileChooser.getInstance();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
