package tools.multipleAttributes;

import java.io.IOException;

import common.CorpusGeneralData;

import utils.Utils;

/**This class is a singleton class that creates and gives access to
 * a set of unigrams gathered from the corpora
 * */
public class UnigramsTool extends TopAttributesManager
{
	protected static volatile UnigramsTool instance = null;
	private int unigramsNumber = 0; //number of all unigrams
	
	
	protected UnigramsTool()
	{
		super();
	}
	
	public static UnigramsTool getInstance()
	{
		if (instance == null)
		{
			System.out.println("start UnigramsTool");
			instance = new UnigramsTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void doCompute(int step)
	{
		for (String token : currTokens)
		{
			if (token != null && !"".equals(token))
			{
				String lowerCaseToken = token.toLowerCase();
				doStep(step, lowerCaseToken);
				/*dataSet.add(lowerCaseToken);
				Utils.incStringIntegerMap(getIntegerMap(),
								lowerCaseToken);*/
				unigramsNumber++;
			}
		}
	}
	
	public int getUnigramsNumber()
	{
		return unigramsNumber;
	}
	
	@Override
	public void writeObject() throws IOException
	{
		super.writeObject();
		CorpusGeneralData.getInstance().writeCorpusGeneralData();
	}
	
	@Override
	public void readObject() throws Exception
	{
		super.readObject();
		CorpusGeneralData.getInstance();
	}
}
