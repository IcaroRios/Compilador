package Model;

import java.util.HashMap;
import java.util.LinkedList;

public class RegraNaoTerminal extends RegraGramatica{
	//todos os símbolos, terminais e n terminais que esse n terminal gera
	private LinkedList<LinkedList<RegraGramatica>> regra;
	//seu conjunto de first
	private HashMap<String, RegraTerminal> first;
	//seu conjunto de follow
	private HashMap<String, RegraTerminal> follow;
	
	public RegraNaoTerminal(String simbolo) {
		super();
		this.simbolo = simbolo;
		this.regra = new LinkedList<>();
		this.first = new HashMap<>();
		this.follow = new HashMap<>();
	} 
	
	public void setRegra(LinkedList<LinkedList<RegraGramatica>> regra){
		this.regra = regra;
	}
	
	public LinkedList<LinkedList<RegraGramatica>> getRegra(){
		return this.regra;
	}
	
	@Override
	public boolean isTerminal(){
		return false;
	}

	public HashMap<String, RegraTerminal> getFirst() {
		return this.first;
	}

	public HashMap<String, RegraTerminal> getFollow() {
		return this.follow;
	}

	@Override
	public String toString() {
		String producao = this.simbolo+" -> ";
		int tamanho = regra.size();
		//percorrendo uma regra
		for(int cont = 0; cont < tamanho; cont++){
			if(cont == tamanho-1){
				//para as produções de uma regra
				for(RegraGramatica a : regra.get(cont)){
					producao = producao + a.getSimbolo();
				}
				//producao= producao + regra.get(cont).getSimbolo();
			}
			else{
				//producao= producao + regra.get(cont).getSimbolo()+" | ";
				producao= producao + " | ";
			}			
		}
		//return producao;
		return ""+regra.size();
	}

}
