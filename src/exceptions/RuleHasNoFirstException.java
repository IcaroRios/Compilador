package exceptions;

public class RuleHasNoFirstException extends Exception {
	private static final long serialVersionUID = 1L;
	public RuleHasNoFirstException(String simbolo){
		super();
		System.out.println("N�O FOI POSS�VEL CALCULAR O FIRST DO S�MBOLO "+simbolo);
		System.out.println("VERIFIQUE SUA GRAM�TICA!");
	}
}
