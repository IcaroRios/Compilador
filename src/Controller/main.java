package Controller;

import java.io.File;

import exceptions.RuleHasEmptyFollowException;
import exceptions.RuleHasNoFirstException;
import exceptions.RuleHasNoFollowException;
import lexical.AnalisadorLexico;

public class main {

	public static void main(String[] args) throws RuleHasNoFirstException, RuleHasNoFollowException,
		RuleHasEmptyFollowException {
		
		String entrada = "entrada";
		String gramatica = "gramatica//grammar.txt";
		String listaEquivalencia = "gramatica//equivalenceList.txt";
		Controlador controlador = new Controlador(entrada, gramatica, listaEquivalencia);
		controlador.analisar();
	}

}
