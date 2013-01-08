package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FileNamesChanger extends AbstractTool 
{
	
	protected static volatile FileNamesChanger instance = null;
	
	private FileNamesChanger() throws Exception 
	{
	}
	
	public static FileNamesChanger getInstance() throws Exception {
		if (instance == null) {
			System.out.println("start FileNamesChanger");
			instance = new FileNamesChanger();
		}
		return instance;
	}

	@Override
	protected void doOp() 
	{
		try {
			File[] files = destDir.listFiles();
			for (File file : files) 
			{
				BufferedReader br = new BufferedReader(new FileReader(file));
				String fileName = file.getName().replace("_", " ");
				fileWriter = new BufferedWriter(new FileWriter(destDirUrl + File.separator + fileName));
				
				String line = br.readLine();
				while (line != null) 
				{
					fileWriter.write(line);
					fileWriter.newLine();
					line = br.readLine();
				}
				fileWriter.close();
				br.close();
				file.delete();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
