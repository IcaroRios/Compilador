package Model;

public class TokenToRegraGramatica {
	RegraTerminal terminalVazio;
    public TokenToRegraGramatica(){
    	super();
    	this.terminalVazio = new RegraTerminal(Constants.PRODUCAO_VAZIA);
    }
    
    public RegraTerminal tokenToTerminal(Token token){
    	RegraTerminal regra = null;
    	//palavra reservada
    	if(token.getId() == Constants.ID_PALAVRA_RESERVADA){
    		String simbolo = "'"+token.getLexema()+"'";
    		regra = new RegraTerminal(simbolo);
    	//numero
    	}else if(token.getId() == Constants.ID_NUMERO){
    		String simbolo = "Number";
    		regra = new RegraTerminal(simbolo);
    	//op aritmetico
    	}else if(token.getId() == Constants.ID_OP_ARITMETICO){
    		String simbolo = null;
    		if(token.getTipo() == Constants.OP_ATRI_SUM || 
    				token.getTipo() == Constants.OP_ATRI_SUB){
    			simbolo = "AritmeticOpSum";
    		}else{
    			simbolo = "AritmeticOpMult";
    		}
    		regra = new RegraTerminal(simbolo);
    	//op relacional
    	}else if(token.getId() == Constants.ID_OP_RELACIONAL){
    		String simbolo = null;
    		if(token.getTipo() == Constants.OP_REL_DIFERENTE ||
    				token.getTipo() == Constants.OP_REL_VERIFICACAO){
    			simbolo = "RelationalOpEqual";
    		}else{
    			simbolo = "RelationalOpComp";
    		}
    		regra = new RegraTerminal(simbolo);
    	//op atribuicao
    	}else if(token.getId() == Constants.ID_OP_DE_ATRIBUICAO){
    		String simbolo = "'='";
    		regra = new RegraTerminal(simbolo);
    	//op logico
    	}else if(token.getId() == Constants.ID_OP_LOGICO){
    		String simbolo = null;
    		if(token.getTipo() == Constants.OP_LOG_NEG){
    			simbolo = "LogicOpSimple";
    		}else if(token.getTipo() == Constants.OP_LOG_AND){
    			simbolo = "'&&'";
    		}else if(token.getTipo() == Constants.OP_LOG_OR){
    			simbolo = "'||'";
    		}
    		regra = new RegraTerminal(simbolo);
    	//delimitador
    	}else if(token.getId() == Constants.ID_DELIMITADOR){
    		String simbolo = "'"+token.getLexema()+"'";
    		regra = new RegraTerminal(simbolo);
    	//identificador
    	}else if(token.getId() == Constants.ID_IDENTIFICADOR){
    		String simbolo = "Id";
    		regra = new RegraTerminal(simbolo);
    	//caracter
    	}else if(token.getId() == Constants.ID_CARACTERE){
    		String simbolo = "Char";
    		regra = new RegraTerminal(simbolo);    		
    	//cadeia de caracter
    	}else if(token.getId() == Constants.ID_CADEIA_DE_CARACTERES){
    		String simbolo = "CharChain";
    		regra = new RegraTerminal(simbolo);
    	}
		return regra;
    }
    
    
	/*
	Retorna 1 se o token for equivalente a regra da gramatica esperada
    Retorna 0 se o token nao for equivalente a regra da gramatica
    Retorna -1 se a regra da gramatica for uma regra nao terminal,
    nao podem ser feitas comparacoes
    */
    public int compare(RegraTerminal terminal, RegraGramatica regra){
    	//regra eh um terminal
        if(regra.isTerminal()){
        	RegraTerminal r = (RegraTerminal) regra;
        	//TODO PEGAR A LISTA DE EQUIVALENCIA ENTRE SIMBOLOS E TOKENS        	        
        	return getTerminalType(terminal, r);
        	
        }
        //regra eh um nTerminal
        else{
        	return -1;	
        }    	       
    }

	private int getTerminalType(RegraTerminal t1, RegraTerminal t2) {				
		if(t1.equals(t2)){
			return 1;
		}else if(t1.equals(terminalVazio ) || t2.equals(terminalVazio)){
			System.out.println(".........................ISSO FUNCIONOU");
			return 1;			
		}
		return 0;			
	}

}
