package semantic;

import java.util.LinkedList;
import java.util.List;

import exceptions.CantSolveExpression;
import exceptions.SemanticExpectedError;
import Model.Constants;
import Model.Token;

public class ExpSolver {
	private LinkedList<Integer> exp;
	private List<Token> tokens;
	private AnalisadorSemantico aSem;
	private boolean hasParenteses;
	private int nLinha;

	public ExpSolver(List<Token> tokens, AnalisadorSemantico aSem){
		super();
		this.tokens = tokens;
		this.aSem = aSem;
		this.hasParenteses = false;
	}

	public void createExp(int nLinha){
		this.exp = new LinkedList<>();
		this.nLinha = nLinha;
	}

	public void addExpToken(int i, String escopo){
		if(tokens.get(i).getId()==Constants.ID_IDENTIFICADOR &&
				tokens.get(i+1).getLexema().equals("(")){//caso seja uma chamada de funcao
			Token a = tokens.get(i); 
			TokenFunction aux = new TokenFunction(a.getLexema(), a.getnLinha());
			TokenFunction tkF = this.aSem.getFuncaoNome(aux);
			if(tkF == null){//se nao existe essa funcao
				this.aSem.escreverErroFuncaoNaoDeclarada(aux);
				return;
			}else{
				exp.add(tkF.getReturnType());
			}
			while(!tokens.get(i).getLexema().equals(")")){
				i++;//indo ate o fim da chamada da funcao
			}
		}else if(tokens.get(i).getId() == Constants.ID_IDENTIFICADOR){//caso identificador
			Token a = this.tokens.get(i);
			TokenId aux = new TokenId(a.getLexema(), escopo, a.getnLinha());
			TokenId tkI = this.aSem.getIdNome(aux);
			if(tkI == null){
				this.aSem.escreverErroVariavelNaoDeclarada(aux);
				return;//para a analise do if/while
			}else{
				exp.add(tkI.getTipo());							
			}
			if(aux.isArray()){
				i = this.aSem.verifyArrayParameter(i, nLinha, escopo);
			}
		}else if(tokens.get(i).getId() == Constants.ID_CARACTERE){//caracter
			exp.add(Constants.EXP_CHAR);
		}else if(tokens.get(i).getId() == Constants.ID_CADEIA_DE_CARACTERES){//string
			exp.add(Constants.EXP_STRING);
		}else if(tokens.get(i).getId() == Constants.ID_NUMERO &&
				tokens.get(i).getTipo() == 1){//int
			exp.add(Constants.EXP_NUM_INT);
		}else if(tokens.get(i).getId() == Constants.ID_NUMERO &&
				tokens.get(i).getTipo() == 2){//real
			exp.add(Constants.EXP_NUM_REAL);
		}else if(tokens.get(i).getId() == Constants.ID_PALAVRA_RESERVADA &&
				(tokens.get(i).getTipo() == Constants.PAL_RES_TRUE || 
				tokens.get(i).getTipo() == Constants.PAL_RES_FALSE)){//boolean
			exp.add(Constants.EXP_BOOLEAN);
		}else if(tokens.get(i).getId() == Constants.ID_OP_RELACIONAL &&
				(tokens.get(i).getTipo() == Constants.OP_REL_DIFERENTE ||
				tokens.get(i).getTipo() == Constants.OP_REL_VERIFICACAO)){//op == !=
			exp.add(Constants.EXP_OP_REL_IGU_DIF);
		}else if(tokens.get(i).getId() == Constants.ID_OP_RELACIONAL){//op relacional
			exp.add(Constants.EXP_OP_REL_MAI_MEN);
		}else if(tokens.get(i).getId() == Constants.ID_OP_LOGICO &&				
				tokens.get(i).getTipo() == Constants.OP_LOG_OR){//or
			exp.add(Constants.EXP_OP_LOG_OR);			
		}else if(tokens.get(i).getId() == Constants.ID_OP_LOGICO &&
				tokens.get(i).getTipo() == Constants.OP_LOG_AND){//and
			exp.add(Constants.EXP_OP_LOG_AND);
		}else if(tokens.get(i).getId() == Constants.ID_OP_LOGICO &&
				tokens.get(i).getTipo() == Constants.OP_LOG_NEG){//neg
			exp.add(Constants.EXP_NEG);
		}else if(tokens.get(i).getId() == Constants.ID_OP_ARITMETICO &&
				(tokens.get(i).getTipo() == Constants.OP_ATRI_SUB ||
				tokens.get(i).getTipo() == Constants.OP_ATRI_SUM)){//sum/sub
			exp.add(Constants.EXP_OP_ARIT_SUM_SUB);
		}else if(tokens.get(i).getId() == Constants.ID_OP_ARITMETICO &&
				tokens.get(i).getTipo() == Constants.OP_ATRI_MUL){//mult
			exp.add(Constants.EXP_OP_ARIT_MUL);
		}else if(tokens.get(i).getId() == Constants.ID_OP_ARITMETICO &&
				tokens.get(i).getTipo() == Constants.OP_ATRI_DIV){//div
			exp.add(Constants.EXP_OP_ARIT_DIV);
		}else if(tokens.get(i).getId() == Constants.ID_OP_ARITMETICO &&
				tokens.get(i).getTipo() == Constants.OP_ATRI_RDIV){//res div
			exp.add(Constants.EXP_OP_ARIT_REST_DIV);
		}else if(tokens.get(i).getId() == Constants.ID_DELIMITADOR &&
				tokens.get(i).getTipo() == Constants.DELIMITADOR_PAREN_OPEN){
			exp.add(Constants.EXP_PAREN_OPEN);
			this.hasParenteses = true;
		}else if(tokens.get(i).getId() == Constants.ID_DELIMITADOR &&
				tokens.get(i).getTipo() == Constants.DELIMITADOR_PAREN_CLOSE){
			exp.add(Constants.EXP_PAREN_CLOSE);
		}

	}

