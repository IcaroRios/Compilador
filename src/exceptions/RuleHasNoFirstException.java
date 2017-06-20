package exceptions;

public class RuleHasNoFirstException extends Exception {
	private static final long serialVersionUID = 1L;
	public RuleHasNoFirstException(String simbolo){
		super();
		System.out.println("NÃO FOI POSSÍVEL CALCULAR O FIRST DO SÍMBOLO "+simbolo);
		System.out.println("VERIFIQUE SUA GRAMÁTICA!");
	}
}
