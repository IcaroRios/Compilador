package semantic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import exceptions.CantSolveExpression;
import exceptions.SemanticExpectedError;
import Model.Constants;
import Model.Folha;
import Model.Token;

public class AnalisadorSemantico{
	private List<Token> tokens;
	private boolean hasError;
	private String errors;
	private LinkedList<TokenId> declaredVariables;
	private LinkedList<TokenFunction> declaredFunctions;
	private ExpSolver exp;
		
	public AnalisadorSemantico(){
		this.declaredVariables = new LinkedList<>();
		this.declaredFunctions = new LinkedList<>();		
	}

	public void executar(String name, Folha tree, List<Token> listTokens){
		this.tokens = listTokens;		
		this.hasError = false;
		this.errors = "";
		this.preCompile();		
		this.compile();
		this.exp = new ExpSolver(this.tokens, this);
		this.functionHasReturn();		
		this.verifyIfWhile();
		//this.verifyExpressions();
		this.gerarSaida(name);
		/*
		for(TokenId tokenId : declaredVariables){
			if(!tokenId.wasUsed()){
				System.out.println(" nao utilizado "+tokenId);	
			}else{
				System.out.println(tokenId);
			}
		}
		for(TokenFunction tokenFunction : declaredFunctions){
			System.out.println(tokenFunction);
		}
		//*/
		System.out.println(hasError);
		System.out.println("ERRORS:\n"+this.errors);				
	}	

	//lista variaveis e funcoes, verifica se existem variaveis duplicadas
		private void preCompile(){
			String escopo = Constants.ESCOPO_GLOBAL_ID;//escopo de declaracao da variavel
			for(int i = 0; i < this.tokens.size(); i++){
				//achar variaveis declaradas e seus tipos
				if(tokens.get(i).getLexema().equals("const")){//achar um bloco de declaracao
					i++;//pula o var
					i++;//pula o begin
					while(!tokens.get(i).getLexema().equals("end")){//ate achar um end
						String t = tokens.get(i).getLexema();//tipo da declaracao
						int tipo = this.getTipo(t);
						i++;//vai para a 1° variavel
						while(!tokens.get(i).getLexema().equals(";")){//ate achar um ;
							Token a = tokens.get(i);
							boolean isArray = false;
							if(tokens.get(i).getLexema().equals(";")){

							}
							TokenId aux = new TokenId(a.getLexema(), escopo, tipo, true, a.getnLinha(),
									isArray,false);
							this.addVariable(aux);
							//System.out.println(tokens.get(i)+" : "+tipo);
							i++;
							if(tokens.get(i).getLexema().equals(",")){
								i++;
							}
						}
						if(tokens.get(i+1).getLexema().equals("end")){
							break;
						}else{
							i++;
						}
					}
				}
				//achou uma funcao, atualiza o escopo
				if(tokens.get(i).getLexema().equals("function")){
					i++;				 
					escopo = tokens.get(i).getLexema();			
					LinkedList<TokenId> parametros = new LinkedList<>();
					//int c = 2;
					if(tokens.get(i+2).getLexema().equals(")")){
						i+=2;
					}else{
						i+=3;
					}				
					while(!tokens.get(i).getLexema().equals(")")){//enquanto n for ')'
						boolean isArray = false;
						if(tokens.get(i+1).getLexema().equals("[")){//se for array
							isArray = true;
						}
						//TODO terminar array aqui
						TokenId aux = new TokenId(tokens.get(i).getLexema(), escopo,
								this.getTipo(tokens.get(i-1).getLexema()), false,
								tokens.get(i).getnLinha(), isArray, true);
						//System.out.println("aqui...  "+tokens.get(i).getLexema());
						parametros.add(aux);//add o parametro								
						this.addVariable(aux);//add o parametro como variavel declarada					
						if(tokens.get(i+1).getLexema().equals(")")){
							i++;
							break;
						}
						i+=3;
					}
					i++;
					TokenFunction e = null;
					//System.out.println(tokens.get(i));
					if(tokens.get(i).getLexema().equals(":")){//se a funcao possui retorno					
						int returnType = this.getTipo(tokens.get(i+2).getLexema());
						e = new TokenFunction(escopo, parametros, tokens.get(i).getnLinha(),
								true, returnType, i+3);
					}else{//se nao possui retorno
						e = new TokenFunction(escopo, parametros, tokens.get(i).getnLinha(),
								false, -1, i+1);
					}				
					this.addFunction(e);
				}
				//achar variaveis declaradas e seus tipos
				if(tokens.get(i).getLexema().equals("var")){//achar um bloco de declaracao
					i++;//pula o var
					i++;//pula o begin
					while(!tokens.get(i).getLexema().equals("end")){//ate achar um end
						String t = tokens.get(i).getLexema();//tipo da declaracao
						int tipo = this.getTipo(t);
						i++;//vai para a 1° variavel
						while(!tokens.get(i).getLexema().equals(";")){//ate achar um ;
							Token a = tokens.get(i);
							TokenId aux = null;
							if(tokens.get(i+1).getLexema().equals("[")){//se e um array
								aux = new TokenId(a.getLexema(), escopo, tipo, false, a.getnLinha(),
										true, true);
							}else{//nao e um array
								aux = new TokenId(a.getLexema(), escopo, tipo, false, a.getnLinha(),
										false, true);
							}						
							this.addVariable(aux);						
							//System.out.println(tokens.get(i)+" : "+tipo);
							i++;
							if(tokens.get(i).getLexema().equals(",")){
								i++;
							}
						}
						if(tokens.get(i+1).getLexema().equals("end")){
							break;
						}else{
							i++;
						}
					}
				}
			}		
		}	

