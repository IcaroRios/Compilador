package exceptions;

public class GramaticaTem2FirstNaMesmaRegra extends Exception {
	private static final long serialVersionUID = 1L;
	public GramaticaTem2FirstNaMesmaRegra(String name){
		super();
		System.out.println("O C�LCULO DE FOLLOW DO S�MBOLO "+name+" RESULTOU EM 2 FIRSTS!");
		System.out.println("VERIFIQUE SUA GRAM�TICA!!!");
	}
}
