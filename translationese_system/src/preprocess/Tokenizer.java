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
 * Go over corpus path and tokenize with Opennlp Tokenizer
 * @param args
 * @throws IOException
 */
public class Tokenizer extends AbstractTool 
{
	protected static volatile Tokenizer instance = null;
	
	private Tokenizer() throws Exception 
	{
	}
	
	public static Tokenizer getInstance() throws Exception {
		if (instance == null) {
			System.out.println("start Tokenizer");
			instance = new Tokenizer();
			instance.doOp();
		}
		return instance;
	}
	
	protected String getDestDirUrl()
	{
		String url = getSrcDirUrl();
		int i = url.lastIndexOf('\\');
		if (i == -1 )
		{
			i = url.lastIndexOf('/');
		}
		url = url.substring(0,i+1) + "Tokenized_" + url.substring(i+1, url.length());
		return  url;
	}
	
	//tokenizedCorpora
	
	
	@Override
	protected void doOp() 
	{
		try {
			String path = ConfigManager.getInstance().getTokenizerPath();
			opennlp.tools.lang.english.Tokenizer tokenizer = 
				new opennlp.tools.lang.english.Tokenizer(path);
									
			File[] files = srcDir.listFiles(new FilenameFilter() {
		           public boolean accept(File file, String name) {
		                return name.endsWith(extension); }
		           });
			int count = 0;
			for (File file : files) 
				{
					if (!file.getName().endsWith(extension))
					{
						continue;
					}
					if (count++ % 50 == 0) 
						System.out.println("done " + (count -1) + " files");
					BufferedReader br = new BufferedReader(new FileReader(file));
					String outPath = getDestDirUrl() + File.separator + file.getName();
					//System.out.println("outPath = " + outPath);
					File outFile = new File(outPath);
					outFile.createNewFile();
					fileWriter = new BufferedWriter(new FileWriter(outFile));
					
					for (String line = br.readLine(); line != null; line = br.readLine()) 
					{
				      if (line.equals("")) {
				    	  fileWriter.newLine();
				      }
				      else {
				        String[] tokens = tokenizer.tokenize(line);
				        if (tokens.length > 0) {
				        	fileWriter.write(tokens[0]);
				        }
				        for (int ti=1, tn = tokens.length; ti < tn; ti++) {
				        	fileWriter.write(" " + tokens[ti]);
				        }
				        fileWriter.newLine();
				      }
					}
					fileWriter.close();
				}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/*@Override
	public void doOp() 
	{
		try {
			opennlp.tools.lang.english.Tokenizer tokenizer = new opennlp.tools.lang.english.Tokenizer("C:/vered/Courses/NLP/models/EnglishTok.bin.gz");
									
			File[] dirs = srcDir.listFiles();
			for (File dir : dirs)
			{
				if (!dir.isDirectory())
				{
					continue;
				}
				String currDirName = dir.getName();
				File currDir = new File(destDirUrl + File.separator + currDirName);
				if (!currDir.exists())
				{
					currDir.mkdir();
				}
				System.out.println("Tokenizing directory: " + dir.getName());
				
				File[] files = dir.listFiles();
				for (File file : files) 
				{
					if (!file.getName().endsWith(extension))
					{
						continue;
					}
					BufferedReader br = new BufferedReader(new FileReader(file));
					String outPath = (destDirUrl + File.separator + currDirName + File.separator + file.getName());
					//System.out.println("outPath = " + outPath);
					File outFile = new File(outPath);
					outFile.createNewFile();
					fileWriter = new BufferedWriter(new FileWriter(outFile));
					
					for (String line = br.readLine(); line != null; line = br.readLine()) 
					{
				      if (line.equals("")) {
				    	  fileWriter.newLine();
				      }
				      else {
				        String[] tokens = tokenizer.tokenize(line);
				        if (tokens.length > 0) {
				        	fileWriter.write(tokens[0]);
				        }
				        for (int ti=1, tn = tokens.length; ti < tn; ti++) {
				        	fileWriter.write(" " + tokens[ti]);
				        }
				        fileWriter.newLine();
				      }
					}
					fileWriter.close();
				}
				
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

}
