package exceptions;

public class RuleHasNoFollowException extends Exception {
	public RuleHasNoFollowException(String simbolo){
		super();
		System.out.println("N�O FOI POSS�VEL CALCULAR O FOLLOW DO S�MBOLO "+simbolo);
		System.out.println("VERIFIQUE SUA GRAM�TICA!");
	}
}
