package exceptions;

public class GramaticaIsNotSimplified extends Exception {
	private static final long serialVersionUID = 1L;
	public GramaticaIsNotSimplified(String string, String string2){
		super();
		System.out.println("A REGRA "+string+" POSSUI UMA PRODU��O QUE GERA APENAS 1 NTERMINAL,"
				+ " PELA PRODU��O "+string2);
		System.out.println("POR ALGUMA RAZ�O DESCONHECIDA ESTE C�DIGO S� FUNCIONA EM GRAM�TICAS"
				+ " QUE ISTO N�O OCORRE!\nVERIFIQUE SUA GRAM�TICA!!!");
	}
}