	public int solveExp() throws SemanticExpectedError, CantSolveExpression{
		//System.out.println("aqui   "+this.exp);
		if(this.hasParenteses){
			this.solveRec(0);
		}else{
			solve(0);
		}
		//System.out.println("SIZE: "+exp.size());
		//System.out.println("VALUE: "+exp);
		if(exp.size()>1){//n restou apenas um elemento na expressao
			LinkedList<String> a = new LinkedList<>();
			for (int i : exp){
				a.add(this.getNameExpByCons(i));
			}
			throw new CantSolveExpression(this.nLinha, a);
		}
		return this.exp.getFirst();//se tem apenas 1 elemento, eh o resultado
	}

	private void solveRec(int position) throws SemanticExpectedError{
		int i;
		//System.out.println("solveRec");
		for(i = position; i < this.exp.size(); i++){//passo de recursao
			if(exp.get(i) == Constants.EXP_PAREN_OPEN){
				//System.out.println("indice->"+(i+1));
				solveRec(i+1);
				exp.remove(i);
				//break;				
			}else if(exp.get(i) == Constants.EXP_PAREN_CLOSE){				
				break;
			}
		}//ao sair o i tem a posicao do parenteses mais alto		
		//System.out.println("solveRec   "+exp + " indice->"+i);
		solve(position);
		//System.out.println(position);
		this.removeCloseParen(position);
	}

