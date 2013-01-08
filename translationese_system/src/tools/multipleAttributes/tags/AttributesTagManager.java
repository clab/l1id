package tools.multipleAttributes.tags;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

import tools.multipleAttributes.AttributesManager;
import utils.Utils;

/**This class is a singleton class that creates and gives access to
 * sets data gathered from the entire corpora to be used as attributes and the actual data files per chunk
 * */
abstract public class AttributesTagManager extends AttributesManager
{
	
	protected String[] currTags = null;
	
	@Override
	protected void buildObject() throws Exception 
	{
		File[] files = srcDir.listFiles(new FilenameFilter() {
	           public boolean accept(File file, String name) {
	                return name.endsWith(extension);
	                }
	           });
		
		File[] taggedFiles = Utils.getDir(getTaggedDirUrl()).listFiles(new FilenameFilter() {
	           public boolean accept(File file, String name) {
	                return name.endsWith(extension);
	                }
	           });
		
		if (files.length != taggedFiles.length)
		{
			System.err.println("Error!!! files.length != taggedFiles.length");
			System.err.println("in class - " + getClass().getCanonicalName());
			System.err.println("srcDir - " + srcDir);
			System.err.println("getTaggedDirUrl() - " + getTaggedDirUrl());
			throw new Exception();
		}
				
		for (int i = 0 ; i < files.length ; ++i) 
		{
			if (i % 50 == 0) 
				System.out.println("Done " + i + " files");
			
			File file = files[i];
			File taggedFile = taggedFiles[i];
			currFileName = file.getName();
			
			doForFile(file, taggedFile);
		}
	}
	
	protected void doForFile(File file, File taggedFile) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(file));
		BufferedReader taggedbr = new BufferedReader(new FileReader(taggedFile));
		
		//int count = 1; 
		for (String line = br.readLine(), taggedLine = taggedbr.readLine(); 
				line != null && taggedLine != null ; 
				line = br.readLine(), taggedLine = taggedbr.readLine()  )
		{
			currTokens = line.split(" ");
			currTags = taggedLine.split(" ");
			if (currTokens.length != currTags.length)
			{
				System.out.println("Error: tokens.length != tags.length for file " + 
						currFileName + ",\n line = " + line + ",\n taggedLine = " + taggedLine);
			}
			compute();
			/*if (file.getName().startsWith("Italian") )/*&& (
					count == 11 || count  == 19 || count == 30 ||
					 count  == 43 || count == 57 ||  count  == 66	 || count == 74 || 
				 count  == 76 || count == 79 ||  count  == 84) )*/
			/*{
				System.out.println("line no. " + count + ": " + getIntegerMap().get("\""));
			}
			count++;*/
		}
		br.close();
		taggedbr.close();
		doFinalStuff();
	}
}

