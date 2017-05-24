package Controller;

import java.io.File;

import lexical.AnalisadorLexico;

public class main {

	public static void main(String[] args) {
		String entrada = "entrada";
		String gramatica = "gramatica//gramatica.txt";
		Controlador controlador = new Controlador(entrada, gramatica);
		controlador.analisar();
	}

}