	private void solve(int i) throws SemanticExpectedError{
		for(int j = i; j < this.exp.size(); j++){
			//RESOLVER AS ESPRESSOES PASSO A PASSO, NIVEL MAIS ALTO PARA NIVEL MENOR
			if(this.exp.get(j) == Constants.EXP_NEG){//se e uma negacao
				if(this.exp.get(j+1) == Constants.EXP_BOOLEAN){
					this.exp.remove(j);
					this.exp.remove(j);
					this.exp.add(j, Constants.EXP_BOOLEAN);
				}else if(this.exp.get(j+1) == Constants.EXP_PAREN_OPEN){
					this.solveRec(j);
				}else{//expected boolean, but recieved -> exp.get(j+1)						
					throw new SemanticExpectedError(this.nLinha,
							getNameExpByCons(Constants.EXP_BOOLEAN),
							getNameExpByCons(exp.get(j+1)));
				}
			//}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){return;}
			}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){break;}
			else if(this.exp.get(j) == Constants.EXP_PAREN_OPEN){break;}
		}
		for(int j = i; j < this.exp.size(); j++){
			if(this.exp.get(j) == Constants.EXP_OP_ARIT_MUL){//se e uma mult
				if(this.exp.get(j-1) == Constants.EXP_NUM_INT
						&& this.exp.get(j+1) == Constants.EXP_NUM_INT){
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.add(j-1, Constants.EXP_NUM_INT);
				}else if((this.exp.get(j-1) == Constants.EXP_NUM_REAL
						&& this.exp.get(j+1) == Constants.EXP_NUM_INT) ||
						(this.exp.get(j-1) == Constants.EXP_NUM_INT
						&& this.exp.get(j+1) == Constants.EXP_NUM_REAL) ||
						(this.exp.get(j-1) == Constants.EXP_NUM_REAL
						&& this.exp.get(j+1) == Constants.EXP_NUM_REAL)){
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.add(j-1, Constants.EXP_NUM_REAL);
				}else if((this.exp.get(j-1) == Constants.EXP_PAREN_CLOSE
						|| this.exp.get(j+1) == Constants.EXP_PAREN_OPEN)){
					this.solveRec(j);
				}else{
					if(exp.get(j-1)==Constants.EXP_NUM_REAL || exp.get(j-1)==Constants.EXP_NUM_INT){
						throw new SemanticExpectedError(this.nLinha,
								getNameExpByCons(Constants.EXP_ERR_INT_REAL), 
								getNameExpByCons(exp.get(j-1)));
					}else{
						throw new SemanticExpectedError(this.nLinha,
								getNameExpByCons(Constants.EXP_ERR_INT_REAL),
								getNameExpByCons(exp.get(j+1)));
					}							
				}
			//}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){return;}
			}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){break;}
			else if(this.exp.get(j) == Constants.EXP_PAREN_OPEN){break;}
		}
		for(int j = i; j < this.exp.size(); j++){
			if(this.exp.get(j) == Constants.EXP_OP_ARIT_DIV){//se e div
				if((exp.get(j-1) == Constants.EXP_NUM_INT)||(exp.get(j-1) == Constants.EXP_NUM_REAL)&&
					(exp.get(j+1) == Constants.EXP_NUM_INT)||(exp.get(j+1) == Constants.EXP_NUM_REAL)){
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.add(j-1, Constants.EXP_NUM_REAL);
				}else if((this.exp.get(j-1) == Constants.EXP_PAREN_CLOSE
						|| this.exp.get(j+1) == Constants.EXP_PAREN_OPEN)){
					this.solveRec(j);
				}else{						
					if(exp.get(j-1)==Constants.EXP_NUM_REAL||exp.get(j-1)==Constants.EXP_NUM_INT){
						throw new SemanticExpectedError(this.nLinha,
								getNameExpByCons(Constants.EXP_ERR_INT_REAL),
								getNameExpByCons(exp.get(j-1)));
					}else{
						throw new SemanticExpectedError(this.nLinha,
								getNameExpByCons(Constants.EXP_ERR_INT_REAL),
								getNameExpByCons(exp.get(j+1)));
					}
				}
			//}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){return;}
			}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){break;}
			else if(this.exp.get(j) == Constants.EXP_PAREN_OPEN){break;}
		}
		for(int j = i; j < this.exp.size(); j++){
			if(this.exp.get(j) == Constants.EXP_OP_ARIT_REST_DIV){//se e resto de div
				if((exp.get(j-1) == Constants.EXP_NUM_INT)||(exp.get(j+1) == Constants.EXP_NUM_INT)){
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.add(j-1, Constants.EXP_NUM_REAL);
				}else if((this.exp.get(j-1) == Constants.EXP_PAREN_CLOSE
						|| this.exp.get(j+1) == Constants.EXP_PAREN_OPEN)){
					this.solveRec(j);
				}else{						
					if(exp.get(j-1)==Constants.EXP_NUM_INT){
						throw new SemanticExpectedError(this.nLinha,
								getNameExpByCons(Constants.EXP_NUM_INT),
								getNameExpByCons(exp.get(j+1)));
					}else{
						throw new SemanticExpectedError(this.nLinha,
								getNameExpByCons(Constants.EXP_NUM_INT),
								getNameExpByCons(exp.get(j-1)));
					}
				}
			//}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){return;}
			}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){break;}
			else if(this.exp.get(j) == Constants.EXP_PAREN_OPEN){break;}
		}
		for(int j = i; j < this.exp.size(); j++){
			if(this.exp.get(j) == Constants.EXP_OP_ARIT_SUM_SUB){//se e uma soma ou sub
				if(this.exp.get(j-1) == Constants.EXP_NUM_INT
						&& this.exp.get(j+1) == Constants.EXP_NUM_INT){
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.add(j-1, Constants.EXP_NUM_INT);
				}else if((this.exp.get(j-1) == Constants.EXP_NUM_REAL
						&& this.exp.get(j+1) == Constants.EXP_NUM_INT) ||
						(this.exp.get(j-1) == Constants.EXP_NUM_INT
						&& this.exp.get(j+1) == Constants.EXP_NUM_REAL) ||
						(this.exp.get(j-1) == Constants.EXP_NUM_REAL
						&& this.exp.get(j+1) == Constants.EXP_NUM_REAL)){
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.add(j-1, Constants.EXP_NUM_REAL);
				}else if((this.exp.get(j-1) == Constants.EXP_CHAR
						&& this.exp.get(j+1) == Constants.EXP_CHAR) ||
						(this.exp.get(j-1) == Constants.EXP_CHAR
						&& this.exp.get(j+1) == Constants.EXP_STRING) ||
						(this.exp.get(j-1) == Constants.EXP_STRING
						&& this.exp.get(j+1) == Constants.EXP_CHAR) ||
						(this.exp.get(j-1) == Constants.EXP_STRING
						&& this.exp.get(j+1) == Constants.EXP_STRING)){
					//char+char || char+string || string+string
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.add(j-1, Constants.EXP_STRING);
				}else if(this.exp.get(j+1) == Constants.EXP_NUM_INT){
					this.exp.remove(j);
					this.exp.remove(j);
					this.exp.add(j, Constants.EXP_NUM_INT);
				}else if(this.exp.get(j+1) == Constants.EXP_NUM_REAL){
					this.exp.remove(j);
					this.exp.remove(j);
					this.exp.add(j, Constants.EXP_NUM_REAL);
				}else if((this.exp.get(j-1) == Constants.EXP_PAREN_CLOSE
						|| this.exp.get(j+1) == Constants.EXP_PAREN_OPEN)){
					this.solveRec(j);
				}else if(this.exp.get(j+1) == Constants.EXP_NUM_INT){
					this.exp.remove(j);
					this.exp.remove(j);
					this.exp.add(j, Constants.EXP_NUM_INT);
				}else if(this.exp.get(j+1) == Constants.EXP_NUM_REAL){
					this.exp.remove(j);
					this.exp.remove(j);
					this.exp.add(j, Constants.EXP_NUM_REAL);
				}else{
					if(exp.get(j-1)==Constants.EXP_NUM_REAL || exp.get(j-1)==Constants.EXP_NUM_INT
						|| exp.get(j-1)==Constants.EXP_CHAR || exp.get(j-1)==Constants.EXP_STRING){
						throw new SemanticExpectedError(this.nLinha,
								getNameExpByCons(exp.get(j-1)),
								getNameExpByCons(exp.get(j+1)));
					}else{
						throw new SemanticExpectedError(this.nLinha,
								getNameExpByCons(exp.get(j+1)),
								getNameExpByCons(exp.get(j-1)));
					}							
				}
			//}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){return;}
			}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){break;}
			else if(this.exp.get(j) == Constants.EXP_PAREN_OPEN){break;}
		}
		for(int j = i; j < this.exp.size(); j++){
			if(this.exp.get(j) == Constants.EXP_OP_REL_MAI_MEN){//se e comp. maior/menor
				if(this.exp.get(j-1) == Constants.EXP_NUM_INT
						&& this.exp.get(j+1) == Constants.EXP_NUM_INT){
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.add(j-1, Constants.EXP_BOOLEAN);
				}else if((this.exp.get(j-1) == Constants.EXP_NUM_REAL
						&& this.exp.get(j+1) == Constants.EXP_NUM_INT) ||
						(this.exp.get(j-1) == Constants.EXP_NUM_INT
						&& this.exp.get(j+1) == Constants.EXP_NUM_REAL) ||
						(this.exp.get(j-1) == Constants.EXP_NUM_REAL
						&& this.exp.get(j+1) == Constants.EXP_NUM_REAL)){
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.add(j-1, Constants.EXP_BOOLEAN);
				}else if((this.exp.get(j-1) == Constants.EXP_CHAR
						&& this.exp.get(j+1) == Constants.EXP_CHAR)){
					//char/char
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.add(j-1, Constants.EXP_BOOLEAN);
				}else if((this.exp.get(j-1) == Constants.EXP_PAREN_CLOSE
						|| this.exp.get(j+1) == Constants.EXP_PAREN_OPEN)){
					this.solveRec(j);
				}else{
					if(exp.get(j-1)==Constants.EXP_NUM_REAL || exp.get(j-1)==Constants.EXP_NUM_INT
							|| exp.get(j-1)==Constants.EXP_CHAR){
						//System.out.println("AQUI");
						throw new SemanticExpectedError(this.nLinha,
								getNameExpByCons(Constants.EXP_ERR_INT_REAL_CHAR), 
								getNameExpByCons(exp.get(j+1)));
					}else{
						throw new SemanticExpectedError(this.nLinha,
								getNameExpByCons(Constants.EXP_ERR_INT_REAL_CHAR),
								getNameExpByCons(exp.get(j-1)));
					}							
				}
			//}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){return;}
			}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){break;}
			else if(this.exp.get(j) == Constants.EXP_PAREN_OPEN){break;}
		}
		for(int j = i; j < this.exp.size(); j++){
			if(this.exp.get(j) == Constants.EXP_OP_REL_IGU_DIF){//se e comp. == || !=
				if(this.exp.get(j-1) == this.exp.get(j+1)){
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.add(j-1, Constants.EXP_BOOLEAN);
				}else if((this.exp.get(j-1) == Constants.EXP_NUM_REAL
						&& this.exp.get(j+1) == Constants.EXP_NUM_INT) ||
						(this.exp.get(j-1) == Constants.EXP_NUM_INT
						&& this.exp.get(j+1) == Constants.EXP_NUM_REAL)){
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.add(j-1, Constants.EXP_BOOLEAN);
				}else if((this.exp.get(j-1) == Constants.EXP_PAREN_CLOSE
						|| this.exp.get(j+1) == Constants.EXP_PAREN_OPEN)){
					this.solveRec(j);
				}else{
						throw new SemanticExpectedError(this.nLinha,
								getNameExpByCons(exp.get(j-1)),
								getNameExpByCons(exp.get(j+1)));												
				}
			//}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){return;}
			}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){break;}
			else if(this.exp.get(j) == Constants.EXP_PAREN_OPEN){break;}
		}
		for(int j = i; j < this.exp.size(); j++){
			if(this.exp.get(j) == Constants.EXP_OP_LOG_AND){//se e &&
				if(this.exp.get(j-1) == Constants.EXP_BOOLEAN &&
						this.exp.get(j+1) == Constants.EXP_BOOLEAN){
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.add(j-1, Constants.EXP_BOOLEAN);
				}else if((this.exp.get(j-1) == Constants.EXP_PAREN_CLOSE
						|| this.exp.get(j+1) == Constants.EXP_PAREN_OPEN)){
					this.solveRec(j);
				}else{
					if(exp.get(j-1)==Constants.EXP_BOOLEAN){
						throw new SemanticExpectedError(this.nLinha,
								getNameExpByCons(Constants.EXP_BOOLEAN), 
								getNameExpByCons(exp.get(j+1)));
					}else{
						throw new SemanticExpectedError(this.nLinha,
								getNameExpByCons(Constants.EXP_BOOLEAN),
								getNameExpByCons(exp.get(j-1)));
					}							
				}
			//}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){return;}
			}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){break;}
			else if(this.exp.get(j) == Constants.EXP_PAREN_OPEN){break;}
		}
		for(int j = i; j < this.exp.size(); j++){
			if(this.exp.get(j) == Constants.EXP_OP_LOG_OR){//se e ||
				if(this.exp.get(j-1) == Constants.EXP_BOOLEAN &&
						this.exp.get(j+1) == Constants.EXP_BOOLEAN){
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.remove(j-1);
					this.exp.add(j-1, Constants.EXP_BOOLEAN);
				}else if((this.exp.get(j-1) == Constants.EXP_PAREN_CLOSE
						|| this.exp.get(j+1) == Constants.EXP_PAREN_OPEN)){
					this.solveRec(j);
				}else{
					if(exp.get(j-1)==Constants.EXP_BOOLEAN){
						throw new SemanticExpectedError(this.nLinha,
								getNameExpByCons(Constants.EXP_BOOLEAN),
								getNameExpByCons(exp.get(j+1)));
					}else{
						throw new SemanticExpectedError(this.nLinha,
								this.getNameExpByCons(Constants.EXP_BOOLEAN),
								getNameExpByCons(exp.get(j-1)));
					}							
				}
			//}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){return;}
			}else if(this.exp.get(j) == Constants.EXP_PAREN_CLOSE){break;}
			else if(this.exp.get(j) == Constants.EXP_PAREN_OPEN){break;}
		}

	}
	
	public String getNameExpByCons(int i){
		String a = "";
		if(i == Constants.EXP_NUM_INT){
			a = "integer number";
		}else if(i == Constants.EXP_NUM_REAL){
			a = "real number";
		}else if(i == Constants.EXP_BOOLEAN){
			a = "boolean";
		}else if(i == Constants.EXP_STRING){
			a = "string";
		}else if(i == Constants.EXP_CHAR){
			a = "char";
		}else if(i == Constants.EXP_OP_LOG_OR){
			a = "logic operation or";
		}else if(i == Constants.EXP_OP_LOG_AND){
			a = "logic operation and";
		}else if(i == Constants.EXP_OP_REL_IGU_DIF){
			a = "== or !=";
		}else if(i == Constants.EXP_OP_REL_MAI_MEN){
			a = "<, >, <= or >=";
		}else if(i == Constants.EXP_OP_ARIT_SUM_SUB){
			a = "- or +";
		}else if(i == Constants.EXP_OP_ARIT_REST_DIV){
			a = "%";
		}else if(i == Constants.EXP_OP_ARIT_DIV){
			a = "/";
		}else if(i == Constants.EXP_OP_ARIT_MUL){
			a = "*";
		}else if(i == Constants.EXP_NEG){
			a = "!";
		}else if(i == Constants.EXP_PAREN_OPEN){
			a = "(";
		}else if(i == Constants.EXP_PAREN_CLOSE){
			a = ")";
		}else if(i == Constants.EXP_ERR_INT_REAL){
			a = "interger or real number";
		}else if(i == Constants.EXP_ERR_INT_REAL_CHAR){
			a = "integer or real number or a char";
		}		
		return a;
	}
	
	private void removeCloseParen(int j){
		for(int i = j; i < this.exp.size(); i++){
			if(exp.get(i) == Constants.EXP_PAREN_CLOSE){
				exp.remove(i);
				break;
			}
		}
	}

}
