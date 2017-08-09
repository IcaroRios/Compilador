package exceptions;

public class RuleHasNoFollowException extends Exception {
	private static final long serialVersionUID = 1L;
	public RuleHasNoFollowException(String simbolo){
		super();
		System.out.println("NÃO FOI POSSÍVEL CALCULAR O FOLLOW DO SÍMBOLO "+simbolo);
		System.out.println("VERIFIQUE SUA GRAMÁTICA!");
	}
}
