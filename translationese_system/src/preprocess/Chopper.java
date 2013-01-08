package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import tools.AbstractTool;



import config.ConfigManager;

/**
 * Input: a folder with files
 * Output: another folder with files based on the input with roughly "wordNum" words a file - taken from a configuration file
 * */
public class Chopper extends AbstractTool 
{
	protected static volatile Chopper instance = null;
	int i = 0, nextCount = 0, currCount = 0;

	private int wordNum = ConfigManager.getInstance().getWordNumber();
	private  File newFile = null;
	private String prevLang = null;
	private String currLang = null;
	
	private Chopper()
	{
		init();
	}
	
	public static Chopper getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start Chopper");
			instance = new Chopper();
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
			File[] files = srcDir.listFiles(new FilenameFilter() {
		           public boolean accept(File file, String name) {
		                return name.endsWith(extension);
		        	  //return file.isFile();
		                }
		           });
			int count = 0;
			for (File file : files) 
			{
				if (count++ % 50 == 0) 
					System.out.println("done " + (count -1) + " files");
				BufferedReader br = new BufferedReader(new FileReader(file));
				String fileName = file.getName().replace(extension, "");
				fileName = fileName.replace(" ", "_");
				//fileName = fileName.replace("-", "");
				prevLang = currLang;
				currLang = fileName.substring(0,4);
				if (switchLang())
				{
					reset(fileName);
				}
				if (newFile == null) 
				{
					newFile = new File(fileName + "_" + i + extension);
					fileWriter = new BufferedWriter(new FileWriter(destDirUrl + File.separator + newFile));
				}
				
				for (String line = br.readLine(); line != null; line = br.readLine()) 
				{
					int sentenceLength = wordsNumber(line);
					if (line.trim().isEmpty() || line.trim().equals("\n"))
					{
						continue;
					}
					nextCount += sentenceLength;
					if (doWrite())
					{
						fileWriter.write(line);
						fileWriter.newLine();
						currCount += sentenceLength;
						if (nextCount >= wordNum)
						{
							reset(fileName);
						}
					}
					else
					{
						reset(fileName);
						continue;
					}
				}
				br.close();
			}
			
			if (null != fileWriter)
				fileWriter.close();
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean switchLang() 
	{
		if (prevLang == null)
		{
			return false;
		}
		return !currLang.equals(prevLang);
			
	}

	/**
	 * Used for doChop - when switching files.
	 * @param fileName
	 * @throws IOException
	 */
	private void reset(String fileName) throws IOException 
	{
		i++;
		nextCount = 0;
		currCount = 0;
		fileWriter.close();
		newFile = new File(destDirUrl + File.separator + fileName + "_" + i + extension);
		fileWriter = new BufferedWriter(new FileWriter(newFile));
	}

	/**
	 * Decide whether to write the current line to the file or not according to the line's length and file's total tokens. 
	 * @return
	 */
	private boolean doWrite() 
	{
		if (nextCount <= wordNum)
			return true;
		int extra = nextCount - wordNum;
		int missing = wordNum - currCount;
		return extra < missing;
	}
	
	private int wordsNumber(String line)
	{
		return line.split(" ").length;
	}
	
	/**
	 * Outputs the last line in the file and the total number of tokens in the file - 
	 * Only if the file's number of tokens is 100 words farther than the suggested number of words. 
	 */
	public void testChopper()
	{
		try {
			String path = ConfigManager.getInstance().getDestFolder();
			File dir = new File(path);

			if (!dir.isDirectory()) {
				System.out.println("Directory " + path + " doesn't exist");
				throw new Exception("Directory " + path + " doesn't exist");
			}
			File[] files = dir.listFiles();
			
			for (File file : files) 
			{
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = br.readLine();
				int counter = 0;
				String lastLine = null;
				while (line != null) 
				{
					counter += wordsNumber(line);
					lastLine = line;
					line = br.readLine();
				}
				if (counter < wordNum - 100 || counter > wordNum + 100)
				{
					System.out.println("lastLine: " + lastLine);
					System.out.println(file.getName() + ", "+ counter);
				}
				br.close();
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
