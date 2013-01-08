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
 * Output: another folder with clean files - after garbage removal
 * */
public class CleanCorpus extends AbstractTool 
{
	protected static volatile CleanCorpus instance = null;
	
	private CleanCorpus()
	{
		init();
	}
	
	public static CleanCorpus getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start CleanCorpus");
			instance = new CleanCorpus();
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
				//String fileName = file.getName().replace(extension, "");
				File newFile = new File(destDirUrl + File.separator + file.getName());
				fileWriter = new BufferedWriter(new FileWriter(newFile));
				for (String line = br.readLine(); line != null; line = br.readLine()) 
				{
					if (line.trim().isEmpty() || line.trim().equals("\n") || removeLine(line))
					{
						continue;
					}
					if (line.matches(".*â€â€.*") || line.matches(".*â€.*"))
					{
						System.out.println("encountered garbage in line:\n" + line);
					}
					line.replace("†", "");
					line.replace("â€â€", "\"");
					line.replace("â€", "'");
					fileWriter.write(line);
					fileWriter.newLine();
				}
				br.close();
				fileWriter.close();
				if (newFile.length() == 0)
				{
					System.out.println("deleting file");
					boolean bool = newFile.delete();
					if (!bool)
					{
						System.out.println("did not delete file!");
					}
				}
				/*if (!fileAltered)  //If the file remains the same no need to clean it
				{
					File newFile = new File(destDirUrl + File.separator + file.getName());
					newFile.delete();
				}*/
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
}
