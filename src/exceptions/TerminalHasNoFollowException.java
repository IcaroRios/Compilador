package exceptions;

public class TerminalHasNoFollowException extends Exception {
	private static final long serialVersionUID = 1L;
	public TerminalHasNoFollowException(){
		super();		
		System.out.println("VERIFIQUE SUA GRAMÁTICA!!!");
	}
}
