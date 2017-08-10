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

<<<<<<< HEAD
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
					i++;//vai para a 1� variavel
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
					i++;//vai para a 1� variavel
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
=======
public class AnalisadorSemantico {

    private List<Token> tokens;
    private String name;
    private Folha tree;
    private boolean hasError;
    private String errors;
    private LinkedList<TokenId> declaredVariables;
    private LinkedList<TokenId> declaredConstants;
    private LinkedList<TokenFunction> declaredFunctions;

    public AnalisadorSemantico() {
        this.declaredVariables = new LinkedList<>();
        this.declaredConstants = new LinkedList<>();
        this.declaredFunctions = new LinkedList<>();
    }

    public void executar(String name, Folha tree, List<Token> listTokens) {
        this.tokens = listTokens;
        this.name = name;
        this.tree = tree;
        this.hasError = false;
        this.errors = "";
        System.out.println(tree);
        this.preCompile();
        this.compile();
        this.modificarTipo(); // modifico os tipos dos tokens na lista para facilitar a comparação.
        this.verificarAtribuicao();// faz a busca de atribuições e analisa (ERRO APENAS COM BOOLEAN DE MAIS DE 3 MEMBROS
        this.buscarExpression();//faz a busca de if e while e verifica se o resultado é um boolean(erro com mais de 3 membros)
        //TODO
        //verificar se funcao chamada foi declarada e quantidade e tipos de parametros corretos
        //verificar se as variaveis atribuidas estao recebendo os tipos corretos
        //TODO SE UMA FUNCAO JA ESTA DECLARADA A ANALISE DA MESMA DEVE SER IGNORADA????
        this.analisar();
        //this.gerarSaida(name);		
        System.out.println("ERRORS:\n" + errors);
    }

    //lista variaveis e funcoes, verifica se existem variaveis duplicadas
    private void preCompile() {
        String escopo = Constants.ESCOPO_GLOBAL_ID;//escopo de declaracao da variavel
        for (int i = 0; i < this.tokens.size(); i++) {

            //achou uma funcao, atualiza o escopo
            if (tokens.get(i).getLexema().equals("function")) {
                i++;
                escopo = tokens.get(i).getLexema();
                LinkedList<TokenId> parametros = new LinkedList<>();
                //int c = 2;
                i += 3;
                while (!tokens.get(i).getLexema().equals(")")) {//enquanto n for ')'					
                    TokenId aux = new TokenId(tokens.get(i).getLexema(), escopo,
                            this.getTipo(tokens.get(i - 1).getLexema()), false,
                            tokens.get(i).getnLinha());
                    //System.out.println("aqui...  "+tokens.get(i).getLexema());
                    parametros.add(aux);//add o parametro								
                    this.addVariable(aux);//add o parametro como variavel declarada					
                    if (tokens.get(i + 1).getLexema().equals(")")) {
                        i++;
                        break;
                    }
                    i += 3;
                }
                i++;
                TokenFunction e = null;
                //System.out.println(tokens.get(i));
                if (tokens.get(i).getLexema().equals(":")) {//se a funcao possui retorno					
                    int returnType = this.getTipo(tokens.get(i + 2).getLexema());
                    e = new TokenFunction(escopo, parametros, tokens.get(i).getnLinha(),
                            true, returnType);
                    //TODO ID QUE REPRESENTA O RETORNO DA FUNCAO
                    //TALVEZ SEJA NECESSARIO MARCAR ESSE MISERAVEL
                    TokenId aux = new TokenId(escopo, escopo, tokens.get(i).getnLinha());
                    this.addVariable(aux);
                } else {//se nao possui retorno
                    e = new TokenFunction(escopo, parametros, tokens.get(i).getnLinha(),
                            false, -1);
                }
                this.addFunction(e);
            }
            //achar variaveis declaradas e seus tipos
            if (tokens.get(i).getLexema().equals("var")) {//achar um bloco de declaracao
                i++;//pula o var
                i++;//pula o begin
                while (!tokens.get(i).getLexema().equals("end")) {//ate achar um end
                    String t = tokens.get(i).getLexema();//tipo da declaracao
                    int tipo = this.getTipo(t);
                    i++;//vai para a 1� variavel
                    while (!tokens.get(i).getLexema().equals(";")) {//ate achar um ;
                        Token a = tokens.get(i);
                        TokenId aux = new TokenId(a.getLexema(), escopo, tipo, false, a.getnLinha());
                        this.addVariable(aux);
                        //System.out.println(tokens.get(i)+" : "+tipo);
                        i++;
                        if (tokens.get(i).getLexema().equals(",")) {
                            i++;
                        }
                    }
                    if (tokens.get(i + 1).getLexema().equals("end")) {
                        break;
                    } else {
                        i++;
                    }
                }
            }
        }
        ///*
        for (TokenId tokenId : declaredVariables) {
            //System.out.println(tokenId);
        }
        for (TokenFunction tokenFunction : declaredFunctions) {
            //System.out.println(tokenFunction);
        }
        //*/
    }

