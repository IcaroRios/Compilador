package semantic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import exceptions.FunctionWithoutReturnStatement;
import Model.Constants;
import Model.Folha;
import Model.Token;

public class AnalisadorSemantico{
	private List<Token> tokens;
	private String name;
	private Folha tree;
	private boolean hasError;
	private String errors;
	private LinkedList<TokenId> declaredVariables;
	private LinkedList<TokenId> declaredConstants;
	private LinkedList<TokenFunction> declaredFunctions;

	public AnalisadorSemantico(){
		this.declaredVariables = new LinkedList<>();
		this.declaredConstants = new LinkedList<>();
		this.declaredFunctions = new LinkedList<>();
	}

	public void executar(String name, Folha tree, List<Token> listTokens){
		this.tokens = listTokens;
		this.name = name;
		this.tree = tree;
		this.hasError = false;
		this.errors = "";
		System.out.println(tree);
		this.preCompile();
		this.compile();
		//TODO
		//verificar se funcao chamada foi declarada e quantidade e tipos de parametros corretos
		//verificar se as variaveis atribuidas estao recebendo os tipos corretos
		//COLOCAR WARNINGS:
		//	VARIAVEL E CONSTANTE DECLARADA MAS NUNCA USADA
		//	CONSTANTE DECLARADA MAS NAO INICIALIZADA
		this.analisar();
		this.functionHasReturn();
		//this.gerarSaida(name);
		///*
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
								isArray,false,1);
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
							tokens.get(i).getnLinha(), isArray, true,1);
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
				System.out.println(tokens.get(i));
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
						int size = 0;
						if(tokens.get(i+1).getLexema().equals("[")){//se e um array
							aux = new TokenId(a.getLexema(), escopo, tipo, false, a.getnLinha(),
									true, true, size);
							//TODO terminar o array aqui
						}else{//nao e um array
							aux = new TokenId(a.getLexema(), escopo, tipo, false, a.getnLinha(),
									false, true, 1);
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
						System.out.println("set "+b.getNome());
					}
				}
			}
		}

	}					

	//verifica se as funcoes tem retorno, e se seus tipos estao certos
	private void functionHasReturn(){
		for(TokenFunction tkF : declaredFunctions){
			//System.out.println(tkF.getNome());			
			//System.out.println(tokens.get(tkF.getTokenPosition()));
			if(tkF.hasReturn()){//caso a funcao tenha retorno
				int index = tkF.getTokenPosition();
				/*
				//se tem um retorno antes mesmo de um if
				try{
					if(!this.hasReturnBeforeIf(index)){
						hasReturnForAllFlows(index, tkF);
					}

				} catch(FunctionWithoutReturnStatement e){
					escreverErroFuncaoDeviaTerRetorno(tkF);
				}
				 */
				hasReturnForAllFlows(index, tkF);
			}else{//caso a funcao nao tenha retorno
				int j = hasFunctionReturn(tkF);
				//System.out.println(j);
				//System.out.println(tokens.get(j));				
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

	/*
	private boolean hasReturnBeforeIf(int i) throws FunctionWithoutReturnStatement{
		while(i<tokens.size()){
			if(tokens.get(i).getLexema().equals("return")){
				return true;
			}else if(tokens.get(i).getLexema().equals("if")){
				return false;
			}else if(tokens.get(i).getLexema().equals("function")){
				throw new FunctionWithoutReturnStatement();
			}
			i++;
		}
		//chegou ao fim dos tokens
		throw new FunctionWithoutReturnStatement();
	}
	 */
	private TokenFunction getFuncaoNome(TokenFunction chamada) {
		for(int i = 0; i < declaredFunctions.size(); i++){
			if(declaredFunctions.get(i).equals(chamada)){
				return declaredFunctions.get(i);
			}
		}
		return null;
	}

	private TokenId getIdNome(TokenId obj){
		for(int i = 0; i < declaredVariables.size(); i++){
			if(declaredVariables.get(i).equals(obj)){
				return declaredVariables.get(i);
			}
		}
		return null;
	}

	private void verificaFuncao(TokenFunction chamada, TokenFunction funcao) {
		//TODO DEVE VERIFICAR SE OS PARAMETROS PASSADOS SAO EQUIVALENTES AOS PARAMETROS ESPERADOS PELO
		//ESCOPO DA FUNCAO DECLARADA. USAR A ARVORE???

	}

	private boolean containsVariable(TokenId aux) {
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

	private boolean isReturnForFunction(TokenId tI, TokenFunction tK){
		return tI.getNome().equals(tK.getNome());
	}

	private void escreverErroFuncaoDeviaTerRetorno(TokenFunction e){
		this.hasError = true;
		this.errors = errors+ "On line "+e.getnLinha()+
				"\n\tfunction "+e.getNome()+" has no return statement, "+
				" but should have it.\n";		
	}

	private void escreverErroFuncaoNaoDeviaTerRetorno(TokenFunction e, Token tk){
		this.hasError = true;
		this.errors = errors+ "On line "+e.getnLinha()+
				"\n\tfunction "+e.getNome()+" has return statement on "+tk.getnLinha()+
				" but should not have it.\n";		
	}

	private void escreverErroFuncoesDuplicada(TokenFunction e, TokenFunction tF){
		this.hasError = true;
		this.errors = errors+ "On line "+e.getnLinha()+
				"\n\tfunction "+e.getNome()+" is already declared at line "+tF.getnLinha()+"\n";		
	}

	private void escreverErroVariavelDuplicada(TokenId aux, TokenId b){
		this.hasError = true;
		this.errors = errors+ "On line "+aux.getnLinha()+
				"\n\tvariable "+aux.getNome()+" is already declared at line "+b.getnLinha()+"\n";		
	}

	private void escreverErroVariavelNaoDeclarada(TokenId aux){
		this.hasError = true;
		this.errors = errors+ "On line "+aux.getnLinha()+
				"\n\tvariable "+aux.getNome()+" is not declared\n";
	}

	private void escreverErroFuncaoNaoDeclarada(TokenFunction aux){
		this.hasError = true;
		this.errors = errors+ "On line "+aux.getnLinha()+
				"\n\tfunction "+aux.getNome()+" is not declared\n";
	}

	private int getTipo(String t){
		int tipo = 0;
		if(t.equals("boolean")){
			tipo = Constants.TIPO_BOOLEAN;
		}else if(t.equals("string")){
			tipo = Constants.TIPO_STRING;
		}else if(t.equals("char")){
			tipo = Constants.TIPO_CHAR;
		}else if(t.equals("integer")){
			tipo = Constants.TIPO_INTEGER;
		}else if(t.equals("real")){
			tipo = Constants.TIPO_REAL;
		}
		return tipo;
	}

	private void analisar(){
		// TODO Auto-generated method stub

	}

	public void cleanLists(){
		this.declaredVariables.clear();
	}
	/*
	private boolean isDeclared(Token variavel){
		for(HashMap<Token, String> hash : declaredVariables){
			if(hash.containsKey(variavel)){
				return true;
			}
		}		
		return false;
	}
	 */
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
