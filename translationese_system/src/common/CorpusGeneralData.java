package common;

import java.io.IOException;

import tools.MapTool;
import tools.MapVarianceTool;
import tools.multipleAttributes.BigramsTool;
import tools.multipleAttributes.UnigramsTool;

public class CorpusGeneralData extends MapTool
{

	protected static volatile CorpusGeneralData instance = null;
	
	private int bigramsNumber = 0; //number of all bigrams
	private int unigramsNumber = 0; //number of all unigrams
	
	public static CorpusGeneralData getInstance()
	{
		if (instance == null) {
			System.out.println("start CorpusGeneralData");
			instance = new CorpusGeneralData();
			instance.doOp();
		}
		return instance;
	}
	
	public void writeCorpusGeneralData()
	{
		if (!map.isEmpty())
		{
			return;
		}
		unigramsNumber = UnigramsTool.getInstance().getUnigramsNumber();
		bigramsNumber = BigramsTool.getInstance().getBigramsNumber();
		try
		{
			getIntegerMap().put("nUnigrams", unigramsNumber);
			getIntegerMap().put("nBigrams", bigramsNumber);
			writeMap("CorpusGeneralData.txt");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/*public void readCorpusGeneralData() 
	{
		if (!map.isEmpty())
		{
			return;
		}
		try
		{
			readMap("corpusGeneralData.txt");
			unigramsNumber = map.get("nUnigrams").intValue();
			bigramsNumber = map.get("nBigrams").intValue();
			System.out.println("unigramsNumber = " + unigramsNumber);
			System.out.println("bigramsNumber = " + bigramsNumber);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}*/
	
	
	/**
	 * @return the number of all bigrams (including repetitions)
	 */
	public int getBigramsNumber() {
		return map.get("nUnigrams").intValue();
	}

	/**
	 * @return the number of all unigrams (including repetitions)
	 */
	public int getUnigramsNumber() {
		return map.get("nBigrams").intValue();
	}
}
