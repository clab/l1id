package tools.multipleAttributes;

/**This class is a singleton class that creates and gives access to
 * a set of unigrams gathered from the corpora
 * */
public class LetterBiGramsTool extends LetterUniGramsTool
{
	protected static volatile LetterBiGramsTool instance = null;
	
	protected LetterBiGramsTool() 
	{
		super();
		nForLetterNGrams = 2;
		isDoTop = false;
	}
	
	public static LetterBiGramsTool getInstance()
	{
		if (instance == null)
		{
			System.out.println("start LetterBiGramsTool");
			instance = new LetterBiGramsTool();
			instance.doOp();
		}
		return instance;
	}
}
