package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import tools.AbstractTool;


import config.ConfigManager;

/**
 * Input: a folder with files
 * Output: another folder with a subset of the files - every n'th file is taken.
 * n is determined by the properties file
 * */
public class FileChooser extends AbstractTool 
{
	protected static volatile FileChooser instance = null;
	
	protected FileChooser()
	{
		init();
	}
	
	public static FileChooser getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start FileChooser"/*, n = " + ConfigManager.getInstance().getN()*/);
			instance = new FileChooser();
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
		                return name.endsWith(extension) ;
		                //&& name.startsWith(ConfigManager.getInstance().getLangToMove());
		        	  //return file.isFile();
		                }
		           });
			Integer maxFiles = ConfigManager.getInstance().getMaxFiles();
			System.out.println("maxFiles = " + maxFiles);
			int n = ConfigManager.getInstance().getN(maxFiles);
			System.out.println("n = " + n);
			int count = 0;
			int done = 0;
			//boolean flag = true; // for more than half the files use this
			for (File file : files) 
			{
				if (maxFiles != null && done >= maxFiles)
				{
					break;
				}
				if (file.length() == 0 || count++ % n != 0)
				{
					if (done % 50 == 0) 
					{
						System.out.println("done " + done + " files");
					}
					//flag = !flag;
					//if (flag)
					//{
						continue;
				//	}
				}
				done++;
				int linesCount = copyFile(file);
				removeFileIfGarbage(linesCount, file);
			}
			
			if (null != fileWriter)
				fileWriter.close();
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean removeLine(String line) 
	{
		if (line.startsWith("404 Not Found Haaretz.co.il"))
		{
			System.out.println("found line to be removed");
			return true;
		}
		if (line.equals("All rights reserved"))
		{
			System.out.println("found line to be removed");
			return true;
		}
		return false;
	}
	
	protected boolean skipLine(String line)
	{
		return line.trim().isEmpty() || line.trim().equals("\n") || removeLine(line);
	}
	
	protected int copyFile(File file) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(file));
		//String fileName = file.getName().replace(extension, "");
		File newFile = new File(destDirUrl + File.separator + file.getName());
		fileWriter = new BufferedWriter(new FileWriter(newFile));
		int linesCount = 0;
		for (String line = br.readLine(); line != null; line = br.readLine()) 
		{
			if (skipLine(line))
			{
				continue;
			}
			linesCount++;
			fileWriter.write(line);
			fileWriter.newLine();
		}
		br.close();
		fileWriter.close();
		return linesCount;
	}
	
	private void removeFileIfGarbage(int linesCount, File file) 
	{
		if (linesCount < 10)
		{
			System.out.println("deleting file " + file.getName());
			File newFile = new File(destDirUrl + File.separator + file.getName());
			boolean bool = newFile.delete();
			if (!bool)
			{
				System.out.println("did not delete file!");
			}
		}
	}
}
