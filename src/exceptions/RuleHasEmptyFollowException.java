package exceptions;

public class RuleHasEmptyFollowException extends Exception {
	private static final long serialVersionUID = 1L;
	public RuleHasEmptyFollowException(String simbolo){		
		super();		
		System.out.println("O C�LCULO DE FOLLOW DO S�MBOLO "+simbolo+" RESULTOU EM VAZIO NO CONJUNTO!");
		System.out.println("VERIFIQUE SUA GRAM�TICA!!!");
	}
}
