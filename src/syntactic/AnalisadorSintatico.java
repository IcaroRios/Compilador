package syntactic;

import Controller.Gramatica;
import Model.CompareToken;
import Model.RegraGramatica;
import Model.RegraNaoTerminal;
import Model.RegraTerminal;
import Model.Token;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class AnalisadorSintatico {

    //A PILHA VAI SER A PR�PRIA PILHA DE EXECU��O DO C�DIGO
    private List<Token> tokens;
    private List<RegraGramatica> primeiraRegra;
    private LinkedList<RegraNaoTerminal> regras;
    private Gramatica gramatica;

    public AnalisadorSintatico(List<Token> tokens) {
        this.tokens = tokens;
        this.gramatica = new Gramatica("gramatica//grammar.txt");
        gramatica.LerGramatica();
        this.regras = gramatica.getRegras();
        this.primeiraRegra = regras.get(0).getRegra().get(0);// pega a primeira regra
        //System.out.println(regras.get(0).getRegra().get(0));
    }

    public void executar(List<RegraGramatica> regra) {

        CompareToken comparador = new CompareToken();

        //System.out.println(regra);
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
                    
                    executar(gramatica.getRegrasHM().get(atual.getSimbolo()).getRegra().get(0));
                    
                

            }
        }

		//TODO
		/*
         se o que tem no topo da pilha � um terminal --- consome
		
         se o que tem no topo da pilha � um n terminal --- vai pra matriz e gera a produ��o dele
		
         segue at� a lista de tokens acabar
		
         lista de tokens acabou e a pilha est� vazia o c�digo est� correto sintaticamente,
         se n�o est� errado
         */
        //TODO
		/*TRATAMENTO DE ERROS
         pegue todos os follows da sua produ��o e de todos os seus antecessores                        
         se for um n�o terminal: ignore tokens at� chegar em um follow desses                        
		                       
         se for um terminal: assuma que o token foi inserido.                        
         e continua
         */
    }

    public List<RegraGramatica> getPrimeiraRegra() {
        return primeiraRegra;
    }

}
