package Model;

import java.util.LinkedList;

import exceptions.TerminalHasNoFollowException;

public class RegraTerminal extends RegraGramatica{

	public RegraTerminal(String simbolo){
		super();
		this.simbolo = simbolo;
	}
	
	@Override
	public boolean isTerminal() {
		return true;
	}

	public RegraTerminal getFirst() {
		return this;
	}

	public LinkedList<RegraTerminal> getFollow() throws TerminalHasNoFollowException{
		throw new TerminalHasNoFollowException();
	}

	@Override
	public String toString(){
		return this.simbolo;
	}

	@Override
	public boolean getFirstEstaPronto() {		
		return true;
	}
	
	@Override
	public boolean getGeraVazio(){
		return false;
	}
}
