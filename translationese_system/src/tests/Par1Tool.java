package tests;

import java.util.HashMap;
import java.util.Map;

import tools.multipleAttributes.AttributesManager;
import utils.Utils;

public class Par1Tool extends AttributesManager
{
	protected static volatile Par1Tool instance = null;
	protected Map<String, String> nicks = new HashMap<String,String>();
	
	protected Par1Tool()
	{
		initSet();
	}
	
	public static Par1Tool getInstance()
	{
		if (instance == null)
		{
			System.out.println("start Par1Tool");
			instance = new Par1Tool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void initSet()
	{
		dataSet.add("leftp");
		/*dataSet.add("rightp");
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
		dataSet.add("comma");*/
		
		nicks.put("(", "leftp");
		/*nicks.put(")", "rightp");
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
		nicks.put(",", "comma");*/
		
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
}
