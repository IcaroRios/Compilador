package exceptions;

public class RuleHasEmptyFollowException extends Exception {
	public RuleHasEmptyFollowException(String simbolo){
		super();
		System.out.println("O C�LCULO DE FOLLOW DO S�MBOLO "+simbolo+" RESULTOU EM VAZIO NO CONJUNTO!");
		System.out.println("VERIFIQUE SUA GRAM�TICA!!!");
	}
}
