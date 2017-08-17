package Controller;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import Model.Token;
import exceptions.GramaticaIsNotSimplified;
import exceptions.GramaticaTem2FirstNaMesmaRegra;
import exceptions.RuleHasEmptyFollowException;
import exceptions.RuleHasNoFirstException;
import exceptions.RuleHasNoFollowException;
import semantic.AnalisadorSemantico;
import syntactic.AnalisadorSintatico;
import syntactic.RegraNaoTerminal;
import syntactic.RegraTerminal;
import syntactic.TokenToRegraGramatica;
import lexical.AnalisadorLexico;

public class Controlador {

	AnalisadorLexico lexico;
	AnalisadorSintatico sintatico;
	AnalisadorSemantico semantico;
	Gramatica gramatica;
	File dirEntrada;
	//listas
	List<Token> tokens;
	LinkedList<RegraNaoTerminal> regras;
	HashMap<String, RegraNaoTerminal> regrasHM;
	LinkedList<RegraTerminal> terminais;
	TokenToRegraGramatica comparador;
	
	public Controlador(String diretorioEntrada, String arquivoGramatica)
			throws RuleHasNoFirstException, RuleHasNoFollowException, 
			RuleHasEmptyFollowException, GramaticaTem2FirstNaMesmaRegra, GramaticaIsNotSimplified{	 
		this.dirEntrada = new File(diretorioEntrada);        
		//se conseguir pegar os arquivos, inicia a analise
		if(!dirEntrada.exists()){
            System.out.println("A pasta "+diretorioEntrada+" nao existe.");
            System.exit(0);
        }
		//analisar a gramatica antes das demais analises
		gramatica = new Gramatica(arquivoGramatica);		
		analiseGramatica();
		this.comparador = new TokenToRegraGramatica();
		this.regrasHM = gramatica.getRegrasHM();
		this.terminais = gramatica.getTerminais();
		this.regras = gramatica.getRegras();
		this.lexico = new AnalisadorLexico();
		this.sintatico = new AnalisadorSintatico(gramatica.getPrimeiraRegra(), regras,
				this.comparador);
		this.semantico = new AnalisadorSemantico();
	}
	
	public void analisar() throws RuleHasNoFirstException, RuleHasNoFollowException,
		RuleHasEmptyFollowException{		
		File listaDeArquivos[] = dirEntrada.listFiles(
            	new FilenameFilter() { 
                public boolean accept(File dir, String filename)
                { return filename.endsWith(".txt"); }
            } );		
		//para cada arquivo realizar as analises
		System.out.println("Diretorio de entrada: "+dirEntrada.getAbsolutePath());
		System.out.println("Arquivos carregados para teste:");
		for(int cont = 0; cont < listaDeArquivos.length; cont++){
        	System.out.println("\t"+listaDeArquivos[cont]);        
        }
		
		for(int cont = 0; cont < listaDeArquivos.length; cont++){
			//Se for diretorio ou um tipo de arquivo ignorado, passa pro proximo
            if (listaDeArquivos[cont].isDirectory()) {
                continue;
            }
            //analise lexica           
        	analiseLexica(listaDeArquivos[cont]);
        	this.tokens = this.lexico.getListTokens();
        	//analise sintatica
        	if(this.lexico.hasError()){
        		//se ocorreu algum erro lexico,limpa todas as listas de tokens
        		this.lexico.cleanLists();        		
			}else{
				//se n houve erro, comeca analise sintatica
        		analiseSintatica(listaDeArquivos[cont].getName());
        	}
        	//analise semantica
        	if(this.sintatico.getHasError()){
        		this.lexico.cleanLists();
        		this.sintatico.cleanLists();
        	}else{
        		analiseSemantica(listaDeArquivos[cont].getName());
        	}
        	
        	
        	this.lexico.cleanLists();
        	this.sintatico.cleanLists();
        	this.semantico.cleanLists();
        }
		System.out.println("ALL FILES HAVE BEEN COMPILED!");					
	}

	public void analiseGramatica() throws RuleHasNoFirstException, RuleHasNoFollowException,
		RuleHasEmptyFollowException, GramaticaTem2FirstNaMesmaRegra, GramaticaIsNotSimplified{
		gramatica.lerGramatica();
		gramatica.criarFirsts();
		for(int i = 0; i < 10; i++)
			gramatica.criarFollows();
		gramatica.isGramaticaAmbigua();
		//gramatica.printGramatica();
	}
	
	public void analiseLexica(File arquivo){
		lexico.executar(arquivo);
	}
	
	private void analiseSintatica(String fileName){
		sintatico.executar(this.tokens, fileName);				
	}
	
	private void analiseSemantica(String name) {		
		this.semantico.executar(name, sintatico.getTree(), lexico.getListTokens());
		this.semantico.cleanLists();
	}
}