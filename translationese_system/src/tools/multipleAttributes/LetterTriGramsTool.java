package tools.multipleAttributes;

/**This class is a singleton class that creates and gives access to
 * a set of unigrams gathered from the corpora
 * */
public class LetterTriGramsTool extends LetterUniGramsTool
{
	protected static volatile LetterTriGramsTool instance = null;
	
	protected LetterTriGramsTool() 
	{
		super();
		nForLetterNGrams = 3;
		isDoTop = false;
	}
	
	public static LetterTriGramsTool getInstance()
	{
		if (instance == null)
		{
			System.out.println("start LetterTriGramsTool");
			instance = new LetterTriGramsTool();
			instance.doOp();
		}
		return instance;
	}
}
