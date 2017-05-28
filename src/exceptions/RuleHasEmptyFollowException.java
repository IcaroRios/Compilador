package exceptions;

public class RuleHasEmptyFollowException extends Exception {
	public RuleHasEmptyFollowException(String simbolo){
		super();
		System.out.println("O CÁLCULO DE FOLLOW DO SÍMBOLO "+simbolo+" RESULTOU EM VAZIO NO CONJUNTO!");
		System.out.println("VERIFIQUE SUA GRAMÁTICA!!!");
	}
}
