package tools;

public enum FileNamesForStatisticsEnum 
{
	AVG_SENTENCE_LENGTH("avgSentenceLength"),
	
	AVG_WORD_LENGTH("avgWordLenth"),
	
	SYLLABLES_RATIO("syllablesRatio"),
	
	TOKENS("tokens"), 
	
	TYPES("types"),
	
	PUNCTUATIONS("punctuations");
	
	
	
	String name;
	
	FileNamesForStatisticsEnum(String name) 
	{
		this.name = name;
	}
}
