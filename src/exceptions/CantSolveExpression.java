package exceptions;

import java.util.LinkedList;

public class CantSolveExpression extends Exception {
	private static final long serialVersionUID = 1L;
	private int nLinha;
	LinkedList<String> exp;
	
	public CantSolveExpression(int nLinha, LinkedList<String> a){
		this.nLinha = nLinha;
		this.exp = a;
	}
	
	public int getNLinha(){
		return this.nLinha;
	}
	
	public LinkedList<String> getExp(){
		return this.exp;
	}
}
