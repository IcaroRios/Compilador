package exceptions;

public class RuleHasNoFollowException extends Exception {
	private static final long serialVersionUID = 1L;
	public RuleHasNoFollowException(String simbolo){
		super();
		System.out.println("N�O FOI POSS�VEL CALCULAR O FOLLOW DO S�MBOLO "+simbolo);
		System.out.println("VERIFIQUE SUA GRAM�TICA!");
	}
}
