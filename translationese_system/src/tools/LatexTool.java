package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.text.DecimalFormat;
import java.util.SortedMap;
import java.util.TreeMap;

import common.Attributes;

public class LatexTool
{

	private String path = null;
	private String caption = null;
	private String label = null;
	private String title = "Acc.";
	
	protected void doOp() 
	{
		try{
			BufferedWriter resultswriter = new BufferedWriter(
					new FileWriter(path + File.separator + "results.tex"));
			
			resultswriter.write("\n\n\\documentclass{article}\n");
			resultswriter.write("\\begin{document}\n");
			resultswriter.write("\n\\begin{table}[hbt]\n");
			resultswriter.write("\\begin{center}\n");
			resultswriter.write("\\begin{tabular}{llr}\n");
			resultswriter.write("&Features enabled&"+ title + "\\ (\\%)\\\\\n");
			
			File resultsDir = new File(path);
			
			SortedMap<Double, String> map = new TreeMap<Double, String>(); 
			
			File[] files = resultsDir.listFiles(new FilenameFilter() {
		           public boolean accept(File file, String name) {
		                return name.endsWith(".txt");
		                }
		           });
			String table_aData = "";
			for (File file : files) 
			{
				//System.out.println("file is " + file.getName());
				
				BufferedReader br = new BufferedReader(new FileReader(file));
				String fileName = file.getName();
				int end = fileName.length()-4;
				int begin = fileName.lastIndexOf("_") + 1;
				String attNum = fileName.substring(begin, end);
				int attrNumber = 0;
				int thresh = 800; //again, a very wrong value
				if (attNum.indexOf("_") == -1)
				{
					attrNumber = Integer.valueOf(attNum) - 1;
				}
				else
				{
					attrNumber = Integer.valueOf(attNum.substring(0,2)) - 1;
					thresh = Integer.valueOf(attNum.substring(3,4));
				}
				
				for (String line = br.readLine(); line != null ; line = br.readLine() )
				{
					if (!line.startsWith("Correctly Classified Instances"))
					{
						continue;
					}
					int featureNumber = 800;  //800 is just a very wrong value
					String featureName = "";
					
					for (Attributes attr : Attributes.values())
					{
						//String attrName = filaName.substring(0, filaName.length()-4);
						//if (attr.getFileName().equals(attrName))
						if (attr.getFeatureNum() == attrNumber)
						{
							featureNumber = attr.getFeatureNum() + 1;
							featureName = attr.getFeaureName();
							break;
						}
					}
					String accuracy = getAccuracy(line);
					if (featureNumber -1 == Attributes.TOKEN_BIGRAMS.getFeatureNum() || 
							featureNumber -1 == Attributes.TOKEN_UNIGRAMS.getFeatureNum())   //n-grams in a different table
					{
						 table_aData = table_aData + " & " + featureName + " & " + accuracy + "\\\\\n";
						 break;
					}
					String tableLine = featureNumber + " & " + featureName;
					if (thresh != 800)
					{
						tableLine = tableLine + " with threshold " + thresh;
					}
					else
					{
						thresh = 0;
					}
										
					tableLine = tableLine + " & " + accuracy;
					map.put(featureNumber + (double)thresh / 10, tableLine);
					break;
				}
				br.close();
			}
			
			for (String line : map.values()) 
			{
				resultswriter.write(line+"\\\\\n");
			}
			
			resultswriter.write("&&\\\\\n");
			resultswriter.write(table_aData);
			resultswriter.write("\\end{tabular}\n");
			resultswriter.write("\\end{center}\n");
			if (caption != null)
			{
				resultswriter.write("\\caption{" + caption + "}\n");
			}
			if (label != null)
			{
				resultswriter.write("\\label{" + label + "}\n");
			}
			resultswriter.write("\\end{table}\n\n");
			resultswriter.write("\\end{document}\n\n");
			resultswriter.flush();
			resultswriter.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private String getAccuracy(String line) 
	{
		String accStr = line.split(" +")[4];
		accStr = accStr.replace("%", "");
		
		Double acc = Double.valueOf(accStr);
		
		return String.valueOf(Math.round(acc));
		//DecimalFormat df = new DecimalFormat("0.00");
		//df.format(acc);
		
		//return df.format(acc);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		LatexTool tl = new LatexTool();
		if (args.length < 1)
		{
			System.err.println("filename argument is missing");
			return;
		}
		if (args.length > 1)
		{
			tl.setCaption(args[1]);
		}
		
		if (args.length > 2)
		{
			tl.setLabel(args[2]);
		}
		
		if (args.length > 3)
		{
			tl.setTitle(args[3]);
		}
		
		tl.setPath(args[0]);
		tl.doOp();
		System.out.println("Done LatexTool!");
	}

	private void setCaption(String caption) 
	{
		this.caption = caption;
	}
	
	private void setLabel(String label) 
	{
		this.label = label;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
