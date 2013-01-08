package Exceptions;

public class ToolsException extends Exception 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ToolsException(String message)
	{
		super(message);
		System.out.println(message);
	}
}
