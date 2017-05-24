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
	
	public void addRegra(LinkedList<LinkedList<RegraGramatica>> lista){
		this.regra.addAll(lista);
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
			LinkedList<RegraGramatica> r = regra.get(cont);
			//para as produções de uma regra
			for(int cont1 = 0; cont1 < r.size(); cont1++){
				if(cont != 0 && cont1 == 0){
					producao = producao +" | "+ r.get(cont1).getSimbolo();
				}
				else{
					producao = producao +" "+ r.get(cont1).getSimbolo();
				}				
			}			
		}
		return producao;
		//return ""+regra.size();
	}

}
