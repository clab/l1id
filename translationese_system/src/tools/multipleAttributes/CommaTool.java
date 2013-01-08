package tools.multipleAttributes;

import java.util.HashMap;
import java.util.Map;

import tools.multipleAttributes.AttributesManager;
import utils.Utils;

public class CommaTool extends AttributesManager
{
	protected static volatile CommaTool instance = null;
	protected Map<String, String> nicks = new HashMap<String,String>();
	
	protected CommaTool()
	{
		initSet();
	}
	
	public static CommaTool getInstance()
	{
		if (instance == null)
		{
			System.out.println("start CommaTool");
			instance = new CommaTool();
			instance.doOp();
		}
		return instance;
	}
	
	@Override
	protected void initSet()
	{
		//dataSet.add("point");
		dataSet.add("comma");
		/*dataSet.add("leftp");
		dataSet.add("rightp");
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
		*/
		
		//nicks.put(".", "point");
		nicks.put(",", "comma");
		/*nicks.put("(", "leftp");
		nicks.put(")", "rightp");
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
		*/
		
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
			if (isPunct(token))
			{
				Utils.incStringIntegerMap(getIntegerMap(),  getPunct(token));
			}
		}
	}
	
	protected String getPunct(String token)
	{
		String par = String.valueOf(token.charAt(0));
		return nicks.get(par);
	}
	
	public boolean isPunct(String token) 
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
