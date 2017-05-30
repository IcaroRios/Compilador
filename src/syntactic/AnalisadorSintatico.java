package syntactic;

import Controller.Gramatica;
import Model.CompareTokenToRegra;
import Model.RegraGramatica;
import Model.RegraNaoTerminal;
import Model.RegraTerminal;
import Model.Token;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class AnalisadorSintatico {

    //A PILHA VAI SER A PROPRIA PILHA DE EXECUCAO DO CODIGO
    private List<Token> tokens;
    private RegraNaoTerminal primeiraRegra;
    private LinkedList<RegraNaoTerminal> regras;
    private HashMap<String, RegraNaoTerminal> regrasHM;
    LinkedList<RegraTerminal> terminais;
    private Gramatica gramatica;

    public AnalisadorSintatico(RegraNaoTerminal primeiraRegra, LinkedList<RegraNaoTerminal> regras,
    		HashMap<String, RegraNaoTerminal> regrasHM, LinkedList<RegraTerminal> terminais) {               
    	this.primeiraRegra = primeiraRegra;
    	this.regras = regras;
        this.regrasHM = regrasHM;
        this.terminais = terminais;        
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
    public void Executar(List<Token> tokens){
    	for (Token token : tokens) {
			System.out.println(token);
		}	
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