	//verifica se as variaveis utilizadas foram declaradas e se funcoes utilizadas foram declaradas
	private void compile(){
		String escopo = Constants.ESCOPO_GLOBAL_ID;//escopo de declaracao da variavel
		for(int i = 0; i < this.tokens.size(); i++){
			//achou uma funcao, atualiza o escopo				
			if(tokens.get(i).getLexema().equals("function")){
				i++;
				escopo = tokens.get(i).getLexema();
				i++;//pula o id, nome da funcao
			}
			if(tokens.get(i).getId()==Constants.ID_IDENTIFICADOR &&
					tokens.get(i+1).getLexema().equals("(")){//achar uma funcao
				Token a = tokens.get(i);		
				TokenFunction chamada = new TokenFunction(a.getLexema(), a.getnLinha());
				//System.out.println("aqui: "+aux);
				if(!this.declaredFunctions.contains(chamada)){//caso nao exista funcao com esse nome				
					this.escreverErroFuncaoNaoDeclarada(chamada);
				}else{//caso exista funcao com esse nome
					TokenFunction funcao = this.getFuncaoNome(chamada);
					//TODO VERIFICAR SE A CHAMA COINCIDE COM A FUNCAO
					this.verificaFuncao(chamada, funcao);
				}
			}else if(tokens.get(i).getId()==Constants.ID_IDENTIFICADOR){//achar um id
				Token a = tokens.get(i);							
				TokenId aux = new TokenId(a.getLexema(), escopo, a.getnLinha());
				//if(!this.declaredVariables.contains(aux)){
				if(!this.containsVariable(aux)){
					this.escreverErroVariavelNaoDeclarada(aux);
				}else{					
					TokenId b = this.getIdNome(aux);
					if(b!=null){//variavel nao declarada retorna null
						b.setWasUsed();
						//System.out.println("set "+b.getNome());
					}
				}
			}
		}
	}					

