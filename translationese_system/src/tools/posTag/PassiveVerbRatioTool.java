package tools.posTag;

public class PassiveVerbRatioTool extends TagMapTool
{
	protected static volatile PassiveVerbRatioTool instance = null;
	
	private int numOfPassives = 0;
	private int numOfVerbs = 0;
	
	/*private int totPassives = 0;
	private int totVerbs = 0;*/
	
	protected PassiveVerbRatioTool() throws Exception 
	{
		setOrder(6);
	}
	
	public static PassiveVerbRatioTool getInstance() throws Exception
	{
		if (instance == null)
		{
			System.out.println("start PassiveVerbRatioTool");
			instance = new PassiveVerbRatioTool();
			instance.doOp();
		}
		return instance;
	}
	
	/*@Override
	protected void createMap()
	{
		map = new HashMap<String, Double>();
	}
	
	@Override
	protected void putInMap(String key, String value)
	{
		((Map<String, Double>)(map)).put(key, Double.valueOf(value));
	}*/
		
	private double computePassiveVerbRatio()
	{
		/*System.out.println("currFile = " + currFileName);
		System.out.println("numOfPassives = " + numOfPassives);
		System.out.println("numOfVerbs = " + numOfVerbs);
		System.out.println("----------------------------------");*/
		return (double) numOfPassives / numOfVerbs;
	}
	
	@Override
	protected void compute()
	{
		for (int i = 0; i < currTags.length ; ++i)
		{
			if (currTags[i].startsWith("VB"))
			{
				numOfVerbs++;
				if (checkPassivity(i))
				{
					numOfPassives++;
					//System.out.print("\n" + tokens[i] + ", tagged " + tags[i]  +", is Passive");
				}
				/*else
				{
					numOfActives++;
					//System.out.print("\n" + tokens[i] + ", tagged " + tags[i]  +",is Active");
				}*/
			}								
		}
		//System.out.println(":");
		//for (String token : tokens) {
		//	System.out.print(token + " ");
		//}
		//System.out.println();
	}
	/*/MD + "have" + "be" + /VBN,
	 *  (/VBZ || /VBP) + "being" + /VBN,
	 *   /VBD +	"been" + /VBN,
	 *    /MD + "be" + /VBN,
	 *     ( /VBZ || /VBP ) + /VBN,
	 *      ( "am" || "are" || "is" || "was" ||	"were" )/VBD + VBN.*/
	private boolean checkPassivity(int i)
	{
		/*return	check_MDhavebeenVBN(tags, tokens, i) || 
				check_VBZ_VBPbeingVBN(tags, tokens, i) ||
				check_VBDbeenVBN(tags, tokens, i) ||
				check_MDbeVBN(tags, tokens, i) ||
				check_VBZ_VBPVBN(tags, tokens, i) ||
				check_amVBDVBN(tags, tokens, i);*/
		
		if (!checkBounds(currTokens, i, 1) )
		{
			return false;
		}
			
		if ((currTokens[i-1].equals("am") || currTokens[i-1].equals("are") || currTokens[i-1].equals("is") 
				|| currTokens[i-1].equals("was") || currTokens[i-1].equals("were") || currTokens[i-1].equals("been")
				|| currTokens[i-1].equals("be") || currTokens[i-1].equals("being"))&&
				currTags[i].equals("VBN") )
		{
			//System.out.println("identified passive of ( am||are||is||was||were ) + /VBN ");
			//m_numOfActives--;
			return true;
		}
		
		if ((currTokens[i-1].endsWith("'s") || currTokens[i-1].endsWith("'m") || currTokens[i-1].endsWith("'re") )
				&& currTags[i].equals("VBN") 	)
		{
			//System.out.println("identified passive of ( 'm||'re||'s ) + /VBN ");
			//m_numOfActives--;
			return true;
		}
		return false;
				
	}
	
	private boolean checkBounds(String[] array, int i, int offset) 
	{
		return  ( i - offset >= 0 );
	}
	
	@Override
	protected void initForFile() 
	{
		numOfPassives = 0;
		numOfVerbs = 0;
	}
	
	@Override
	protected void doFinalStuff()
	{
		/*totPassives += numOfPassives;
		totVerbs += numOfVerbs;
		
		System.out.println("totPassives = " + totPassives);
		System.out.println("totVerbs = " + totVerbs);*/
		getIntegerMap().put(currFileName, normalize(computePassiveVerbRatio()));
	}
}
