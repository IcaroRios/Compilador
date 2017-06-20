package syntactic;

import Model.Constants;
import Model.RegraGramatica;
import Model.Token;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.management.RuntimeErrorException;

public class AnalisadorSintatico {

	//A PILHA VAI SER A PROPRIA PILHA DE EXECUCAO DO CODIGO
	private List<Token> tokens;
	private RegraNaoTerminal primeiraRegra;
	private LinkedList<RegraNaoTerminal> regras;
	private HashMap<String, RegraNaoTerminal> regrasHM;
	private LinkedList<RegraTerminal> terminais;    
	private TokenToRegraGramatica comparador;
	RegraTerminal terminalVazio;
	int cont;
	private boolean hasError;	

	public AnalisadorSintatico(RegraNaoTerminal primeiraRegra, LinkedList<RegraNaoTerminal> regras,
			HashMap<String, RegraNaoTerminal> regrasHM, LinkedList<RegraTerminal> terminais,
			TokenToRegraGramatica comparador) {               
		this.primeiraRegra = primeiraRegra;
		this.regras = regras;
		this.regrasHM = regrasHM;
		this.terminais = terminais;     
		this.comparador = comparador;
		this.terminalVazio = new RegraTerminal(Constants.PRODUCAO_VAZIA);
	}

	/*
    se o que tem no topo da pilha e um terminal --- consome		
    se o que tem no topo da pilha e um n terminal --- vai pra matriz e gera a producao dele		
    segue ate a lista de tokens acabar		
    lista de tokens acabou e a pilha esta vazia o codigo esta correto sintaticamente,
    se nao estas errado

	TRATAMENTO DE ERROS
    pegue todos os follows da sua producao e de todos os seus antecessores                        
    se for um nao terminal: ignore tokens ate chegar em um follow desses                        		                      
    se for um terminal: assuma que o token foi inserido.                        
    e continua
	 */
	public void executar(List<Token> tokens, String fileName){
		this.tokens = tokens;
		this.cont = 0;
		this.hasError = false;
		LinkedList<RegraTerminal> allFollows = new LinkedList<>();
		allFollows.addAll(primeiraRegra.getSeguinte());
		this.analisar(primeiraRegra, allFollows);
		
		this.gerarSaida(fileName);
	}

	public void analisar(RegraNaoTerminal r, LinkedList<RegraTerminal> allFollows){
		RegraTerminal terminalAtual = comparador.tokenToTerminal(tokens.get(cont));
		int p = this.getPosicion(r, terminalAtual, allFollows);		
		if(p == -1){
			//achou como follow, entao pule essa regra
			return;
		}
		//System.out.println(terminalAtual);
		//System.out.println();
		//System.out.println("Regra nTerminal: "+r);
		//System.out.println("\tTomei a regra: "+p);
		//pegando a regra atual
		LinkedList<RegraGramatica> regraAtual = r.getRegra().get(p);
		for(int c = 0; c < regraAtual.size(); c++){
			if(cont == tokens.size()){
				//System.out.println("DEU MUITA MERDA\n A REGRA N TERMINOU E OS TOKENS JA.........");
				throw new RuntimeErrorException(null);
				//break;
			}
			terminalAtual = comparador.tokenToTerminal(tokens.get(cont));
			
			int resp = comparador.compare(terminalAtual, regraAtual.get(c));
			RegraTerminal b = comparador.tokenToTerminal(tokens.get(cont));
			//System.out.println(b);
			//System.out.println("\tTOKEN("+cont+"): "+tokens.get(cont));
			//System.out.println("\tPRODUCAO: "+regraAtual.get(c).getSimbolo());
			//SE DER CERTO, FAZ NADA, APENAS ANDA
			if(resp == 1){
				allFollows.remove(terminalAtual);
				//System.out.println(resp+" eh Terminal, DEU CERTO");				
			}
			//SE DEU ERRADO, FAZER TRATAMENTO DE ERRO
			else if(resp == 0){
				//System.out.println(resp+" DEU ERRADO PIVETE");
				this.hasError = true;
				throw new RuntimeErrorException(null);
			}
			//SE E UM NTERMINAL, VAI PRA REGRA NTERMINAL AGORA
			else if(resp == -1){
				//System.out.println(resp+" eh nTerminal");
				RegraNaoTerminal a = (RegraNaoTerminal) regraAtual.get(c);
				allFollows.addAll(a.getSeguinte());
				this.analisar(a, allFollows);
				allFollows.removeAll(a.getSeguinte());
				//System.out.println("....VOLTANDO POR BACKTRACKING.....");
				//System.out.println();
				//retirando a contagem extra do backtracking
				cont--;
				//recursivo(cont, a);
			}
			
			cont++;
		}

	}

	private int getPosicion(RegraNaoTerminal r, RegraTerminal terminalAtual,
			LinkedList<RegraTerminal> allFollows){		
		//System.out.println("PRIMEIRO "+r.getPrimeiro());
		//System.out.println(r.getPrimeiroHM());		
		//System.out.println("LISTA DE FOLLOWS: "+r.getSeguinte());
		//System.out.println("ALL FOLLOWS"+allFollows);
		int posicaoRegra;
		//se retornar null eh pq o simbolo n faz parte do conjunto
		if(r.getPrimeiroHM().get(terminalAtual.getSimbolo()) == null){
			//System.out.println("AHHHHHH "+r.getPrimeiroHM().get(terminalAtual.getSimbolo()));			
			//se o nTerminal possui o simbolo como follow						
			//if(r.getGeraVazio() && r.getSeguinte().contains(terminalAtual)){
			if(r.getSeguinte().contains(terminalAtual)){				
				//System.out.println("->ACHEI NO FOLLOW");
				posicaoRegra = -1;
				//posicaoRegra = -1;
				//allFollows.remove(terminalAtual);				
			}else{
				throw new RuntimeErrorException(null);
			}
			/*
			if(r.getSeguinte().contains(terminalAtual)){
				System.out.println("->ACHEI NO FOLLOW");
				posicaoRegra = -1;
			}else{
				System.out.println("->NAO ACHEI NEM NO FOLLOW");
				posicaoRegra = -2;
			}
			*/
			
		}else{
			//System.out.println("->ACHEI NO FIRST");
			posicaoRegra = r.getPrimeiroHM().get(terminalAtual.getSimbolo());
			//allFollows.addAll(r.getSeguinte());
		}
		return posicaoRegra;
	}
	
	public boolean getHasError(){
		return this.hasError;
	}
	
	private void gerarSaida(String arquivo) {
		try {
			File pasta = new File(Constants.pastaSaidaSin);
			pasta.mkdir();
			File n = new File(pasta.getName() + File.separator +
					"Out_"+arquivo.split("\\.")[0]+ Constants.extensaoArquivosSin);			
			BufferedWriter bw = new BufferedWriter(new FileWriter(n));			
			if(this.hasError){//se houve erros
				bw.newLine();
				bw.newLine();
				bw.write("------------------------------ERROS SINTATICOS IENTIFICADOS------------------------------");
				bw.newLine();
				bw.flush();
				
			}
			else{//se nao houve erros
				bw.newLine();
				bw.flush();
				bw.write("SUCESSO NA ANALISE SINTATICA DO ARQUIVO: "+arquivo);
				System.out.println("Analise Sintatica para o arquivo: " + arquivo + ": Sucesso.");

			}
			bw.close();			
		} catch (IOException ex) {
			System.out.println("Deu merda na escrita do arquivo.");
		}
	}
	
}
