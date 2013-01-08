package tools.posTag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

import tools.MapTool;
import utils.Utils;

abstract public class TagMapTool extends MapTool
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
		initForFile();
		BufferedReader br = new BufferedReader(new FileReader(file));
		BufferedReader taggedbr = new BufferedReader(new FileReader(taggedFile));
		
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
		}
		br.close();
		taggedbr.close();
		doFinalStuff();
	}
	
	protected String getTaggedDirUrl()
	{
		String url = srcDirUrl;
		int i = url.lastIndexOf('\\');
		if (i == -1 )
		{
			i = url.lastIndexOf('/');
		}
		url = url.substring(0,i+1) + "Tagged_" + url.substring(i+1, url.length());
		return  url;
	}
}
