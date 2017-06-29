package semantic;

import java.util.LinkedList;

import Model.Constants;

public class TokenFunction{
	private String nome;	
	private LinkedList<TokenId> parametros;
	private int nLinha;
	private boolean hasReturn;
	private int returnType;

	public TokenFunction(String nome,LinkedList<TokenId> parametros, int nLinha,
			boolean hasReturn, int returnType){
		this.nome = nome;
		this.parametros= parametros;
		this.nLinha = nLinha;
		this.hasReturn = hasReturn;
		this.returnType = returnType;
	}

	public TokenFunction(String lexema, int nLinha){
		this.nome = lexema;
		this.parametros = new LinkedList<>();
		this.nLinha = nLinha;
	}

	public int getnLinha(){
		return nLinha;
	}

	public void setnLinha(int nLinha) {
		this.nLinha = nLinha;
	}

	public String getNome(){
		return nome;
	}

	public LinkedList<TokenId> getTipo(){
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
		if(i == 0){
			a = "integer";
		}else if(i == 1){
			a = "real";
		}else if(i == 2){
			a = "boolean";
		}else if(i == 3){
			a = "string";
		}else if(i == 4){
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
		a = a +" \n\t->linha: "+this.nLinha;
		a = a +" \n\t->return:"+this.hasReturn+" - "+this.tipo(this.returnType);
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
