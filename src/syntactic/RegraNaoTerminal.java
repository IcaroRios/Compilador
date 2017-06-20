package syntactic;

import java.util.HashMap;
import java.util.LinkedList;

import Model.RegraGramatica;

public class RegraNaoTerminal extends RegraGramatica{
	//todos os símbolos, terminais e n terminais que esse n terminal gera
	private LinkedList<LinkedList<RegraGramatica>> regra;
	//seu conjunto de first
	private HashMap<String, Integer> primeiroHM;
	private LinkedList<RegraTerminal> primeiro;
	//seu conjunto de follow
	private HashMap<String, LinkedList<RegraTerminal>> follow;
	private LinkedList<RegraTerminal> seguinte;
	//se a regra gera produçao vazia
	private boolean geraVazio;
	private boolean firstEstaPronto;
		
	public RegraNaoTerminal(String simbolo) {
		super();
		this.simbolo = simbolo;
		this.regra = new LinkedList<>();
		this.primeiroHM = new HashMap<>();
		this.primeiro = new LinkedList<>();
		this.follow = new HashMap<>();
		this.seguinte = new LinkedList<>();
		this.geraVazio = false;
		this.firstEstaPronto = false;
	} 
	
	public void addRegra(LinkedList<LinkedList<RegraGramatica>> lista){
		this.regra.addAll(lista);
	}
	
	public LinkedList<LinkedList<RegraGramatica>> getRegra(){
		return this.regra;
	}
	
	public void addPrimeiroHM(String key, int value){
		if(!this.primeiroHM.containsKey(key)){
			this.primeiroHM.put(key, value);
		}			
	}
	
	public HashMap<String, Integer> getPrimeiroHM() {
		return this.primeiroHM;
	}
	
	public void addPrimeiro(RegraTerminal f){
		if(!this.primeiro.contains(f)){
			this.primeiro.add(f);
		}				
	}
	
	public LinkedList<RegraTerminal> getPrimeiro(){
		return this.primeiro;
	}
	
	
	public void setFollows(HashMap<String, LinkedList<RegraTerminal>> f){
		this.follow = f;
	}
	
	public HashMap<String, LinkedList<RegraTerminal>> getFollows(){
		return this.follow;
	}
	
	public void addSeguinte(RegraTerminal f){
		if(!this.seguinte.contains(f)){
			this.seguinte.add(f);
		}
	}
	
	public LinkedList<RegraTerminal> getSeguinte(){
		return this.seguinte;
	}
	
	@Override
	public boolean isTerminal(){
		return false;
	}	

	public HashMap<String, LinkedList<RegraTerminal>> getFollow() {
		return this.follow;
	}
	
	public void setGeraVazio(){
		this.geraVazio = true;
	}
	
	@Override
	public boolean getGeraVazio(){
		return this.geraVazio;
	}
	
	public void setfirstEstaPronto(){
		this.firstEstaPronto = true;
	}
	
	@Override
	public boolean getFirstEstaPronto(){
		return this.firstEstaPronto;
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
