package tests;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import tools.multipleAttributes.AttributesManager;
import utils.Utils;

public class Par5Tool extends AttributesManager
{
	protected static volatile Par5Tool instance = null;
	protected Map<String, String> nicks = new HashMap<String,String>();
	
	protected Par5Tool()
	{
		initSet();
	}
	
	public static Par5Tool getInstance()
	{
		if (instance == null)
		{
			System.out.println("start Par5Tool");
			instance = new Par5Tool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void initSet()
	{
		dataSet.add("leftp");
		//dataSet.add("rightp");
		dataSet.add("point");
		dataSet.add("dq");
		dataSet.add("q");
		dataSet.add("qm");
		dataSet.add("em");
		dataSet.add("apostrophe");
		dataSet.add("semiColon");
		dataSet.add("colon");
		dataSet.add("hiphen");
		dataSet.add("slash");
		dataSet.add("rsb");
		dataSet.add("lsb");
		dataSet.add("comma");
		
		nicks.put("(", "leftp");
		//nicks.put(")", "rightp");
		nicks.put(".", "point");
		nicks.put("\"", "dq");
		nicks.put("'", "q");
		nicks.put("?", "qm");
		nicks.put("!", "em");
		nicks.put("`", "apostrophe");
		nicks.put(";", "semiColon");
		nicks.put(":", "colon");
		nicks.put("-", "hiphen");
		nicks.put("/", "slash");
		nicks.put("]", "rsb");
		nicks.put("[", "lsb");
		nicks.put(",", "comma");
		
	}
	
	@Override
	protected void compute()
	{
		for (int i=0 ; i < currTokens.length ; ++i)
		{
			String token = currTokens[i];
			if (token.isEmpty())
			{
				continue;
			}
			if (isPar(token))
			{
				Utils.incStringIntegerMap(getIntegerMap(),  getPar(token));
			}
		}
	}
	
	protected String getPar(String token)
	{
		String par = String.valueOf(token.charAt(0));
		return nicks.get(par);
	}
	
	public boolean isPar(String token) 
	{
		if (token != null || token.length() > 0)
		{
			try{
				String tok = String.valueOf(token.charAt(0));
				return token.length() == 1 && nicks.keySet().contains(tok);
			} catch (StringIndexOutOfBoundsException e)
			{
				System.out.println("token = ---" + token + "---");
			}
		}
		return false;
	}
	
	@Override
	protected void doFinalStuff() throws IOException
	{
		String outputFileName = destDirUrl + java.io.File.separator + "parentheses.csv";
		BufferedWriter fw = new BufferedWriter(new FileWriter(outputFileName, true));
		int right =  map.get("rightp") == null ? 0 : getIntegerMap().get("rightp");
		int left =  map.get("leftp") == null ? 0 : getIntegerMap().get("leftp");
		fw.append(currFileName + "," + left + "," + right);
		fw.newLine();
		fw.close();
		super.doFinalStuff();
	}
}
