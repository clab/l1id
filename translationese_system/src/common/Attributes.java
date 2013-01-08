package common;

/**
 * This enumeration stands for the available features
 * */
public enum Attributes
{
	TTR("tools.TTRTool", "Type/token ratio", "TTRTool" ),  //1
	
	TTR2("tools.TTR2Tool", "ttr2", "TTR2Tool" ),
	
	TTR3("tools.TTR3Tool", "tt3", "TTR3Tool"),
	
	AVG_WORD_LENGTH("tools.AvgWordLengthTool", "Mean word length",
			"avgWordLenth"),
			
	AVG_SYLLABLES_NUMBER("tools.SyllablesRatioTool", 
			"syllables Ratio", "syllablesRatio"),		//5
			
	LEXICAL_DENSITY("tools.posTag.LexicalDensityTool",
			"Lexical Density", "LexicalDensity"),
			
	AVG_SENTENCE_LENGTH("tools.AvgSentenceLengthTool", 
			"Mean sentence length", "avgSentenceLength"), 
			
	AVG_RANK1("tools.Rank1Tool", "Mean word rank1", "Rank1Tool"),   
	AVG_RANK2("tools.Rank2Tool", "Mean word rank2", "Rank2Tool"), 			
			
	WORD_FREQ("tools.multipleAttributes.WordFreqTool",  //10
			"N Top frequent words", "WordFreq"),
			
	WORD_FREQ_BY_POSITION("tools.multipleAttributes.WordFreqByPositionTool",
			"Positional Token frequency", "WordFreqByPosition"), 
			
	EXPLICIT_NAMING("tools.posTag.ExplicitNamingTool",
			"Explicit Naming", "ExplicitNaming"),   
			
	SINGLE_NNP("tools.posTag.SingleNNPTool",
			"Single Naming", "Single_nnp"),
			
	MULTIPLE_NNP("tools.posTag.MultipleNNPTool",
			"Mean Multiple Naming", "Multiple_nnp"),
			
	CONNECTIVES("tools.multipleAttributes.ConnectivesTool",
			"Coheseve Markers", "Connectives"),  //15
			
	PRONOUNS("tools.multipleAttributes.PronounsTool",
			"Pronouns", "Pronouns"),
	
	POS_UNIGRAMS("tools.multipleAttributes.PosUnigramsTool",
			"POS-tag unigrams", "posUnigrams"), 
	
	POS_BIGRAMS("tools.multipleAttributes.PosBigramsTool",
			"POS-tag bigrams", "posBigrams"),
	
	POS_TRIGRAMS("tools.multipleAttributes.PosTrigramsTool",
			"POS-tag trigrams", "posTrigrams"),
			
	LETTER_UNI_GRAMS("tools.multipleAttributes.LetterUniGramsTool", //20
					"letter unigrams", "LetterUnigrams"),
	LETTER_BI_GRAMS("tools.multipleAttributes.LetterBiGramsTool",
			"letter bigrams", "LetterBigrams"), 
	LETTER_TRII_GRAMS("tools.multipleAttributes.LetterTriGramsTool",
			"letter trigrams", "LetterTrigrams"), 
			
	CONTEXTUAL_FW("tools.multipleAttributes.tags.ContextFWTool",
			"Contextual function words", "contextualFW"),
	
	REPETITIONS("tools.RepetitionsTool",
			"Repetitions", "Repetitions"), 
			
	CONTRACTIONS("tools.multipleAttributes.ContractionsTool",
			"Contractions", "Contractions"),   //25
			
	AVG_PMI("tools.PmiAverage", "Average PMI", "PMIAvg"), 
	PMI_THRESHOLD("tools.PmiThreshold", "Threshold PMI", "PMIThreshold"), 
	
	FW("tools.multipleAttributes.FWTool",
			"Function Words", "Function Words"),
			
	PUNCTUATION("tools.multipleAttributes.tags.PunctsTool", "Punctuation",
			"punctuations"),
			
	PUNCTUATION_RATIO_1("tools.multipleAttributes.tags.PunctsRatioTool", "PunctuationRatio1",
			"punctuations_1"),  //30
					
	PUNCTUATION_RATIO_2("tools.multipleAttributes.tags.PunctsRatio2Tool", "PunctuationRatio2",
			"punctuations_2"),  
			
	PASSIVE_VERB_RATIO("tools.posTag.PassiveVerbRatioTool",
			"Ratio of Passive forms", "PassiveVerbRatioTool"),
			
	TOKEN_UNIGRAMS("tools.multipleAttributes.UnigramsTool",
			"Token unigrams", "unigrams"),
	
	TOKEN_BIGRAMS("tools.multipleAttributes.BigramsTool",
			"Token bigrams", "bigrams"), 
			
	COMMA("tools.multipleAttributes.CommaTool",
			"Commas and points", "CommasNpoints"), 
			
	TOKENIZER("preprocess.Tokenizer", 
			"Tokenizer", "Tokenizer"),  //36
	
	CHOPPER("preprocess.Chopper",   //37
			"Chopper", "Chopper"), 
	
	TAGGER("preprocess.Tagger",
			"Tagger", "Tagger"),   //38
	
	WEKATOOL("tools.WekaTool",
				"WekaTool", "WekaTool"), //39
	
	CLASSIFICATIONTOOL("tools.ClassificationTool",
					"ClassificationTool", "ClassificationTool"),  //40
						
	LATEXTOOL("tools.LatexTool",
			"LatexTool", "LatexTool"), 
	
	PARENTHESESTOOL("utils.ParenthesesTool",
			"ParenthesesTool", "ParenthesesTool");
	
	private String featureName;
	private String filename;
	private String prefix;
	private String cannonicalName;
	private boolean isComplex = false;
	
	Attributes() 
	{
		this("", "", "");
	}
	
	Attributes(String cannonicalName, String featureName, 
			String filename)
	{
		System.out.println("23:55");
		this.cannonicalName = cannonicalName;
		this.featureName = featureName;
		this.filename = filename;
		isComplex = isComplexAttr(cannonicalName);
		if (isComplex)
		{
			prefix = filename + "_";
		}
		prefix = isComplex ? prefix : filename;
	}
	
	public static int size()
	{
		return 42;
	}
	
	public static int features()
	{
		return 35;
	}
	
	public int getFeatureNum()
	{
		return ordinal();
	}
	
	public String getFileName()
	{
		return filename;
	}
	
	public String getFeaureName()
	{
		return featureName;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public String getCannonicalName() 
	{
		return cannonicalName;
	}
	
	private boolean isComplexAttr(String cannonicalName)
	{
		String regex = ".*multipleAttributes.*";
		return cannonicalName.matches(regex);
	}
	
	public boolean isComplex()
	{
		return isComplex;
	}
}
