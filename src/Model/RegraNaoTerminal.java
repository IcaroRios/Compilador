package Model;

import java.util.HashMap;
import java.util.LinkedList;

public class RegraNaoTerminal extends RegraGramatica{
	//todos os símbolos, terminais e n terminais que esse n terminal gera
	private LinkedList<RegraGramatica> regra;
	//seu conjunto de first
	private HashMap<String, RegraTerminal> first;
	//seu conjunto de follow
	private HashMap<String, RegraTerminal> follow;
	
	public RegraNaoTerminal(String simbolo, LinkedList<RegraGramatica> regra) {
		super();
		this.simbolo = simbolo;
		this.regra = regra;
		this.first = new HashMap<>();
		this.follow = new HashMap<>();
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
		for(int cont = 0; cont < tamanho; cont++){
			if(cont == tamanho-1){
				producao= producao + regra.get(cont).getSimbolo();
			}
			else{
				producao= producao + regra.get(cont).getSimbolo()+" | ";
			}			
		}
		return producao;
	}

	
	
}
