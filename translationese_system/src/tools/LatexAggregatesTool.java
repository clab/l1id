package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import config.ConfigManager;

public class LatexAggregatesTool
{

	private String path = null;
	private String extension = ConfigManager.getInstance().getExtension();
	
	protected void doOp() 
	{
		try{
			BufferedWriter resultswriter = new BufferedWriter(
					new FileWriter(path + File.separator + "results.csv"));
			
			//resultswriter.write("\n\n\\documentclass{article}\n");
			//resultswriter.write("\\begin{document}\n");
			//resultswriter.write("\n\\begin{table}[hbt]\n");
			//resultswriter.write("\\begin{center}\n");
			File resultsDir = new File(path);
			
			//(language, (feature, value))
			Map<String, SortedMap<String,String>> map = new HashMap<String, SortedMap<String,String>>(); 
			
			File[] files = resultsDir.listFiles(new FilenameFilter() {
		           public boolean accept(File file, String name) {
		                return (name.endsWith(extension) && !name.replace(extension, "" ).endsWith("Attributes"));
		                }
		           });
			for (File file : files) 
			{
				//System.out.println("file is " + file.getName());
				
				BufferedReader br = new BufferedReader(new FileReader(file));
				String fileName = file.getName();
				String attrName = fileName.replace(extension,"");
				
				for (String line = br.readLine(); line != null ; line = br.readLine() )
				{
					//System.out.println("line = " + line);
					String[] lineArr = line.split(":");
					String lang = lineArr[0];
					String value = lineArr[1];
					SortedMap<String, String> langMap = getLangMap(map, lang);
					langMap.put(attrName, value);
				}
				br.close();
			}
			
			//defineTabular(map, resultswriter);
			fillTabular(map, resultswriter);
			
			//resultswriter.write("\\end{tabular}\n\n");
			//resultswriter.write("\\end{center}\n\n");
			//resultswriter.write("\\end{table}\n\n");
			//resultswriter.write("\\end{document}\n\n");
			resultswriter.flush();
			resultswriter.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void defineTabular(Map<String, SortedMap<String, String>> map, BufferedWriter resultswriter) throws IOException
	{
		resultswriter.write("\\begin{tabular}{l");
		for (int i = 0; i < map.values().iterator().next().size(); i++) 
		{
			resultswriter.write("r");
		}
		resultswriter.write("}\n");
		
	}
	
	private void fillTabular(Map<String, SortedMap<String, String>> map, BufferedWriter resultswriter) throws IOException
	{
		firstLine(map, resultswriter);
		//resultswriter.write("Language&Features enabled&"+ title + "\\ (\\%)\\\\\n");
		for (Map.Entry<String, SortedMap<String, String>> entry : map.entrySet()) 
		{
			resultswriter.write(entry.getKey());
			for (String val : entry.getValue().values()) 
			{
				//resultswriter.write("&" + val);
				resultswriter.write("," + val);
			}
			//resultswriter.write("\\\\\n");
			resultswriter.write("\n");
		}
	}
	
	private void firstLine(Map<String, SortedMap<String, String>> map,
			BufferedWriter resultswriter) throws IOException
	{
		resultswriter.write("Language");
		Iterator<SortedMap<String, String>> iterator = map.values().iterator();
		if (iterator.hasNext())
		{
			for (String feature : iterator.next().keySet()) 
			{
				//resultswriter.write("&" + feature);
				resultswriter.write("," + feature);
			}
			resultswriter.write("\n");
			//resultswriter.write("\\\\\n");
		}
	}

	private SortedMap<String, String> getLangMap(Map<String, SortedMap<String, String>> map, String lang)
	{
		SortedMap<String, String> langMap = map.get(lang);
		if (null == langMap)
		{
			langMap = new TreeMap<String, String>();
			map.put(lang, langMap);
		}
		return langMap;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		LatexAggregatesTool tl = new LatexAggregatesTool();
		if (args.length < 1)
		{
			System.err.println("filename argument is missing");
			return;
		}
		tl.setPath(args[0]);
		tl.doOp();
		System.out.println("Done LatexTool!");
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
