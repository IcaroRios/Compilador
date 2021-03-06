package semantic;

import Model.Constants;

public class TokenId{
	private String nome;
	private String escopo;
	private int tipo;
	private int nLinha;
	private boolean isConstant;
	private boolean isArray;
	private boolean wasInitialized;
	private boolean wasUsed;
	
	public TokenId(String nome, String escopo, int tipo, boolean isConstant, int nLinha,
			boolean isArray, boolean wasInitialized){
		this.nome = nome;
		this.escopo = escopo;
		this.tipo = tipo;		
		this.isConstant = isConstant;
		this.nLinha = nLinha;
		this.isArray = isArray;
		this.wasInitialized = wasInitialized;
		this.wasUsed = false;
	}

	public TokenId(String nome, String escopo, int nLinha){
		this.nome = nome;
		this.escopo = escopo;
		this.nLinha = nLinha;
	}

	public int getnLinha(){
		return nLinha;
	}

	public boolean isArray(){
		return this.isArray;
	}
	
	public boolean wasUsed(){
		return this.wasUsed;
	}
	
	public void setWasUsed(){
		this.wasUsed = true;
	}
	
	public boolean wasInitialized(){
		return this.wasInitialized;
	}
	
	public void setnLinha(int nLinha) {
		this.nLinha = nLinha;
	}

	public String getNome(){
		return nome;
	}

	public String getEscopo(){
		return escopo;
	}

	public int getTipo(){
		return tipo;
	}

	public boolean isConstant(){
		return this.isConstant;
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
		return "->nome: "+ this.nome + "\t\t->tipo: " + this.tipo(tipo)+" \t\t->escopo: "+this.escopo
				+" \t\t->constante: "+this.isConstant+" \t\t->array: "+this.isArray;
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof TokenId){
			TokenId a = (TokenId)obj;
			if(a.getNome().equals(this.nome) && a.getEscopo().equals(this.escopo)){
				return true;
			}else{				
				return false;
			}
		}else {
			return false;
		}		
	}

	public boolean myEquals(TokenId a){
		if(a.getEscopo().equals(Constants.ESCOPO_GLOBAL_ID) ||
				this.escopo.equals(Constants.ESCOPO_GLOBAL_ID)){
			if(a.getNome().equals(this.nome)){
				return true;
			}else{
				return false;
			}
		}else{
			if(a.getNome().equals(this.nome) && a.getEscopo().equals(this.escopo)){
				return true;
			}else{
				return false;
			}
		}		
	}
}
