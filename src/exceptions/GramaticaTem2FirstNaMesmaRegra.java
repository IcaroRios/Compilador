package exceptions;

public class GramaticaTem2FirstNaMesmaRegra extends Exception {
	private static final long serialVersionUID = 1L;
	public GramaticaTem2FirstNaMesmaRegra(String name){
		super();
		System.out.println("O CÁLCULO DE FOLLOW DO SÍMBOLO "+name+" RESULTOU EM 2 FIRSTS!");
		System.out.println("VERIFIQUE SUA GRAMÁTICA!!!");
	}
}
