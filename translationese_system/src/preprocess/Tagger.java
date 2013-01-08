package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;


import opennlp.tools.lang.english.ParserTagger;
import opennlp.tools.postag.POSTaggerME;
import tools.posTag.TagMapTool;

import config.ConfigManager;

public class Tagger extends TagMapTool 
{
	protected static volatile Tagger instance = null;
	
	private Tagger() throws Exception 
	{
	}
	
	public static Tagger getInstance() throws Exception {
		if (instance == null) {
			System.out.println("start Tagger");
			instance = new Tagger();
			instance.doOp();
		}
		return instance;
	}
	
	protected String getDestDirUrl()
	{
		return getTaggedDirUrl();
	}

	
	@Override
	protected void doOp() 
	{
		try {
			String tagdict = ConfigManager.getInstance().getTagdictPath();
			String dictFile = ConfigManager.getInstance().getDictFilePath();
			POSTaggerME tagger = new ParserTagger(dictFile, tagdict, false);
			
			
			File[] files = srcDir.listFiles(new FilenameFilter() {
		           public boolean accept(File dir, String name) {
		                return name.endsWith(extension);
		                }
		           });
									
			String dirPrefix = destDirUrl + File.separator;
			TagFiles(tagger, dirPrefix, files);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void TagFiles(POSTaggerME tagger, String dirPrefix, File[] files)
			throws FileNotFoundException, IOException {
		int count = 0;
		for (File file : files) 
		{
			//System.out.println(file.getName());
			BufferedReader br = new BufferedReader(new FileReader(file));
			String outPath = (dirPrefix + file.getName());
			File outFile = new File(outPath);
			outFile.createNewFile();
			fileWriter = new BufferedWriter(new FileWriter(outFile));
			
			if (count++ % 50 == 0) 
				System.out.println("done " + (count -1) + " files");
			
			for (String line = br.readLine(); line != null; line = br.readLine()) 
			{
		      if (line.equals("")) {
		    	  fileWriter.newLine();
		      }
		      else {
		        String[] tags = tagger.tag(line.split(" "));
		        
		        if (tags.length > 0) {
		        	fileWriter.write(tags[0]);
		        }
		        
		        for (int i=1, length = tags.length; i < length; i++) {
		        	fileWriter.write(" " + tags[i]);
		        }
		        
		        fileWriter.newLine();
		      }
			}
			 br.close();
			fileWriter.close();
		}
	}

}