    //verifica se as variaveis utilizadas foram declaradas e se funcoes utilizadas foram declaradas
    private void compile() {
        String escopo = Constants.ESCOPO_GLOBAL_ID;//escopo de declaracao da variavel
        for (int i = 0; i < this.tokens.size(); i++) {
            //achou uma funcao, atualiza o escopo				
            if (tokens.get(i).getLexema().equals("function")) {
                i++;
                escopo = tokens.get(i).getLexema();
                i++;//pula o id, nome da funcao
            }
            if (tokens.get(i).getId() == Constants.ID_IDENTIFICADOR
                    && tokens.get(i + 1).getLexema().equals("(")) {//achar uma funcao
                Token a = tokens.get(i);
                TokenFunction chamada = new TokenFunction(a.getLexema(), a.getnLinha());
                //System.out.println("aqui: "+aux);
                if (!this.declaredFunctions.contains(chamada)) {//caso nao exista funcao com esse nome				
                    this.escreverErroFuncaoNaoDeclarada(chamada);
                } else {//caso exista funcao com esse nome
                    TokenFunction funcao = this.getFuncaoNome(chamada);
                    this.verificaFuncao(chamada, funcao);
                }
            } else if (tokens.get(i).getId() == Constants.ID_IDENTIFICADOR) {//achar um id
                Token a = tokens.get(i);
                TokenId aux = new TokenId(a.getLexema(), escopo, a.getnLinha());
                //if(!this.declaredVariables.contains(aux)){
                if (!this.containsVariable(aux)) {
                    this.escreverErroVariavelNaoDeclarada(aux);
                }
            }
        }

    }

    private TokenFunction getFuncaoNome(TokenFunction chamada) {
        for (int i = 0; i < declaredFunctions.size(); i++) {
            if (declaredFunctions.get(i).equals(chamada)) {
                return declaredFunctions.get(i);
            }
        }
        return null;
    }

    private void verificaFuncao(TokenFunction chamada, TokenFunction funcao) {
        //TODO DEVE VERIFICAR SE OS PARAMETROS PASSADOS SAO EQUIVALENTES AOS PARAMETROS ESPERADOS PELO
        //ESCOPO DA FUNCAO DECLARADA. USAR A ARVORE???

    }

    private boolean containsVariable(TokenId aux) {
        for (int i = 0; i < declaredVariables.size(); i++) {
            if (aux.myEquals(declaredVariables.get(i))) {
                return true;
            }
        }
        return false;
    }

    private void addVariable(TokenId aux) {
        if (this.declaredVariables.contains(aux)) {
            int i = 0;
            while (!declaredVariables.get(i).equals(aux)) {
                i++;
            }
            this.escreverErroVariavelDuplicada(aux, declaredVariables.get(i));
        } else {
            this.declaredVariables.add(aux);
        }

    }

    private void addFunction(TokenFunction e) {
        if (this.declaredFunctions.contains(e)) {
            int i = 0;
            while (!declaredFunctions.get(i).equals(e)) {
                i++;
            }
            this.escreverErroFuncoesDuplicada(e, declaredFunctions.get(i));
        } else {
            this.declaredFunctions.add(e);
        }
    }

    private boolean isReturnForFunction(TokenId tI, TokenFunction tK) {
        return tI.getNome().equals(tK.getNome());
    }

    private void escreverErroFuncoesDuplicada(TokenFunction e, TokenFunction tF) {
        this.hasError = true;
        this.errors = errors + "On line " + e.getnLinha()
                + "\n\tfunction " + e.getNome() + " is already declared at line " + tF.getnLinha() + "\n";
    }

