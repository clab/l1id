package tools.multipleAttributes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

import config.ConfigManager;
import utils.MultiString;


/**This class is a singleton class that creates and gives access to
 * a set of character unigrams gathered from the corpora.
 * The unigrams are of the regex: [a-z|A-Z]. punctuations are 
 * excluded as are any other characters 
 * */
public class LetterUniGramsTool extends TopAttributesManager
{
	private static final String REPLACEMENT = "_";
	protected static boolean isDoTop = false;

	protected static volatile LetterUniGramsTool instance = null;
	
	protected int nForLetterNGrams = 1;
	
	public static LetterUniGramsTool getInstance()
	{
		if (instance == null)
		{
			System.out.println("start LetterUniGramsTool");
			instance = new LetterUniGramsTool();
			instance.doOp();
		}
		return instance;
	}
		
	@Override
	protected void buildObject() throws Exception
	{
		if (isDoTop)
		{
			super.buildObject();
		}
		else  //original buildObject
		{
			try {
				File[] files = srcDir.listFiles(new FilenameFilter() {
			           public boolean accept(File file, String name) {
			                return name.endsWith(extension);
			                }
			           });
				int count = 0;
				for (File file : files) 
				{
					if (count++ % 50 == 0) System.out.println("Done " + count + " files");
					currFileName = file.getName();
					//System.out.println(currFileName);
					doForFile(file);
				}
					
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void doForFile(File file) throws Exception
	{
		if (isDoTop)
		{
			super.doForFile(file);
		}
		else  //original doForFile
		{
			initForFile();
			BufferedReader br = new BufferedReader(new FileReader(file));
			for (currLine = br.readLine(); currLine != null ; currLine = br.readLine())
			{
				currTokens = currLine.split(" ");
				compute();
			}
			br.close();
			doFinalStuff();
		}
	}
	
	protected void doCompute(int step)
	{
		for (String token : currTokens)
		{
			if (token.equals(""))
			{
				continue;
			}
			MultiString paddedToken = new MultiString(
					getProcessedToken(token), 
					nForLetterNGrams, BOW, EOW);
			for (int i = 0; i < paddedToken.length() - 
								(nForLetterNGrams - 1); i++) 
			{
				String nGram = paddedToken.substring(
							i, i + nForLetterNGrams);
				String processedNGram = getProcessedNGram(nGram);
				if (retain(processedNGram))
				{
					doStep(step, processedNGram);
				}
			}
		}
	}
	
	@Override
	protected void doStep(int step, String str)
	{
		switch (step) {
		case 1:
			incAttrCount(str);
			break;
		case 2:
			dataSet.add(str);
			incAttrCount(str);
			break;
		case 3:
			testAndSetAttr(str);
			break;

		default:
			break;
		}
	}

	@Override
	protected void compute()
	{
		if (isDoTop)
		{
			doCompute(3);
		}
		else
		{
			doCompute(2);
		}
	}
	
	private String getProcessedNGram(String nGram)
	{
		if (nForLetterNGrams == 1 && nGram.equals(REPLACEMENT))
		{
			return null;
		}
		return nGram;
	}

	private String getProcessedToken(String token)
	{
		if  (ConfigManager.getInstance().retainAlphaLetterNGrams())
		{
			return token.replaceAll("[^a-z|^A-Z]", REPLACEMENT);
		}
		return token;
	}

	private boolean retain(String ngram)
	{
		return ngram != null;
	}
}
//	@Override
//	protected String getFileNamePrefix()
//	{
//		return super.getFileNamePrefix().replace("LetterN", getNPrefix());
//	}
//	
//	@Override
//	protected String getFolderPath()
//	{
//		return super.getFolderPath().replace("LetterN", getNPrefix());
//	}
//	
//	private String getNPrefix()
//	{
//		String prefix = "Letter";
//		switch (nForLetterNGrams)
//		{
//			case (1) : return prefix + "Uni";
//			case (2) : return prefix + "Bi";
//			case (3) : return prefix + "Tri";
//			default : return prefix + String.valueOf(nForLetterNGrams); 
//		}
//	}


/*
public class LetterUniGramsTool extends AttributesManager
{
	protected static volatile LetterUniGramsTool instance = null;
	
	protected int nForLetterNGrams = 1;
	
	protected LetterUniGramsTool() 
	{
		super();
	}
	
	public static LetterUniGramsTool getInstance()
	{
		if (instance == null)
		{
			System.out.println("start LetterUniGramsTool");
			instance = new LetterUniGramsTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void compute()
	{
		for (String token : currTokens)
		{
			MultiString paddedToken = new MultiString(token.toLowerCase(), 
					nForLetterNGrams, BOW, EOW);
			for (int i = 0; i < paddedToken.length() - 
								(nForLetterNGrams - 1); i++) 
			{
				String nGram = paddedToken.substring(
							i, i + nForLetterNGrams);
				
				if (retain(nGram))
				{
					dataSet.add(nGram);
					Utils.incStringIntegerMap(getIntegerMap(), nGram);
				}
			}
		}
	}
	
//	private boolean retain(String ngram)
//	{
//		if  (ConfigManager.getInstance().retainAlphaNumericLetterNGrams())
//		{
//			return !ngram.matches("\\w*");
//		}
//		return true;
//		
//	}
	
	private boolean retain(String ngram)
	{
		if  (ConfigManager.getInstance().excludePunctsForLetterNGrams())
		{
			return !PunctsTool.getInstance().isPunct(ngram);
		}
		return true;
	}
	
//	@Override
//	protected String getFileNamePrefix()
//	{
//		return super.getFileNamePrefix().replace("LetterN", getNPrefix());
//	}
//	
//	@Override
//	protected String getFolderPath()
//	{
//		return super.getFolderPath().replace("LetterN", getNPrefix());
//	}
//	
//	private String getNPrefix()
//	{
//		String prefix = "Letter";
//		switch (nForLetterNGrams)
//		{
//			case (1) : return prefix + "Uni";
//			case (2) : return prefix + "Bi";
//			case (3) : return prefix + "Tri";
//			default : return prefix + String.valueOf(nForLetterNGrams); 
//		}
//	}
}*/
