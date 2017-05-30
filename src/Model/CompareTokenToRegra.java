/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.Comparator;

public class CompareTokenToRegra {

    public CompareTokenToRegra() {
    	super();
    }

    /*Retorna 1 se o token for equivalente a regra da gramatica esperada
     * Retorna 0 se o token nao for equivalente a regra da gramatica
     * Retorna -1 se a regra da gramatica for uma regra nao terminal,
     * nao podem ser feitas comparacoes
     * */
    public int compare(Token token, RegraGramatica regra) {
    	//regra eh um terminal
        if(regra.isTerminal()){        	
        	RegraTerminal r = (RegraTerminal) regra;
        	//TODO PEGAR A LISTA DE EQUIVALENCIA ENTRE SIMBOLOS E TOKENS
        	getTerminalType(r.getSimbolo());
        	return 0;
        }
        //regra eh um nTerminal
        else{
        	return -1;	
        }    	       
    }

	private void getTerminalType(String simbolo) {		
		
	}

}
