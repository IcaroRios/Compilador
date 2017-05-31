/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class CompareTokenToRegra {
	
	LinkedList<LinkedList<String>> lista;
    public CompareTokenToRegra(String arquivoEquivalencia){
    	super();
    	this.lista = new LinkedList<>();
    	carregarLista(arquivoEquivalencia);    	
    }
    
    private void carregarLista(String arquivoEquivalencia){
    	BufferedReader br = null;
		FileReader fr = null;
    	try {
    		fr = new FileReader(arquivoEquivalencia);
			br = new BufferedReader(fr);
			String linha;			
			while ((linha = br.readLine()) != null) {
				String entrada[] = linha.split(" ");
				LinkedList<String> l = new LinkedList<>();
				for (int cont = 0; cont < entrada.length; cont++) {
					l.add(entrada[cont]);
				}
				this.lista.add(l);
			}
		} catch (IOException e){//se der merda na leitura
			e.printStackTrace();
		} finally {//vamos fechar?
			try {
				if (br != null)	br.close();
				if (fr != null) fr.close();
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}		
	}



	/*Retorna 1 se o token for equivalente a regra da gramatica esperada
     * Retorna 0 se o token nao for equivalente a regra da gramatica
     * Retorna -1 se a regra da gramatica for uma regra nao terminal,
     * nao podem ser feitas comparacoes
     * */
    public int compare(Token token, RegraGramatica regra){
    	//regra eh um terminal
        if(regra.isTerminal()){        	
        	RegraTerminal r = (RegraTerminal) regra;
        	//TODO PEGAR A LISTA DE EQUIVALENCIA ENTRE SIMBOLOS E TOKENS        	        
        	return getTerminalType(token, r);
        	
        }
        //regra eh um nTerminal
        else{
        	return -1;	
        }    	       
    }

	private int getTerminalType(Token t, RegraTerminal r) {		
		LinkedList<String> aux = this.lista.get(t.getId());		
		if(aux.contains(r.getSimbolo())){
			return 1;
		}
		else{
			return 0;
		}
	}

}
