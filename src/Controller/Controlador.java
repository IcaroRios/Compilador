package Controller;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import Model.TokenToRegraGramatica;
import Model.RegraNaoTerminal;
import Model.RegraTerminal;
import Model.Token;
import exceptions.RuleHasEmptyFollowException;
import exceptions.RuleHasNoFirstException;
import exceptions.RuleHasNoFollowException;
import syntactic.AnalisadorSintatico;
import lexical.AnalisadorLexico;

public class Controlador {

	AnalisadorLexico lexico;
	AnalisadorSintatico sintatico;
	Gramatica gramatica;
	File dirEntrada;
	//listas
	List<Token> tokens;
	LinkedList<RegraNaoTerminal> regras;
	HashMap<String, RegraNaoTerminal> regrasHM;
	LinkedList<RegraTerminal> terminais;
	TokenToRegraGramatica comparador;
	
	public Controlador(String diretorioEntrada, String arquivoGramatica)
			throws RuleHasNoFirstException, RuleHasNoFollowException, RuleHasEmptyFollowException{	 
		this.dirEntrada = new File(diretorioEntrada);        
		//se conseguir pegar os arquivos, inicia a analise
		if(!dirEntrada.exists()) {
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
				regrasHM, terminais, this.comparador);
		
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
        	analiseLexica(listaDeArquivos[cont]);
        	this.tokens = this.lexico.getListTokens();
        	//se ocorreu algum erro lexico
        	if(this.lexico.hasError()){
        		//limpa todas as listas de tokens
        		this.lexico.cleanLists();        		
			}
        	//se n houve erro, comeca analise sintatica
        	else{
        		analiseSintatica();
        	}
        	
        }
		System.out.println("ALL FILES HAVE BEEN COMPILED!");					
	}	


	public void analiseGramatica() throws RuleHasNoFirstException, RuleHasNoFollowException,
		RuleHasEmptyFollowException{
		gramatica.lerGramatica();
		gramatica.criarFirsts();
		gramatica.criarFollows();
		//gramatica.printGramatica();
	}
	
	public void analiseLexica(File arquivo){
		lexico.executar(arquivo);
		//System.out.println("AQUI");
		//System.exit(0);
	}
	
	private void analiseSintatica() {
		sintatico.executar(this.tokens);		
		
	}
	
}