	//procura pelo if e while, deve verificar se sao booleans
	private void verifyIfWhile(){
		boolean isIf = false;
		String escopo = Constants.ESCOPO_GLOBAL_ID;//escopo de declaracao da variavel
		for(int i = 0; i < this.tokens.size(); i++){
			//achou uma funcao, atualiza o escopo				
			if(tokens.get(i).getLexema().equals("function")){
				i++;
				escopo = tokens.get(i).getLexema();
				i++;//pula o id, nome da funcao
			}

			if(tokens.get(i).getLexema().equals("if") || tokens.get(i).getLexema().equals("while")){
				if(tokens.get(i).getLexema().equals("if")){
					isIf = true;
				}else{
					isIf = false;
				}
				i+=2;//pula o (
				int qtdP = 0;	
				int nLinha = tokens.get(i).getnLinha();
				this.exp.createExp(nLinha);
				//copiar a expressao ate o fim do if/while
				while(!tokens.get(i).getLexema().equals(")") || qtdP!=0){
					if(tokens.get(i).getLexema().equals("(")){
						qtdP++;						
					}else if(tokens.get(i).getLexema().equals(")")){
						qtdP--;
					}else if(tokens.get(i).getLexema().equals("[")){
						i = this.verifyArrayParameter(i, nLinha, escopo);
					}
					this.exp.addExpToken(i, escopo);
					i++;
				}				
				try {//resolver expressao
					int result = this.exp.solveExp();
					if(result != Constants.EXP_BOOLEAN){//deu um boolean como resultado
						this.escreverErroIfWhile(nLinha, isIf, exp.getNameExpByCons(result));						
					}
				} catch (SemanticExpectedError e){
					this.escreverErroTipos(e.getNLinha(), e.getExpected(), e.getRecieved());
				} catch (CantSolveExpression e){
					this.escreverErroCantSolveExpression(e.getNLinha(),e.getExp());
				}
				
			}
		}
		
	}

	int verifyArrayParameter(int i, int nLinha, String escopo){//i starts on [
		int qtdC = 0;
		i++;
		ExpSolver ex = new ExpSolver(tokens, this);
		ex.createExp(nLinha);
		int result = 0;
		while(!tokens.get(i).getLexema().equals("]") || qtdC != 0){
			if(tokens.get(i).getLexema().equals("[")){
				qtdC++;
			}else if(tokens.get(i).getLexema().equals("]")){
				qtdC--;
			}				
			ex.addExpToken(i, escopo);			
			i++;
		}//saiu tem a expressao completa
		try {
			result = ex.solveExp();
			if(result != Constants.EXP_NUM_INT){//deu um boolean como resultado
				this.escreverErroParameterC(nLinha, this.exp.getNameExpByCons(result));						
			}
		} catch (SemanticExpectedError e){
			this.escreverErroTipos(e.getNLinha(), e.getExpected(), e.getRecieved());
		} catch (CantSolveExpression e){
			this.escreverErroCantSolveExpression(e.getNLinha(),e.getExp());			
		}		
		return i;//retorna a posicao atual
	}
	
