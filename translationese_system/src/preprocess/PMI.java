package preprocess;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import Exceptions.ToolsException;

import common.CorpusGeneralData;

import tools.MapTool;
import utils.Utils;

public class PMI extends MapTool
{
	protected static volatile PMI instance = null;
	
	protected String filename = null;
	
	private Map<String, Integer> uniMap = null;
	private Map<String, Integer> biMap = null;
	
	protected PMI() throws Exception 
	{
		filename = "pmi.txt";
		uniMap = new HashMap<String, Integer>();
		biMap = new HashMap<String, Integer>();
	}
	
	@Override
	protected void init()
	{
		super.init();
		System.out.println("in PMI");
		/*filename = "pmi.txt";
		uniMap = new HashMap<String, Integer>();
		biMap = new HashMap<String, Integer>();*/
	}
	
	@Override
	protected void createMap()
	{
		map = new TreeMap<String, Double>();
	}
	
	@Override
	protected void putInMap(String key, String value)
	{
		getDoubleMap().put(key, Double.valueOf(value));
	}
	
	public static PMI getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("in PMITool.getInstance() for the first time");
			instance = new PMI();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void setSize()
	{
		
	}
	
	@Override
	protected void buildObject() throws Exception
	{
		super.buildObject();
		generatePmiMap();
	}
	
	@Override
	protected void compute() throws Exception 
	{
		String prevToken = "";
		for (String token : currTokens)
		{
			String lowerCaseToken = token.toLowerCase();
			Utils.incStringIntegerMap(uniMap, lowerCaseToken);
			if (!prevToken.isEmpty() && !lowerCaseToken.isEmpty())
			{
				String bigram = prevToken + " " + lowerCaseToken;
				Utils.incStringIntegerMap(biMap, bigram);
			}
			prevToken = lowerCaseToken;
		}
	}
	

	private void generatePmiMap() throws Exception
	{
		int i = 0;
		for (Map.Entry<String, Integer> entry : biMap.entrySet()) 
		{
			String bigram = entry.getKey();
			Integer bigramInstances = entry.getValue();
			String[] bigramArr = bigram.split(" ");
			if (bigramArr.length > 2)
			{
				throw new ToolsException("A bigram of more than two words? " + "bigram: \"" + bigram +"\"");
			}
			String token1 = bigramArr[0];
			String token2 = bigramArr[1];
			int bigramsCount = CorpusGeneralData.getInstance().getBigramsNumber();
			int unigramsCount = CorpusGeneralData.getInstance().getUnigramsNumber();
			double biFreq = (double)bigramInstances / bigramsCount;
			//double token1Freq = (uniMap.get(token1) - bigramInstances) / unigramsCount;
			//double token2Freq = (uniMap.get(token2) - bigramInstances) / unigramsCount;
			
			//The previous version yields a lot of zeros, which yields infinity, which yields infinity as the average
			//number of occurrences / number of unigrams  
			double token1Freq = (double)(uniMap.get(token1)) / unigramsCount;
			double token2Freq = (double)(uniMap.get(token2)) / unigramsCount;
			
			//token1Freq = ( 0 == token1Freq ? 1 : token1Freq );
			//token2Freq = ( 0 == token2Freq ? 1 : token2Freq );
			
			if (++i < 20)
			{
				System.out.println("");
				System.out.println("bigram = " + bigram);
				System.out.println("token1 = " + token1);
				System.out.println("token2 = " + token2);
				System.out.println("bigram Instances = " + bigramInstances);
				System.out.println("bigram freq = " + biFreq);
				System.out.println("token1 number = " + uniMap.get(token1));
				System.out.println("token2 number = " + uniMap.get(token2));
				System.out.println("token1 freq = " + token1Freq);
				System.out.println("token2 freq = " + token2Freq);
			}
			Double pmi = null;
			
			try
			{
				if (0 == bigramInstances )
				{
					pmi = Double.MIN_VALUE;
					continue;
				}
				if (0 == token1Freq || 0 == token2Freq )
				{
					pmi = Double.MAX_VALUE;
					continue;
				}
				//PMI^k
				//PMI = Math.log(bigramsCount * (Math.pow(biFreq, p) / (token1Freq * token2Freq) ));
				
				
				double nominator = calcNominator(bigramsCount, biFreq);
				pmi = Utils.log2(nominator / (token1Freq * token2Freq) );
				
			} finally
			{
				if (i < 20) System.out.println("PMI = " + pmi);
				getDoubleMap().put(bigram, pmi);
			}
			
			
			//if (i < 20) System.out.println("PMI, base e = " + PMI);
			//PMI = PMI / Math.log(2);
			//if (i < 20) System.out.println("PMI, base 2 = " + PMI);
			i++;
		}
	}
	
	protected double calcNominator(int bigramsCount, double biFreq)
	{
		return biFreq;
	}

	public Map<String, Double> getCorpusPMImap() throws Exception 
	{
		return getDoubleMap();
		
	}
}
