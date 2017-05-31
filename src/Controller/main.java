package Controller;

import exceptions.RuleHasEmptyFollowException;
import exceptions.RuleHasNoFirstException;
import exceptions.RuleHasNoFollowException;

public class main {

	public static void main(String[] args) throws RuleHasNoFirstException, RuleHasNoFollowException,
		RuleHasEmptyFollowException {
		
		String entrada = "entrada";
		String gramatica = "gramatica//grammar.txt";
		Controlador controlador = new Controlador(entrada, gramatica);
		controlador.analisar();
	}

}