	//procura por expressoes e verifica se os tipos sao compariveis
	private void verifyExpressions(){		 
		String escopo = Constants.ESCOPO_GLOBAL_ID;//escopo de declaracao da variavel
		TokenId tkId = null;
		for(int i = 0; i < this.tokens.size(); i++){
			//achou uma funcao, atualiza o escopo
			if(tokens.get(i).getLexema().equals("function")){
				i++;
				escopo = tokens.get(i).getLexema();
				i++;//pula o id, nome da funcao
			}else if(tokens.get(i).getId() == Constants.ID_IDENTIFICADOR){
				tkId = new TokenId(tokens.get(i).getLexema(), escopo, tokens.get(i).getnLinha());
			}else if(tokens.get(i).getId() == Constants.ID_OP_DE_ATRIBUICAO &&
					tokens.get(i+1).getLexema().equals("[")){//achou um = [....]
				//System.out.println("aquiiii");
				int nLinha = tokens.get(i).getnLinha();
				this.exp.createExp(nLinha);
				//copiar a expressao ate o fim do if/while
				while(!tokens.get(i).getLexema().equals(",")){
					if(tokens.get(i).getId() == Constants.ID_IDENTIFICADOR &&
							tokens.get(i+1).getLexema().equals("[")){
						this.exp.addExpToken(i, escopo);
						i = this.verifyArrayParameter(i, nLinha, escopo);
					}else if(tokens.get(i).getId() == Constants.ID_IDENTIFICADOR &&
							tokens.get(i+1).getLexema().equals("(")){//chamada de funcao
						TokenFunction chamada = new TokenFunction(tokens.get(i).getLexema(), nLinha);
						this.exp.addExpToken(i, escopo);
						i = this.verifyFunction(chamada, i, escopo);
					}else{
						this.exp.addExpToken(i, escopo);
					}
					i++;
				}
				try {//resolver expressao
					int result = this.exp.solveExp();
					TokenId t = this.getIdNome(tkId);
					if(t == null){
						this.escreverErroVariavelNaoDeclarada(tkId);
					}else if((result == Constants.EXP_NUM_INT || result == Constants.EXP_NUM_REAL) &&
							(t.getTipo() == Constants.EXP_NUM_INT || 
							t.getTipo() == Constants.EXP_NUM_REAL)){
						//se um dos dois e inteiro ou real e o outro e um inteiro ou real
						
					}else if(result != t.getTipo()){//deu resultado diferente do esperado
						this.escreverErroExpression(nLinha, t, exp.getNameExpByCons(result));
					}
				} catch (SemanticExpectedError e){
					this.escreverErroTipos(e.getNLinha(), e.getExpected(), e.getRecieved());
				} catch (CantSolveExpression e){
					this.escreverErroCantSolveExpression(e.getNLinha(),e.getExp());
				}
				
			}else if(tokens.get(i).getId() == Constants.ID_OP_DE_ATRIBUICAO){//achou um =				
				int nLinha = tokens.get(i).getnLinha();
				this.exp.createExp(nLinha);
				//copiar a expressao ate o fim do if/while
				while(!tokens.get(i).getLexema().equals(";")){
					if(tokens.get(i).getId() == Constants.ID_IDENTIFICADOR &&
							tokens.get(i+1).getLexema().equals("[")){
						this.exp.addExpToken(i, escopo);
						i = this.verifyArrayParameter(i, nLinha, escopo);
					}else if(tokens.get(i).getId() == Constants.ID_IDENTIFICADOR &&
							tokens.get(i+1).getLexema().equals("(")){//chamada de funcao						
						TokenFunction chamada = new TokenFunction(tokens.get(i).getLexema(), nLinha);
						this.exp.addExpToken(i, escopo);
						i = this.verifyFunction(chamada, i, escopo);
					}else{
						this.exp.addExpToken(i, escopo);
					}
					i++;
				}
				try {//resolver expressao
					int result = this.exp.solveExp();
					TokenId t = this.getIdNome(tkId);
					//TODO ERRO AQUI					
					if(t == null){
						this.escreverErroVariavelNaoDeclarada(tkId);
					}else if((result == Constants.EXP_NUM_INT || result == Constants.EXP_NUM_REAL) &&
							(t.getTipo() == Constants.EXP_NUM_INT || 
							t.getTipo() == Constants.EXP_NUM_REAL)){
						//se um dos dois e inteiro ou real e o outro e um inteiro ou real
						
					}else if(result != t.getTipo()){//deu resultado diferente do esperado
						this.escreverErroExpression(nLinha, t, exp.getNameExpByCons(result));
					}
				} catch (SemanticExpectedError e){
					this.escreverErroTipos(e.getNLinha(), e.getExpected(), e.getRecieved());
				} catch (CantSolveExpression e){
					this.escreverErroCantSolveExpression(e.getNLinha(),e.getExp());
				}
				
			}
		}
		
	}
	
