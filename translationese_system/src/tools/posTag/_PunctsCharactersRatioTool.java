package tools.posTag;

import java.io.File;
import java.util.Map;
import java.util.SortedMap;

import tools.multipleAttributes.tags.AttributesTagManager;

public class _PunctsCharactersRatioTool extends AttributesTagManager
{
	protected static volatile _PunctsCharactersRatioTool instance = null;
	
	protected _PunctsCharactersRatioTool() throws Exception 
	{
		initSet();
		setOrder(6);
	}
	
	public static _PunctsCharactersRatioTool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start PunctsCharactersRatioTool");
			instance = new _PunctsCharactersRatioTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void buildObject() throws Exception 
	{
		Map<String, SortedMap<String, Integer>> punctsFilesMap = 
				_PunctsTool.getInstance().getFilesData();
		
		String path = srcDirUrl + File.separator;
		
		for (Map.Entry<String, SortedMap<String, Integer>> entry : 
			punctsFilesMap.entrySet())
		{
			String fileName = entry.getKey();
			File currFile = new File(path + fileName);
			long numOfChars = currFile.length();
			SortedMap<String, Integer> puncts = entry.getValue();
			for (Map.Entry<String, Integer> pEntry : puncts.entrySet()) 
			{
				int val = normalize((double)pEntry.getValue() / numOfChars);
				getIntegerMap().put(pEntry.getKey(), val);
			}
			doFinalStuff();
		}
	}
		
	/*@Override
	protected void buildObject() throws Exception 
	{
		//TBD
		File[] punctsFiles = destDir.listFiles(new FilenameFilter() {
	           public boolean accept(File file, String name) {
	                return name.endsWith(extension);
	                }
	           });
		
		//TBD
		File[] tokensFiles = destDir.listFiles(new FilenameFilter() {
	           public boolean accept(File file, String name) {
	                return name.endsWith(extension);
	                }
	           });
		
		if (tokensFiles.length != punctsFiles.length)
		{
			System.err.println("Error!!! tokensFiles.length != punctsFiles.length");
			System.err.println("in class - " + getClass().getCanonicalName());
			System.err.println("destDir - " + destDir);
			throw new Exception();
		}
				
		for (int i = 0 ; i < punctsFiles.length ; ++i) 
		{
			if (i % 50 == 0) 
				System.out.println("Done " + i + " files");
			
			File pFile = punctsFiles[i];
			File tFile = tokensFiles[i];
			currFileName = pFile.getName();
			
			doForFile(tFile, pFile);
		}
	}
	
	protected void doForFile(File tFile, File pFile) throws Exception
	{
		BufferedReader tBr = new BufferedReader(new FileReader(tFile));
		BufferedReader pBr = new BufferedReader(new FileReader(pFile));
		
		for (String tLine = tBr.readLine(), pLine = pBr.readLine(); 
				tLine != null && pLine != null ; 
				tLine = tBr.readLine(), pLine = pBr.readLine()  )
		{
			currPun = tLine.split(" ");
			currTags = pLine.split(" ");
			if (currTokens.length != currTags.length)
			{
				System.out.println("Error: tokens.length != tags.length for file " + 
						currFileName + ",\n line = " + tLine + ",\n taggedLine = " + pLine);
			}
			compute();
		}
		tBr.close();
		pBr.close();
		doFinalStuff();
	}*/
}
