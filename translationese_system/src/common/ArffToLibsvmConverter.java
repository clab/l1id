package common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;

import utils.Utils;

import config.ConfigManager;

public class ArffToLibsvmConverter 
{
	public static String outDir = ConfigManager.getInstance().getDestFolder();
	
	static public void convert(boolean sparse)
	{
		try {
			BufferedWriter fileWriter = null;
			File dir = Utils.getDir(outDir);
			
			File[] files = dir.listFiles(new FilenameFilter() {
		           public boolean accept(File dir, String name) {
		                return name.toLowerCase().endsWith(".arff");
		                }
		           });
			for (File file : files) 
			{
				System.out.println("coverting " + file.getName());
				BufferedReader br = new BufferedReader(new FileReader(file));
				String fileName = file.getName().replace("arff", "lsvm");
				fileWriter = new BufferedWriter(new FileWriter(outDir + File.separator + fileName));
				boolean skipLine = true;
				for (String line = br.readLine(); line != null; line = br.readLine()) 
				{
					if (line.length() == 0)
					{
						continue;
					}
					
					if (skipLine)
					{
						if (line.equals("@data"))
						{
							skipLine = false;
						}
						continue;
					}
					line = cutLine(line, sparse);
					int index = 1;
					
					String[] splitLine = line.split(",");
					if (splitLine.length > 1)
					{
						String newLine = getNewLine(splitLine, sparse);
						for (int i = 0; i < splitLine.length -1 ; i++) 
						{
							newLine = newLine + " ";
							newLine = newLine + (sparse ?  getIndexVal(splitLine[i], sparse) : index++ + ":" + splitLine[i]);
						}
						fileWriter.write(newLine);
						fileWriter.newLine();
					}
				}
				
				fileWriter.close();
				br.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getNewLine(String[] splitLine, boolean sparse) 
	{
		//sparce is regarded as translationese output
		if (sparse)
		{
			return getLabel(splitLine[splitLine.length -1].endsWith("Original"));
		}
		return splitLine[splitLine.length - 1].equals("0") ? "-1" : "1";
	}

	private static String cutLine(String line, boolean sparse) 
	{
		if (sparse)
		{
			return line.substring(1, line.length() - 1);
		}
		return line;
	}

	public static String getIndexVal(String str, boolean sparse) 
	{
		String[] all = str.split(" ");
		int index = Integer.parseInt(all[0]);
		index++;
		return index + ":" + all[1];
	}
	

	public static String getLabel(boolean orig) 
	{
		return (orig ? "-1" : "+1");
	}
	
	public static void main(String[] args) 
	{
		boolean sparse = false;
		convert(sparse);
		System.out.println("Done!");
	}
}