	private int verifyFunction(TokenFunction tkF, int i, String escopo){
		TokenFunction t = this.getFuncaoNome(tkF);
		int j = 0;
		if(t == null){//funcao nao declarada
			this.escreverErroFuncaoNaoDeclarada(tkF);
		}else{
			while(!tokens.get(i).getLexema().equals("(")){
				i++;
			}
			i++;
			for(j = 0; j < t.getParameters().size(); j++){
				ExpSolver ex = new ExpSolver(tokens, this);
				ex.createExp(tkF.getNLinha());
				boolean a = true;
				while(!tokens.get(i).getLexema().equals(",") && 
						!tokens.get(i).getLexema().equals(")")){
					//System.out.println(tokens.get(i));
					ex.addExpToken(i, escopo);
					a = false;
					i++;
				}
				try {
					if(a && t.getParameters().size() != 0){
						this.escreverErroFuncaoFaltaParametros(t, tkF.getNLinha(), j);
						break;
					}
					int res = ex.solveExp();
					if(res != t.getParameters().get(j).getTipo()){
						this.escreverErroFuncaoParametroNaoTemMesmoTipo(t, tkF.getNLinha(),
								j+1, exp.getNameExpByCons(t.getParameters().get(j).getTipo()),
								exp.getNameExpByCons(res));						
					}
					if(tokens.get(i).getLexema().equals(")") && j <= t.getParameters().size()){
						//acabou os parametros da chamada mas ainda tem parametros na funcao
						this.escreverErroFuncaoFaltaParametros(t, tkF.getNLinha(), j);
						break;
					}
				} catch(SemanticExpectedError e){
					this.escreverErroTipos(e.getNLinha(), e.getExpected(), e.getRecieved());
				} catch(CantSolveExpression e){
					this.escreverErroCantSolveExpression(e.getNLinha(),e.getExp());
				}
			}
			if(!tokens.get(i).getLexema().equals(";") && j == t.getParameters().size()){
				//se ainda tem mais parametros na chamada da funcao do que na declaracao
				this.escreverErroFuncaoTemMaisParametros(t, tkF.getNLinha());
				while(tokens.get(i).getLexema().equals(")")){					
					i++;
				}
				i++;
			}
		}
		return i;
	}

	//verifica se as funcoes tem retorno, e se seus tipos estao certos
	private void functionHasReturn(){
		for(TokenFunction tkF : declaredFunctions){
			if(tkF.hasReturn()){//caso a funcao tenha retorno
				int index = tkF.getTokenPosition();
				hasReturnForAllFlows(index, tkF);
			}else{//caso a funcao nao tenha retorno
				int j = hasFunctionReturn(tkF);
				if(j != -1){//caso tenha retorno, mesmo n devendo ter
					escreverErroFuncaoNaoDeviaTerRetorno(tkF, tokens.get(j));					
				}
			}
		}
	}

	//retorna a posicao do return, caso nao ache, retorna -1
	private int hasFunctionReturn(TokenFunction tkF){
		int i = tkF.getTokenPosition();
		while(i<tokens.size()){
			if(tokens.get(i).getLexema().equals("return")){//achou um return
				return i;
			}else if(tokens.get(i).getLexema().equals("function")){//achou um inicio de outra funcao
				return -1;
			}
			i++;
		}
		return -1;
	}

	private void hasReturnForAllFlows(int index, TokenFunction tkF){		
		int nivel = 0;
		boolean returnHighLevel = false;
		for(int i = tkF.getTokenPosition(); i < tokens.size(); i++){			
			if(tokens.get(i).getLexema().equals("return") && nivel==0){
				returnHighLevel = true;				
				//TODO OLHAR OS TIPOS DESSE E DE TODOS OS RETURN DA FUNCAO
			}else if(tokens.get(i).getLexema().equals("return")){//return dentro de algum bloco
				//TODO OLHAR TIPO DO RETORNO
			}else if(tokens.get(i).getLexema().equals("begin")){//achou um bloco
				nivel++;
			}else if(tokens.get(i).getLexema().equals("end")){//fechou um bloco
				nivel--;
				if(nivel==-1){//acabou a funcao
					break;
				}
			}
		}
		if(!returnHighLevel){
			escreverErroFuncaoDeviaTerRetorno(tkF);
		}
	}

