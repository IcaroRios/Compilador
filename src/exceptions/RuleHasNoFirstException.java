package exceptions;

public class RuleHasNoFirstException extends Exception {
	public RuleHasNoFirstException(String simbolo){
		super();
		System.out.println("N�O FOI POSS�VEL CALCULAR O FIRST DO S�MBOLO "+simbolo);
		System.out.println("VERIFIQUE SUA GRAM�TICA!");
	}
}
