package Model;

import java.util.HashMap;
import java.util.LinkedList;

public class RegraNaoTerminal extends RegraGramatica{
	//todos os símbolos, terminais e n terminais que esse n terminal gera
	private LinkedList<LinkedList<RegraGramatica>> regra;
	//seu conjunto de first
	private HashMap<String, LinkedList<RegraTerminal>> first;
	private LinkedList<RegraTerminal> primeiro;
	//seu conjunto de follow
	private HashMap<String, LinkedList<RegraTerminal>> follow;
	private LinkedList<RegraTerminal> seguinte;
	//se a regra gera produção vazia
	private boolean geraVazio;
		
	public RegraNaoTerminal(String simbolo) {
		super();
		this.simbolo = simbolo;
		this.regra = new LinkedList<>();
		this.first = new HashMap<>();
		this.primeiro = new LinkedList<>();
		this.follow = new HashMap<>();
		this.seguinte = new LinkedList<>();
		this.geraVazio = false;
	} 
	
	public void addRegra(LinkedList<LinkedList<RegraGramatica>> lista){
		this.regra.addAll(lista);
	}
	
	public LinkedList<LinkedList<RegraGramatica>> getRegra(){
		return this.regra;
	}
	
	public void setFirsts(HashMap<String, LinkedList<RegraTerminal>> f){
		this.first = f;
	}
	
	public void addPrimeiro(RegraTerminal f){
		this.primeiro.add(f);
	}
	
	public LinkedList<RegraTerminal> getPrimeiro(){
		return this.primeiro;
	}
	
	public HashMap<String, LinkedList<RegraTerminal>> getFirsts(){
		return this.first;
	}
	
	public void setFollows(HashMap<String, LinkedList<RegraTerminal>> f){
		this.follow = f;
	}
	
	public HashMap<String, LinkedList<RegraTerminal>> getFollows(){
		return this.follow;
	}
	
	public void addSeguinte(RegraTerminal f){
		this.seguinte.add(f);
	}
	
	public LinkedList<RegraTerminal> getSeguinte(){
		return this.seguinte;
	}
	
	@Override
	public boolean isTerminal(){
		return false;
	}

	public HashMap<String, LinkedList<RegraTerminal>> getFirst() {
		return this.first;
	}

	public HashMap<String, LinkedList<RegraTerminal>> getFollow() {
		return this.follow;
	}
	
	public void setGeraVazio(){
		this.geraVazio = true;
	}
	
	public boolean getGeraVazio(){
		return this.geraVazio;
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
