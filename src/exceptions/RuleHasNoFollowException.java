package exceptions;

public class RuleHasNoFollowException extends Exception {
	public RuleHasNoFollowException(String simbolo){
		super();
		System.out.println("NÃO FOI POSSÍVEL CALCULAR O FOLLOW DO SÍMBOLO "+simbolo);
		System.out.println("VERIFIQUE SUA GRAMÁTICA!");
	}
}
