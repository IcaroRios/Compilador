package exceptions;

public class GramaticaIsNotSimplified extends Exception {
	private static final long serialVersionUID = 1L;
	public GramaticaIsNotSimplified(String string, String string2){
		super();
		System.out.println("A REGRA "+string+" POSSUI UMA PRODUÇÃO QUE GERA APENAS 1 NTERMINAL,"
				+ " PELA PRODUÇÃO "+string2);
		System.out.println("POR ALGUMA RAZÃO DESCONHECIDA ESTE CÓDIGO SÓ FUNCIONA EM GRAMÁTICAS"
				+ " QUE ISTO NÃO OCORRE!\nVERIFIQUE SUA GRAMÁTICA!!!");
	}
}