	TokenFunction getFuncaoNome(TokenFunction chamada){
		for(int i = 0; i < declaredFunctions.size(); i++){
			if(declaredFunctions.get(i).equals(chamada)){
				return declaredFunctions.get(i);
			}
		}
		return null;
	}

	TokenId getIdNome(TokenId obj){
		for(int i = 0; i < declaredVariables.size(); i++){
			if(declaredVariables.get(i).equals(obj)){
				return declaredVariables.get(i);
			}
		}
		return null;
	}


	private void verificaFuncao(TokenFunction chamada, TokenFunction funcao){
		//TODO DEVE VERIFICAR SE OS PARAMETROS PASSADOS SAO EQUIVALENTES AOS PARAMETROS ESPERADOS PELO
		//ESCOPO DA FUNCAO DECLARADA. USAR A ARVORE???

	}

	private boolean containsVariable(TokenId aux){
		for(int i = 0; i < declaredVariables.size(); i++){
			if(aux.myEquals(declaredVariables.get(i))){
				return true;
			}
		}
		return false;
	}

	private void addVariable(TokenId aux){
		if(this.declaredVariables.contains(aux)){
			int i = 0;
			while(!declaredVariables.get(i).equals(aux)){
				i++;
			}
			this.escreverErroVariavelDuplicada(aux, declaredVariables.get(i));
		}else{
			this.declaredVariables.add(aux);
		}		

	}

	private void addFunction(TokenFunction e){
		if(this.declaredFunctions.contains(e)){
			int i = 0;
			while(!declaredFunctions.get(i).equals(e)){
				i++;
			}
			this.escreverErroFuncoesDuplicada(e, declaredFunctions.get(i));
		}else{
			this.declaredFunctions.add(e);
		}
	}	
	
	private int getTipo(String t){
 		int tipo = 0;
		if(t.equals("boolean")){			
			tipo = Constants.EXP_BOOLEAN;
 		}else if(t.equals("string")){
			tipo = Constants.EXP_STRING;
 		}else if(t.equals("char")){
			tipo = Constants.EXP_CHAR;
 		}else if(t.equals("integer")){
			tipo = Constants.EXP_NUM_INT;
 		}else if(t.equals("real")){
 			tipo = Constants.EXP_NUM_REAL;
 		}
 		return tipo;
 	}
	
	private void escreverErroExpression(int linha, TokenId id, String recieved){
		this.hasError = true;
		this.errors = errors+ "On line "+linha+
				"\nThe expression should have a "+this.exp.getNameExpByCons(id.getTipo())+
				" value to the id '"+id.getNome()+
		"'. But the expression resulted in a "+recieved+ " value\n";
	}
	
	
	private void escreverErroFuncaoTemMaisParametros(TokenFunction funcao, int linhaChamada){
		this.hasError = true;
		this.errors = errors+ "Function "+funcao.getNome()+" was called at line "+linhaChamada+
				" and has more parameters in this call than in it's declaration\n";
	}
	
	private void escreverErroFuncaoFaltaParametros(TokenFunction funcao, int linhaChamada,
			int parameterPosition){
		this.hasError = true;
		this.errors = errors+ "On line "+linhaChamada+
				"\nFunction "+funcao.getNome()+" was called and is missing";
		for(; parameterPosition < funcao.getParameters().size(); parameterPosition++){
			this.errors = this.errors+" the parameter "+
		funcao.getParameters().get(parameterPosition).getNome()+",";
		}
		this.errors = errors+"\n";
	}
	
	private void escreverErroFuncaoParametroNaoTemMesmoTipo(TokenFunction funcao, int linhaChamada,
			int parameterPosition, String expected, String recieved){
		this.hasError = true;
		this.errors = errors+ "On line "+linhaChamada+
				"\nFunction "+funcao.getNome()+" was called"+
				" and the "+parameterPosition+"° parameter should be a "+expected+" but recieved "+
				recieved+"\n";		
	}
	
	private void escreverErroIfWhile(int nLinha, boolean isIf, String result){
		this.hasError = true;
		String st = "while";
		if(isIf){
			st = "if";
		}
		this.errors = errors+ "On line "+nLinha+
				"\n\tThe statement "+st+" must recieve a boolean expression but recieved "+
				"The result was"+result+"\n";
	}
	
