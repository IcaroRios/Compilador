package semantic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
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
	private LinkedList<TokenFunction> declaredFunctions;

	public AnalisadorSemantico(){
		this.declaredVariables = new LinkedList<>();
		this.declaredFunctions = new LinkedList<>();
	}

	public void executar(String name, Folha tree, List<Token> listTokens){
		this.tokens = listTokens;
		this.name = name;
		this.tree = tree;
		this.hasError = false;
		this.errors = "";
		this.preCompile();
		this.compile();
		//TODO
		//verificar se funcao chamada foi declarada e quantidade e tipos de parametros corretos
		//verificar se as variaveis atribuidas estao recebendo os tipos corretos
		//TODO SE UMA FUNCAO JA ESTA DECLARADA A ANALISE DA MESMA DEVE SER IGNORADA????
		this.analisar();
		//this.gerarSaida(name);		
		System.out.println("ERRORS:"+errors);
	}	

	//lista variaveis e funcoes, verifica se existem variaveis duplicadas
	private void preCompile(){
		String escopo = Constants.ESCOPO_GLOBAL_ID;//escopo de declaracao da variavel
		for(int i = 0; i < this.tokens.size(); i++){
			//achou uma funcao, atualiza o escopo
			if(tokens.get(i).getLexema().equals("function")){
				i++;				 
				escopo = tokens.get(i).getLexema();			
				LinkedList<TokenId> parametros = new LinkedList<>();
				//int c = 2;
				i+=2;
				while(!tokens.get(i).getLexema().equals(")")){//enquanto n for ')'					
					TokenId aux = new TokenId(tokens.get(i).getLexema(), escopo,
							tokens.get(i).getnLinha());
					parametros.add(aux);
					i++;
				}
				i++;
				TokenFunction e = null;
				if(tokens.get(i).getLexema().equals(":")){//se a funcao possui retorno					
					int returnType = this.getTipo(tokens.get(i+2).getLexema());
					e = new TokenFunction(escopo, parametros, tokens.get(i).getnLinha(),
							true, returnType );
					//TODO ID QUE REPRESENTA O RETORNO DA FUNCAO
					//TALVEZ SEJA NECESSARIO MARCAR ESSE MISERAVEL
					TokenId aux =  new TokenId(escopo, escopo, tokens.get(i).getnLinha());
					this.addVariable(aux);
				}else{//se nao possui retorno
					e = new TokenFunction(escopo, parametros, tokens.get(i).getnLinha(),
							false, -1);
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
						TokenId aux = new TokenId(a.getLexema(), escopo, tipo, false, a.getnLinha());
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
		///*
		for(TokenId tokenId : declaredVariables){
			System.out.println(tokenId);
		}
		for(TokenFunction tokenFunction : declaredFunctions){
			System.out.println(tokenFunction);
		}
		//*/
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
				TokenFunction aux = new TokenFunction(a.getLexema(), a.getnLinha());
				//System.out.println("aqui: "+aux);
				if(!this.declaredFunctions.contains(aux)){
					this.escreverErroFuncaoNaoDeclarada(aux);
				}
			}else if(tokens.get(i).getId()==Constants.ID_IDENTIFICADOR){//achar um id
				Token a = tokens.get(i);							
				TokenId aux = new TokenId(a.getLexema(), escopo, a.getnLinha());
				if(!this.declaredVariables.contains(aux)){
					this.escreverErroVariavelNaoDeclarada(aux);
				}
			}
		}

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
