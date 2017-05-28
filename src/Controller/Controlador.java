package Controller;

import java.io.File;

import exceptions.RuleHasEmptyFollowException;
import exceptions.RuleHasNoFirstException;
import exceptions.RuleHasNoFollowException;
import syntactic.AnalisadorSintatico;
import lexical.AnalisadorLexico;

public class Controlador {

	AnalisadorLexico lexico;
	AnalisadorSintatico sintatico;
	Gramatica gramatica;
	File dir;
	
	public Controlador(String diretorioEntrada, String arquivoGramatica){	 
		this.dir = new File(diretorioEntrada);        
		//se conseguir pegar os arquivos, inicia a analise
		if(!dir.exists()) {
            System.out.println("A pasta "+diretorioEntrada+" n�o existe.");
            System.exit(0);
        }
		gramatica = new Gramatica(arquivoGramatica);
		lexico = new AnalisadorLexico();
		sintatico = new AnalisadorSintatico();
	}
	
	public void analisar() throws RuleHasNoFirstException, RuleHasNoFollowException,
		RuleHasEmptyFollowException{
		analiseGramatica();		
		//analiseLexica();
		//analiseSintatica();
	}
	
	public void analiseGramatica() throws RuleHasNoFirstException, RuleHasNoFollowException,
		RuleHasEmptyFollowException{
		gramatica.LerGramatica();
		gramatica.CriarFirsts();
		gramatica.CriarFollows();
		gramatica.printGramatica();
	}
	
	public void analiseLexica(){
		lexico.Executar(dir);
		System.exit(0);
	}
	
	public void analiseSintatica(){
		
	}
	
}