    private void escreverErroVariavelDuplicada(TokenId aux, TokenId b) {
        this.hasError = true;
        this.errors = errors + "On line " + aux.getnLinha()
                + "\n\tvariable " + aux.getNome() + " is already declared at line " + b.getnLinha() + "\n";
    }

    private void escreverErroVariavelNaoDeclarada(TokenId aux) {
        this.hasError = true;
        this.errors = errors + "On line " + aux.getnLinha()
                + "\n\tvariable " + aux.getNome() + " is not declared\n";
    }

    private void escreverErroFuncaoNaoDeclarada(TokenFunction aux) {
        this.hasError = true;
        this.errors = errors + "On line " + aux.getnLinha()
                + "\n\tfunction " + aux.getNome() + " is not declared\n";
    }

    private void escreverComparacaoTipoNaoPermitido(Token aux, int a, int b) {
        this.hasError = true;
        this.errors = errors + "On line " + aux.getnLinha()
                + "\n\tComparison not allowed between " + getTipo(a) + " and " + getTipo(b) + " \n";
    }

    private void escreverErroExpressaoNaoRetornaBoolean(Token aux) {
        this.hasError = true;
        this.errors = errors + "On line " + aux.getnLinha()
                + "\n\tExpression does not return a boolean\n";
    }

    private void escreverErroAtribuicaoNaoPermitida(Token recebedor, int a, int b) {
        this.hasError = true;
        this.errors = errors + "On line " + recebedor.getnLinha()
                + "\n\tAttribuition not allowed between " + getTipo(a) + " and " + getTipo(b) + " \n";
    }

    private void escreverErroTamanhoAtribuicaoNaoPermitida(Token atual, int a) {
        this.hasError = true;
        this.errors = errors + "On line " + atual.getnLinha()
                + "\n\tAttribuition not allowed to " + getTipo(a) + "\n";
    }

    private void escreverErroOperacaoAtribuicaoNaoPermitida(Token atual, int a) {
        this.hasError = true;
        this.errors = errors + "On line " + atual.getnLinha()
                + "\n\tBad operation in Attribuition to " + getTipo(a) + "\n";
    }

    private int getTipo(String t) {
        int tipo = 0;
        if (t.equals("boolean")) {
            tipo = Constants.TIPO_BOOLEAN;
        } else if (t.equals("string")) {
            tipo = Constants.TIPO_STRING;
        } else if (t.equals("char")) {
            tipo = Constants.TIPO_CHAR;
        } else if (t.equals("integer")) {
            tipo = Constants.TIPO_INTEGER;
        } else if (t.equals("real")) {
            tipo = Constants.TIPO_REAL;
        }
        return tipo;
    }

    private String getTipo(int i) {
        String a = "";
        if (i == 0) {
            a = "integer";
        } else if (i == 1) {
            a = "real";
        } else if (i == 2) {
            a = "boolean";
        } else if (i == 3) {
            a = "string";
        } else if (i == 4) {
            a = "char";
        }
        return a;
    }

    private void analisar() {
        // TODO Auto-generated method stub

    }

