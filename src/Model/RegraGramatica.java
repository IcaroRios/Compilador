package Model;

import java.util.LinkedList;

public abstract class RegraGramatica {
	
	protected String simbolo;

	public abstract boolean isTerminal();
	
	public abstract boolean getFirstEstaPronto();
	
	public void setSimbolo(String simbolo){
		this.simbolo = simbolo;
	}
	public String getSimbolo(){
		return simbolo;
	}
		
	public boolean equals(Object other){
		if(other instanceof RegraGramatica){
			RegraGramatica a = (RegraGramatica) other;
			return a.getSimbolo().equals( this.simbolo);
		}
		return false;
	}
	
	@Override
	public abstract String toString();
}