	private void escreverErroCantSolveExpression(int nLinha, LinkedList<String>e){
		this.hasError = true;
		this.errors = errors+ "On line "+nLinha+
				"\n\tFor some reason the compiler can't solver the expression." +
				"\nThe result was"+ e +"\n";
		System.out.println("On line "+nLinha+
				"\n\tFor some reason the compiler can't solver the expression." +
				"\nThe result was"+ e +"\n");
	}
		
	private void escreverErroParameterC(int nLinha, String result){
		this.hasError = true;
		this.errors = errors+ "On line "+nLinha+
				"\n\tThe parameter of an array must be an Integer numeral.\n"+
				"But recieved "+result+"\n";
	}
	
	private void escreverErroTipos(int nLinha, String expected, String recieved){
		this.hasError = true;
		this.errors = errors+ "On line "+nLinha+
				"\n\tExpression expected "+expected+" but recieved "+recieved+"\n";
	}
	
	
	private void escreverErroFuncaoDeviaTerRetorno(TokenFunction e){
		this.hasError = true;
		this.errors = errors+ "On line "+e.getNLinha()+
				"\n\tfunction "+e.getNome()+" has no return statement, "+
				" but should have it.\n";		
	}

	private void escreverErroFuncaoNaoDeviaTerRetorno(TokenFunction e, Token tk){
		this.hasError = true;
		this.errors = errors+ "On line "+e.getNLinha()+
				"\n\tfunction "+e.getNome()+" has return statement on "+tk.getnLinha()+
				" but should not have it.\n";		
	}

	private void escreverErroFuncoesDuplicada(TokenFunction e, TokenFunction tF){
		this.hasError = true;
		this.errors = errors+ "On line "+e.getNLinha()+
				"\n\tfunction "+e.getNome()+" is already declared at line "+tF.getNLinha()+"\n";		
	}

	private void escreverErroVariavelDuplicada(TokenId aux, TokenId b){
		this.hasError = true;
		this.errors = errors+ "On line "+aux.getnLinha()+
				"\n\tvariable "+aux.getNome()+" is already declared at line "+b.getnLinha()+"\n";		
	}


	void escreverErroVariavelNaoDeclarada(TokenId aux){
		this.hasError = true;		
		this.errors = errors+ "On line "+aux.getnLinha()+
				"\n\tvariable "+aux.getNome()+" is not declared\n";
	}


	void escreverErroFuncaoNaoDeclarada(TokenFunction aux){
		this.hasError = true;
		this.errors = errors+ "On line "+aux.getNLinha()+
				"\n\tfunction "+aux.getNome()+" is not declared\n";
	}

	public void cleanLists(){
		this.declaredVariables.clear();
		this.declaredFunctions.clear();
	}
	
	private void gerarSaida(String arquivo){
		try {
			File pasta = new File(Constants.PASTA_SAIDA_SEM);
			pasta.mkdir();
			File n = new File(pasta.getName() + File.separator +
					"Out_Sem_"+arquivo.split("\\.")[0]+ Constants.ARQUIVO_EXTENSAO_SEM);			
			BufferedWriter bw = new BufferedWriter(new FileWriter(n));			
			if(this.hasError){//se houve erros		
				bw.write("------------------------------ERROS SEMANTICOS"
						+ " IDENTIFICADOS------------------------------");
				bw.newLine();
				bw.flush();
				bw.write(this.errors);
			}else{//se nao houve erros				
				bw.write("SUCESSO NA ANALISE SEMANTICA DO ARQUIVO: "+arquivo);
				System.out.println("Analise Semantica para o arquivo: " + arquivo + ": Sucesso.");
			}
			bw.close();			
		} catch (IOException ex) {
			System.out.println("Deu merda na escrita do arquivo."
					+ "\nVerifique as permissoes de execucao do codigo.");
		}
	}

}
