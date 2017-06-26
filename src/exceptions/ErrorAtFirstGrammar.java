package exceptions;

import syntactic.RegraTerminal;

public class ErrorAtFirstGrammar extends Exception {
	private static final long serialVersionUID = 1L;
	private RegraTerminal regra;
	
	public ErrorAtFirstGrammar(RegraTerminal aux){
		super();
		this.regra = aux;
	}
	
	public RegraTerminal getRegraTerminal(){
		return this.regra;
	}
}
