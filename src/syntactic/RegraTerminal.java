package syntactic;

import java.util.LinkedList;

import Model.Constants;
import Model.RegraGramatica;
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
		//return this.simbolo;
		String a = "";
		if(this.simbolo.equals("LogicOpSimple")){			
			a = "'!'";
		}else if(this.simbolo.equals("AritmeticOpSum")){			
			a = "'+' '-'";
		}else if(this.simbolo.equals("AritmeticOpMult")){			
			a = "'*' '/'";
		}else if(this.simbolo.equals("RelationalOpEqual")){			
			a = "'!=' '=='";
		}else if(this.simbolo.equals("RelationalOpComp")){			
			a = "'<=' '>=' '<' '>'";
		}else if(this.simbolo.equals(Constants.PRODUCAO_VAZIA)){
			a = "'PRODUCAO VAZIA'";
		}else if(this.simbolo.equals(Constants.PRODUCAO_FIM_DO_ARQUIVO)){
			a = "'PRODUCAO FIM DO ARQUIVO'";
		}else{
			a = this.simbolo;
		}
		return a;
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
