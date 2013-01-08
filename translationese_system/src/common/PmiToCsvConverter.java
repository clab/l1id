package common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;

import utils.Utils;

import config.ConfigManager;

public class PmiToCsvConverter 
{
	public static String outDir = ConfigManager.getInstance().getDestFolder();
	
	static public void convert()
	{
		try {
			BufferedWriter fileWriter = null;
			File dir = Utils.getDir(outDir);
			
			File file = new File(outDir + File.separator + "PMI.txt");
			if (!file.exists())
			{
				System.out.println("PMI file is missing from " + outDir);
				return;
			}
			System.out.println("coverting " + file.getName());
			BufferedReader br = new BufferedReader(new FileReader(file));
			String fileName = file.getName().replace("txt", "csv");
			fileWriter = new BufferedWriter(new FileWriter(outDir + File.separator + fileName));
			for (String line = br.readLine(); line != null; line = br.readLine()) 
			{
				String newLine = getNewLine(line);
				fileWriter.write(newLine);
				fileWriter.newLine();
			}
				
			fileWriter.close();
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getNewLine(String line) 
	{
		String newLine = line.replace(",", "<comma>");   //first replace commas in the bigram
		newLine = newLine.replace("\"", "<qq>");
		newLine = newLine.replace("\'", "<q>");
		//replace last occurrence of ':' with ','
		int i = newLine.lastIndexOf(":");
		newLine = newLine.substring(0, i) + "," + newLine.substring(i + 1);
		//split bigram into token1 token2
		newLine = newLine.replace(" ", ",");
		if (newLine.split(",").length != 3)
		{
			System.err.println("error in line: " + line);
			System.err.println("translates into: " + newLine);
		}
		return newLine;
	}

	public static void main(String[] args) 
	{
		convert();
		System.out.println("Done!");
	}
}

