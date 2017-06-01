package syntactic;

import Model.Constants;
import Model.TokenToRegraGramatica;
import Model.RegraGramatica;
import Model.RegraNaoTerminal;
import Model.RegraTerminal;
import Model.Token;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import jdk.nashorn.internal.ir.Terminal;

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
	public void executar(List<Token> tokens){
		this.tokens = tokens;
		analisar(primeiraRegra);
		this.cont = 0;
	}

	public void analisar(RegraNaoTerminal r){		
		RegraTerminal terminalAtual = comparador.tokenToTerminal(tokens.get(cont));
		int p = getPosicion(r, terminalAtual);
		//System.out.println(terminalAtual);
		System.out.println();
		System.out.println("Regra nTerminal: "+r);
		System.out.println("\tTomei a regra: "+p);
		//pegando a regra atual
		LinkedList<RegraGramatica> regraAtual = r.getRegra().get(p);
		for(int c = 0; c < regraAtual.size(); c++){
			terminalAtual = comparador.tokenToTerminal(tokens.get(cont));
			if(cont == tokens.size()){
				System.out.println("DEU MUITA MERDA\n A REGRA N TERMINOU E OS TOKENS JA.........");
				break;
			}
			int resp = comparador.compare(terminalAtual, regraAtual.get(c));
			RegraTerminal b = comparador.tokenToTerminal(tokens.get(cont));
			System.out.println(b);
			//SE DER CERTO, FAZ NADA, APENAS ANDA
			if(resp == 1){
				System.out.println(resp+" eh Terminal, DEU CERTO");
				System.out.println("\tTOKEN("+cont+"): "+tokens.get(cont));
				System.out.println("\tPRODUCAO: "+regraAtual.get(c).getSimbolo());						
			}
			//SE DEU ERRADO, FAZER TRATAMENTO DE ERRO
			else if(resp == 0){
				System.out.println(resp+" DEU ERRADO PIVETE");
				System.out.println("\tTOKEN("+cont+"): "+tokens.get(cont));
				System.out.println("\tPRODUCAO: "+regraAtual.get(c).getSimbolo());
			}
			//SE E UM NTERMINAL, VAI PRA REGRA NTERMINAL AGORA
			else if(resp == -1){
				System.out.println(resp+" eh nTerminal");
				System.out.println("\tTOKEN("+cont+"): "+tokens.get(cont));
				System.out.println("\tPRODUCAO: "+regraAtual.get(c).getSimbolo());
				RegraNaoTerminal a = (RegraNaoTerminal) regraAtual.get(c);
				this.analisar(a);
				System.out.println("....VOLTANDO POR BACKTRACKING.....");
				System.out.println();
				//retirando a contagem extra do backtracking
				cont--;
				//recursivo(cont, a);
			}
			cont++;
		}		

	}	

	private int getPosicion(RegraNaoTerminal r, RegraTerminal terminalAtual) {
		int posicaoRegra;
		//vamos pressumir que a regra 
		if(r.getPrimeiroHM().get(terminalAtual.getSimbolo()) == null){			
			if(r.getPrimeiro().contains(terminalVazio)){
				posicaoRegra = r.getPrimeiroHM().get(terminalVazio.getSimbolo());
			}
			else{
				posicaoRegra = -1;
				System.out.println("DEU MUITA MERDA..................");
			}
		}else{
			posicaoRegra = r.getPrimeiroHM().get(terminalAtual.getSimbolo());		
		}
		return posicaoRegra;
	}

	/*
    public void executar(List<Token> tokens) {
    CompareToken comparador = new CompareToken();
    for (RegraGramatica atual : regra) {

        if (atual.isTerminal()) {
            //System.out.println( tokens.get(0).getLexema()+ " aaa " + atual.toString());
            //System.out.println( atual.toString().substring(1, atual.toString().length()-1));
            if (comparador.compare(tokens.get(0), atual.getSimbolo()) == 1) {
                if(!tokens.isEmpty())
                    tokens.remove(0);
            } else {
                System.out.println("Erro sintático na linha: " + tokens.get(0).getnLinha());
            }
        } else {                
                System.out.println("expression encontrada "+gramatica.getRegrasHM().get(atual.getSimbolo()));
                System.out.println("enviando para a regra dele.");
                System.out.println(gramatica.getRegrasHM().get(atual.getSimbolo()).getRegra().get(0));                    
                //executar(gramatica.getRegrasHM().get(atual.getSimbolo()).getRegra().get(0));                                
        }
	}
	 */

}
