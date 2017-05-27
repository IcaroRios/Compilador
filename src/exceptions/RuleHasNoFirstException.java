package exceptions;

public class RuleHasNoFirstException extends Exception {
	public RuleHasNoFirstException(String simbolo){
		super();
		System.out.println("NÃO FOI POSSÍVEL CALCULAR O FIRST DO SÍMBOLO "+simbolo);
		System.out.println("VERIFIQUE SUA GRAMÁTICA!");
	}
}
