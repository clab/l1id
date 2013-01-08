package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;

import tools.AbstractTool;
import utils.Utils;


import config.ConfigManager;

/**
 * moves files from source folder to destination folder
 */
public class FilesMover extends AbstractTool 
{
	
	protected static volatile FilesMover instance = null;
	
	private FilesMover() throws Exception 
	{
		init();
	}
	
	public static FilesMover getInstance() throws Exception {
		if (instance == null) {
			System.out.println("start FilesMover");
			instance = new FilesMover();
			instance.doOp();
		}
		return instance;
	}

	private int i = 0;
	
	@Override
	protected void doOp() 
	{
		try {
			String path = ConfigManager.getInstance().getSourceFolder();
			File srcDir = Utils.getDir(path);
			
			File[] files = srcDir.listFiles(new FilenameFilter() {
		           public boolean accept(File dir, String name) {
		                return name.startsWith(ConfigManager.getInstance().getLangToMove());
		                }
		           });
			int count = 0;
			Integer numOfFilesToMove = ConfigManager.getInstance().getNumOfFilesToMove();
			for (File file : files) 
			{
				if (count % 50 == 0) System.out.println("moved " + count + " files");
				if (numOfFilesToMove != null && count == numOfFilesToMove)
				{
					break;
				}
				count++;
				i = 0;
				BufferedReader br = new BufferedReader(new FileReader(file));
				fileWriter = new BufferedWriter(new FileWriter(destDirUrl + File.separator + file.getName()));
							
				for (String line = br.readLine(); line != null ; line = br.readLine()) 
				{
					fileWriter.write(line);
					fileWriter.newLine();
				}
				fileWriter.close();
				br.close();
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
