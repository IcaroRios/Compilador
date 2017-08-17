package semantic;

import java.util.LinkedList;

import Model.Constants;

public class TokenFunction{
	private String nome;	
	private LinkedList<TokenId> parametros;
	private int nLinha;
	private boolean hasReturn;
	private int returnType;
	private int tokenPosition;

	public TokenFunction(String nome,LinkedList<TokenId> parametros, int nLinha,
			boolean hasReturn, int returnType, int tokenPosition){
		this.nome = nome;
		this.parametros= parametros;
		this.nLinha = nLinha;
		this.hasReturn = hasReturn;
		this.returnType = returnType;
		this.tokenPosition = tokenPosition;
	}

	public TokenFunction(String lexema, int nLinha){
		this.nome = lexema;
		this.parametros = new LinkedList<>();
		this.nLinha = nLinha;
	}

	public int getNLinha(){
		return nLinha;
	}
	
	public int getTokenPosition(){
		return this.tokenPosition;
	}
	public void setnLinha(int nLinha) {
		this.nLinha = nLinha;
	}

	public String getNome(){
		return nome;
	}

	public LinkedList<TokenId> getParameters(){
		return parametros;
	}
	
	public boolean hasReturn(){
		return this.hasReturn;
	}
	
	public int getReturnType(){
		return this.returnType;
	}
	
	private String tipo(int i){		
		String a  = "";
		if(i == Constants.EXP_NUM_INT){
			a = "integer";
		}else if(i == Constants.EXP_NUM_REAL){
			a = "real";
		}else if(i == Constants.EXP_BOOLEAN){
			a = "boolean";
		}else if(i == Constants.EXP_STRING){
			a = "string";
		}else if(i == Constants.EXP_CHAR){
			a = "char";
		}
		return a;
	}
	
	@Override
	public String toString(){
		String a = "->Funcao: "+ this.nome + "\n\t->parametros:\n\t\t";
		for(TokenId tokenId : parametros){
			a = a + tokenId+"\n\t\t";
		}
		a = a +"->linha: "+this.nLinha;
		a = a +" \n\t\t->return:"+this.hasReturn+" - "+this.tipo(this.returnType);
		return a;
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof TokenFunction){
			TokenFunction a = (TokenFunction)obj;
			return a.getNome().equals(this.nome);						
		}else{
			return false;
		}		
	}

}
