package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import opennlp.maxent.PlainTextByLineDataStream;

import config.ConfigManager;

public class BinsWriter 
{
	static private Map<Integer, Integer> sentenceLegnthMap = new HashMap<Integer, Integer>();
	
	static private void updateSenetencesLengthMap(String[] tokens)
	{
		int currLength = tokens.length;
		int key = currLength < 100 ? currLength : 100;
		Utils.incMap(sentenceLegnthMap, key);
	}
	
	static public void generateSentencesBins()
	{
		try {
			String srcDir = ConfigManager.getInstance().getSourceFolder();
			String outDir = ConfigManager.getInstance().getDestFolder();
			String corpus = srcDir.substring(srcDir.lastIndexOf("/") + 1);
			
			File dir = Utils.getDir(srcDir);
			
			File[] files = dir.listFiles();
			for (File file : files) 
			{
				BufferedReader br = new BufferedReader(new FileReader(file));
						
				PlainTextByLineDataStream textStream = new PlainTextByLineDataStream(br);
				
				if (textStream == null)
					continue;
				while (textStream.hasNext()) {
			          String line = (String) textStream.nextToken();
			          String[] tokens = line.split(" ");
			          updateSenetencesLengthMap(tokens);
				}
				
				br.close();
				file.delete();
			}
			writeBins(outDir + File.separator + corpus + ".csv");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void writeBins(String location) throws IOException 
	{
		BufferedWriter binsWriter = new BufferedWriter(new FileWriter(location));
		for (Map.Entry<Integer, Integer> entry : sentenceLegnthMap.entrySet())
		{
			String line = entry.getKey() + "," + entry.getValue();
			binsWriter.write(line);
			binsWriter.newLine();
		}
		binsWriter.close();
	}
	
	public static void main(String[] args) {
		generateSentencesBins();
		System.out.println("Done!");
	}
}