    public void cleanLists() {
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

    private void gerarSaida(String arquivo) {
        try {
            File pasta = new File(Constants.PASTA_SAIDA_SEM);
            pasta.mkdir();
            File n = new File(pasta.getName() + File.separator
                    + "Out_Sem_" + arquivo.split("\\.")[0] + Constants.ARQUIVO_EXTENSAO_SEM);
            BufferedWriter bw = new BufferedWriter(new FileWriter(n));
            if (this.hasError) {//se houve erros		
                bw.write("------------------------------ERROS SEMANTICOS"
                        + " IDENTIFICADOS------------------------------");
                bw.newLine();
                bw.flush();
                bw.write(this.errors);
            } else {//se nao houve erros				
                bw.write("SUCESSO NA ANALISE SEMANTICA DO ARQUIVO: " + arquivo);
                System.out.println("Analise Semantica para o arquivo: " + arquivo + ": Sucesso.");
            }
            bw.close();
        } catch (IOException ex) {
            System.out.println("Deu merda na escrita do arquivo."
                    + "\nVerifique as permissoes de execucao do codigo.");
        }
    }

    private void modificarTipo() {
        for (Token atual : tokens) {
            if (atual.getId() == Constants.ID_NUMERO) {
                atual.setTipo(1);
            } else if (atual.getLexema().equals("true") || atual.getLexema().equals("false")) {
                atual.setTipo(2);
            } else if (atual.getId() == Constants.ID_CADEIA_DE_CARACTERES) {
                atual.setTipo(3);
            } else if (atual.getId() == Constants.ID_CARACTERE) {
                atual.setTipo(4);
            }

        }
    }

    private int buscarTipo(Token atual) {
        for (TokenId atualId : declaredVariables) {
            if (atual.getId() == Constants.ID_IDENTIFICADOR) {
                if (atual.getLexema().equals(atualId.getNome())) {
                    //System.out.println(atual+"  "+ atualId.getTipo());
                    return atualId.getTipo();

                }
            }
        }
        /**
         * nao sei se está certo devido a um erro na parte de declaracao.
         */
        if (atual.getId() == Constants.ID_OP_ARITMETICO
                || atual.getId() == Constants.ID_OP_LOGICO
                || atual.getId() == Constants.ID_OP_RELACIONAL /*||atual.getId()== Constants.ID_PALAVRA_RESERVADA
                 */) {
            return atual.getId();
        }
        return atual.getTipo();
    }

    private void buscarExpression() {
        String escopo = Constants.ESCOPO_GLOBAL_ID;
        LinkedList<Token> expression = new LinkedList<>();
        //System.out.println("começando a verificar ex");
        for (int i = 0; i < this.tokens.size(); i++) {
            //verificar dentro de fi e while
            //System.out.println(tokens.get(i));                    
            if ((tokens.get(i).getLexema().equals("if") || tokens.get(i).getLexema().equals("while"))
                    && tokens.get(i).getId() != Constants.PAL_RES_READ && i < tokens.size() - 1
                    && tokens.get(i + 1).getLexema().equals("(")) {

                //System.out.println(tokens.get(i + 2));
                while (!tokens.get(i + 3).getLexema().equals("then")) {
                    expression.add(tokens.get(i + 2));
                    i++;
                }
                int resultado = verificarExpression(expression, escopo);
                if (resultado != Constants.TIPO_BOOLEAN) {
                    escreverErroExpressaoNaoRetornaBoolean(tokens.get(i));
                } else {
                    /**
                     * FAZER A REMOÇÃO DA LISTA expression DE tokens
                     */
                    expression.clear();
                }

            }
        }
    }

    /**
     * este metodo está fazendo as verificaçções
     *
     * @param Expression
     * @param escopo
     * @return
     */
    private int verificarExpression(LinkedList<Token> expression, String escopo) {
        LinkedList<Token> subExpression = new LinkedList<>();
        if (expression.size() == 3) {
            //System.out.println("cheguei no menor possível");

            int tipoPrimeiroMembro = buscarTipo(expression.get(0));
            //System.out.println(Expression.get(0) + "  " + tipoPrimeiroMembro);
            int tipoSegundoMembro = buscarTipo(expression.get(2));
            //System.out.println(Expression.get(2) + "  " + tipoSegundoMembro);
            int tipoOperador = expression.get(1).getId();
            Token operador = expression.get(1);
            //System.out.println(Expression.get(1).getLexema());
            /**
             * VERIFICACAO NUMERO
             */
            if (tipoPrimeiroMembro == Constants.TIPO_INTEGER || tipoPrimeiroMembro == Constants.TIPO_REAL) {// OPERACOES INT FLOAT
                //é integer, entao pode fazer operacoes com integer e float
                if (tipoSegundoMembro == Constants.TIPO_INTEGER || tipoSegundoMembro == Constants.TIPO_REAL) {
                    if (tipoOperador == Constants.ID_OP_RELACIONAL) {//relacional
                        return Constants.TIPO_BOOLEAN; // a comparação é permitida e o resultado é um boolean
                    } else if (tipoOperador == Constants.ID_OP_ARITMETICO) {//se é aritmetica...
                        return Constants.TIPO_REAL;
                    } else if (tipoOperador == Constants.ID_OP_LOGICO) {//não é permitido comparacao logica com inteiros
                        escreverComparacaoTipoNaoPermitido(operador, tipoPrimeiroMembro, tipoSegundoMembro);
                    }

                } else {
                    escreverComparacaoTipoNaoPermitido(operador, tipoPrimeiroMembro, tipoSegundoMembro);
                }
            }
            /**
             * VERIFICACAO DE STRINGS E CHARS
             */
            if (tipoPrimeiroMembro == Constants.TIPO_STRING || tipoPrimeiroMembro == Constants.TIPO_CHAR) {
                if (tipoSegundoMembro == Constants.TIPO_STRING || tipoSegundoMembro == Constants.TIPO_CHAR) {
                    if (tipoOperador == Constants.ID_OP_ARITMETICO) {
                        if (operador.getLexema().equals("+")) {
                            // a unica operacao aritmetica permitida com string e char é +
                            return Constants.TIPO_STRING;
                        } else {
                            escreverComparacaoTipoNaoPermitido(operador, tipoPrimeiroMembro, tipoSegundoMembro);
                        }
                    } else if (tipoOperador == Constants.ID_OP_RELACIONAL) {
                        return Constants.TIPO_BOOLEAN;
                    } else {
                        escreverComparacaoTipoNaoPermitido(operador, tipoPrimeiroMembro, tipoSegundoMembro);
                    }

                } else {
                    escreverComparacaoTipoNaoPermitido(operador, tipoPrimeiroMembro, tipoSegundoMembro);
                }
            } /**
             * BOOLEAN este deve ser a ultima verificação
             */
            else if ((tipoPrimeiroMembro == Constants.TIPO_BOOLEAN || tipoSegundoMembro == Constants.TIPO_BOOLEAN)
                    && (tipoOperador != Constants.ID_OP_LOGICO && (!operador.getLexema().equals("==")
                    && !operador.getLexema().equals("!=")))) {
                System.out.println(tipoPrimeiroMembro);
                System.out.println(Constants.TIPO_BOOLEAN);
                escreverComparacaoTipoNaoPermitido(operador, tipoPrimeiroMembro, tipoSegundoMembro);
            } else {
                return 2;
            }
            /*            else if(tipoPrimeiroMembro == 3||tipoPrimeiroMembro == 4 ){
             if (tipoSegundoMembro != 0 || tipoSegundoMembro == 1) {
            
             }
             }*/

            //se permite, e depois retorna um token com o tipo resultante.
        } /**
         * FALTA FAZER A VERIFICAÇÃO DE O TAMANHO É 2, NO CASO DOS UNÁRIOS.
         */
        else {
            for (int i = 0; i < expression.size(); i++) {

                /*
                 e achar um parênteses, deve montar uma sublista e enviar tudo que tem dentro para ele mesmo, recursivamente
                 */
                if (expression.get(i).getLexema().equals("(")) {
                    while (!expression.get(i + 1).getLexema().equals(")")) {
                        subExpression.add(expression.get(i + 1));
                        i++;
                    }
                    return verificarExpression(subExpression, escopo);
                }

            }
        }
        return -1;
    }

    private void verificarAtribuicao() {
        LinkedList<Token> expression = new LinkedList<>();
        for (int i = 0; i < this.tokens.size(); i++) {
            if (tokens.get(i).getId() == Constants.ID_IDENTIFICADOR && tokens.get(i + 1).getLexema().equals("=")) {
                expression.add(tokens.get(i));
                //System.out.println(tokens.get(i));
                boolean funcao = false;
                while (!(tokens.get(i + 2).getLexema().equals(",")) && !(tokens.get(i + 2).getLexema().equals(";"))) {
                    //System.out.println(tokens.get(i + 2));
                    //expression.add(tokens.get(i + 2));
                    if (tokens.get(i + 2).getLexema().equals("(")) {
                        funcao = true;
                    }
                    if (funcao == false) {
                        expression.add(tokens.get(i + 2));

                    } else if (tokens.get(i + 2).getLexema().equals(")") && funcao == true) {
                        funcao = false;
                    }
                    i++;
                }

                analisarAtribuicao(expression);
                expression.clear();
            }
        }

    }

    private void analisarAtribuicao(LinkedList<Token> expression) {
        int tipoPrimeiroMembro = buscarTipo(expression.get(0));
        //System.out.println("tipo do primeiro membro é: " + tipoPrimeiroMembro);
        for (Token atual : expression) {
            int tipoAtual = buscarTipo(atual);
            //System.out.println(atual.getLexema() + "  "+ tipoAtual);                       

            if (tipoPrimeiroMembro == Constants.TIPO_INTEGER || tipoPrimeiroMembro == Constants.TIPO_REAL) {
                if (tipoAtual == Constants.TIPO_INTEGER || tipoAtual == Constants.TIPO_REAL) {
                    /*System.out.println(" TIPOS EQUIVALENTES = " + expression.get(0) + "tipo: " + tipoPrimeiroMembro
                     + atual + " tipo " + tipoAtual);*/
                } //COM INTEGER E REAL SÓ SAO PERMITIDOS OPERAÇÕES ARITIMETICAS
                else if (tipoAtual == Constants.ID_OP_ARITMETICO) {//VERIFICAR O PORQUE NAO ESTA LENDO TRUE E FALSE
                    //System.out.println("operador = " + atual);
                    if (atual.getLexema().equals("true") || atual.getLexema().equals("false")) {
                        escreverErroAtribuicaoNaoPermitida(expression.get(0), tipoPrimeiroMembro, tipoAtual);
                    }
                } else {
                    escreverErroAtribuicaoNaoPermitida(expression.get(0), tipoPrimeiroMembro, tipoAtual);
                }
            } else if (tipoPrimeiroMembro == Constants.TIPO_CHAR) {
                //System.out.println("é um char");
                if (expression.size() > 2) {
                    escreverErroTamanhoAtribuicaoNaoPermitida(atual, tipoPrimeiroMembro);
                    return;
                }
                if (tipoAtual != Constants.TIPO_CHAR) {

                    if (tipoAtual == 1) {// ainda não sei se é inteiro ou Real
                        char a[] = atual.getLexema().toCharArray();
                        boolean temPonto = false;
                        for (int i = 0; i < a.length; i++) {
                            temPonto = a[i] == '.';//feliz por saber que isso funciona kkkkk

                        }
                        if (!temPonto) {

                            tipoAtual = 0;

                        }
                    }
                    escreverErroAtribuicaoNaoPermitida(atual, tipoPrimeiroMembro, tipoAtual);

                }
            } else if (tipoPrimeiroMembro == Constants.TIPO_STRING) {
                if (!((tipoAtual == Constants.TIPO_STRING) || (tipoAtual == Constants.TIPO_CHAR))) {
                    if (tipoAtual == Constants.ID_OP_ARITMETICO) {
                        if (!atual.getLexema().equals("+")) {
                            escreverErroOperacaoAtribuicaoNaoPermitida(atual, tipoPrimeiroMembro);
                        }
                    } else {
                        escreverErroAtribuicaoNaoPermitida(expression.get(0), tipoPrimeiroMembro, tipoAtual);
                    }
                }
                if (atual.getLexema().equals("<=")
                        || atual.getLexema().equals(">=")
                        || atual.getLexema().equals("!=")
                        || atual.getLexema().equals(">")
                        || atual.getLexema().equals("<")
                        || atual.getLexema().equals("==")) {
                    escreverErroOperacaoAtribuicaoNaoPermitida(atual, tipoPrimeiroMembro);
                    return;
                }

            } else if (tipoPrimeiroMembro == Constants.TIPO_BOOLEAN) {
                /**
                 * Fiz uma cópia da lista já que não pode ser modificada, e
                 * removi o primeiro membro para que eu possa utilizar o método
                 * que verifica dentro de if e while.
                 */
                LinkedList<Token> modExpression = (LinkedList<Token>) expression.clone();
                modExpression.remove(0);
                int resultado = verificarExpression(modExpression, null);
                if (resultado != Constants.TIPO_BOOLEAN) {
            // se o resultado não é um boolean, lança o erro.

                    escreverErroExpressaoNaoRetornaBoolean(tokens.get(0));
                }
            }
            /*
             System.out.println("operador ou diferente= " + atual+"\n "
             + "AGORA VERIFICAR SE É UM OPERADOR PERMITIDO OU LANÇAR ERRO");*/

        }
        //System.out.println("fim");
    }
>>>>>>> 66b60a27532c8f2bac2f665db440379249d97bb0
}
