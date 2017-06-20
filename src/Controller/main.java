package Controller;

import exceptions.GramaticaTem2FirstNaMesmaRegra;
import exceptions.RuleHasEmptyFollowException;
import exceptions.RuleHasNoFirstException;
import exceptions.RuleHasNoFollowException;

public class main {

	public static void main(String[] args) throws RuleHasNoFirstException, RuleHasNoFollowException,
		RuleHasEmptyFollowException, GramaticaTem2FirstNaMesmaRegra {
		
		String entrada = "entrada";
		String gramatica = "gramatica//grammar.txt";
		Controlador controlador = new Controlador(entrada, gramatica);
		controlador.analisar();
	}

}
