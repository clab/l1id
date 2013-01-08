package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;

import tools.AbstractTool;

public class ParenthesesTool extends AbstractTool 
{
	
	protected static volatile ParenthesesTool instance = null;
	private ParenthesesTool()
	{
		System.out.println("srcDir = " + srcDir);
		init();
		System.out.println("srcDir = " + srcDir);
	}
	
	public static ParenthesesTool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start ParenthesesTool");
			instance = new ParenthesesTool();
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
		System.out.println("srcDir = " + srcDir.getPath());
		try {
			File[] files = srcDir.listFiles(new FilenameFilter() {
		           public boolean accept(File file, String name) {
		                return name.endsWith(extension);
		        	  //return file.isFile();
		                }
		           });
			for (File file : files) 
			{
				BufferedReader br = new BufferedReader(new FileReader(file));
				String fileName = file.getName();
				File newFile = new File(destDirUrl + File.separator +fileName);
				if (!newFile.exists())
				{
					newFile.createNewFile();
				}
				fileWriter = new BufferedWriter(new FileWriter(newFile));
				for (String line = br.readLine(); line != null; line = br.readLine()) 
				{
					if (line.trim().isEmpty() || line.trim().equals("\n"))
					{
						continue;
					}
					char[] lineArray = line.toCharArray();
					if (doWriteLine(lineArray))
					{
						fileWriter.write(line);
						fileWriter.newLine();
					}
				}
				br.close();
				if (null != fileWriter)
					fileWriter.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean doWriteLine(char[] array)
	{
		int left = count(array, '(');
		int right = count(array, ')');
		return left != right;
			
	}

	private int count(char[] array, char c)
	{
		int count = 0;
		for (int i = 0; i < array.length; i++)
		{
			if (array[i] == c)
			{
				count++;
			}
		}
		return count;
	}
}
