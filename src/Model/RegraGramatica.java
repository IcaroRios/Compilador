package Model;

import java.util.LinkedList;

public abstract class RegraGramatica {
	
	protected String simbolo;

	public abstract boolean isTerminal();
	
	
	public void setSimbolo(String simbolo){
		this.simbolo = simbolo;
	}
	public String getSimbolo(){
		return simbolo;
	}
		
	public boolean equals(RegraGramatica other){
		if(other instanceof RegraGramatica){
			return ( other.getSimbolo().equals( this.getSimbolo()) );
		}
		return false;
	}
	
	@Override
	public abstract String toString();
}
