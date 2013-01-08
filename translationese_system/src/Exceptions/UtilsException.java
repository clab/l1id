package Exceptions;

public class UtilsException extends RuntimeException 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UtilsException(String message)
	{
		super(message);
		System.out.println(message);
	}
}
