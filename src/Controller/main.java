package Controller;

import java.io.File;

import exceptions.RuleHasNoFirstException;
import exceptions.RuleHasNoFollowException;
import lexical.AnalisadorLexico;

public class main {

	public static void main(String[] args) throws RuleHasNoFirstException, RuleHasNoFollowException {
		String entrada = "entrada";
		String gramatica = "gramatica//grammar.txt";
		Controlador controlador = new Controlador(entrada, gramatica);
		controlador.analisar();
	}

}
