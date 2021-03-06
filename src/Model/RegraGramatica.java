package Model;

public abstract class RegraGramatica {
	
	protected String simbolo;

	public abstract boolean isTerminal();
	
	public abstract boolean getFirstEstaPronto();
	
	public abstract boolean getGeraVazio();
	
	public void setSimbolo(String simbolo){
		this.simbolo = simbolo;
	}
	public String getSimbolo(){
		return simbolo;
	}
	
	@Override
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
