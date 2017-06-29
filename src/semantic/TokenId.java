package semantic;

import Model.Constants;

public class TokenId{
	private String nome;
	private String escopo;
	private int tipo;
	private int nLinha;
	private boolean isConstant;
	
	public TokenId(String nome, String escopo, int tipo, boolean isConstant, int nLinha){
		this.nome = nome;
		this.escopo = escopo;
		this.tipo = tipo;		
		this.isConstant = isConstant;
		this.nLinha = nLinha;
	}
		
	public TokenId(String nome, String escopo, int nLinha){
		this.nome = nome;
		this.escopo = escopo;
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
		return "->nome: "+ this.nome + "\t\t->tipo: " + this.tipo(tipo)+" \t\t->escopo: "+this.escopo;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof TokenId){
			TokenId a = (TokenId)obj;
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
		}else {
			return false;
		}		
	}
}
