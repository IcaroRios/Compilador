package syntactic;

import Model.Constants;
import Model.Folha;
import Model.RegraGramatica;
import Model.Token;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import exceptions.ErrorAtFirstGrammar;

public class AnalisadorSintatico {

	//A PILHA VAI SER A PROPRIA PILHA DE EXECUCAO DO CODIGO
	private List<Token> tokens;
	private RegraNaoTerminal primeiraRegra;
	private TokenToRegraGramatica comparador;
	private int cont;
	private boolean hasError;	
	private String errors;
	private Folha arvore;
	

	public AnalisadorSintatico(RegraNaoTerminal primeiraRegra, LinkedList<RegraNaoTerminal> regras,
			TokenToRegraGramatica comparador){               
		this.primeiraRegra = primeiraRegra;
		this.comparador = comparador;
	}

	/*
    se o que tem no topo da pilha e um terminal --- consome		
    se o que tem no topo da pilha e um n terminal --- vai pra matriz e gera a producao dele		
    segue ate a lista de tokens acabar lista de tokens acabou e a pilha esta vazia
     o codigo esta correto sintaticamente, se nao estas errado
	TRATAMENTO DE ERROS
    pegue todos os follows da sua producao e de todos os seus antecessores                        
    se for um nao terminal: ignore tokens ate chegar em um follow desses                        		                      
    se for um terminal: assuma que o token foi inserido e continua
	 */
	public void executar(List<Token> tokens, String fileName){
		this.tokens = tokens;
		this.cont = 0;
		this.hasError = false;
		LinkedList<RegraTerminal> allFollows = new LinkedList<>();
		allFollows.addAll(primeiraRegra.getSeguinte());
		this.arvore = new Folha(primeiraRegra, false);
		this.errors = "";		
		try {
			this.analisar(primeiraRegra, allFollows);
			//caso tenha algo apos o ultimo end
			if(this.cont < tokens.size()){
				this.escreverErroEsperavaFimDeArquivo();
			}
		}catch (ErrorAtFirstGrammar e){
			this.errors = errors+" FATAL ERROR\n RTFM?!\n";
		}		
		this.gerarSaida(fileName);
		//System.out.println("ARVORE: "+this.arvore.toString());
	}

	public void analisar(RegraNaoTerminal r, LinkedList<RegraTerminal> allFollows)
			throws ErrorAtFirstGrammar{
		//System.out.println(r);
		RegraTerminal terminalAtual = comparador.tokenToTerminal(tokens.get(cont));
		Folha folha = new Folha(r, false);
		int p = this.getPosicion(r, terminalAtual, allFollows);		
		if(p == -1){
			//achou como follow, entao pule essa regra
			return;
		}
		//pegando a regra atual
		LinkedList<RegraGramatica> regraAtual = r.getRegra().get(p);
		for(int c = 0; c < regraAtual.size(); c++){
			if(cont == tokens.size()){				
				this.escreverErroFimDeArquivo(regraAtual.get(c));
				break;
			}
			terminalAtual = comparador.tokenToTerminal(tokens.get(cont));
			
			int resp = comparador.compare(terminalAtual, regraAtual.get(c));
			//SE DER CERTO, FAZ NADA, APENAS ANDA
			if(resp == 1){
				folha.addLeaf(new Folha(tokens.get(cont), true));
				allFollows.remove(terminalAtual);				
			}
			//SE DEU ERRADO, FAZER TRATAMENTO DE ERRO
			else if(resp == 0){				
				this.escreverErro(tokens.get(cont), regraAtual.get(c));
				cont--;
			}
			//SE E UM NTERMINAL, VAI PRA REGRA NTERMINAL AGORA
			else if(resp == -1){
				RegraNaoTerminal a = (RegraNaoTerminal) regraAtual.get(c);
				allFollows.addAll(a.getSeguinte());				
				try{
					this.analisar(a, allFollows);
				}catch (ErrorAtFirstGrammar e){
					//se essa regra possui o terminal como follow
					if(r.getSeguinte().contains(e.getRegraTerminal())){
						//continue;
					}else{//se nao, lance novamente ate a regra que possui
						throw e;
					}
				}				
				allFollows.removeAll(a.getSeguinte());
				//retirando a contagem extra do backtracking
				cont--;
			}			
			cont++;
		}
		this.arvore.addLeaf(folha);
	}

	private void escreverErroFimDeArquivo(RegraGramatica regra){
		this.hasError = true;
		//System.out.println("\tEXPECTED: "+regra+"\n\tbut recieved: END OF FILEn\n");
		this.errors = errors+"\tEXPECTED: "+regra+"\n\tbut recieved: END OF FILEn\n";		
	}

	private void escreverErroEsperavaFimDeArquivo(){
		this.hasError = true;
		//System.out.println("\tEXPECTED: END OF FILE \n\tbut recieved(on line "
		//		+tokens.get(cont).getnLinha()+"): ");
		this.errors = errors+"\tEXPECTED: END OF FILE \n\tbut recieved(on line "
				+tokens.get(cont).getnLinha()+"): ";
		while(cont < tokens.size()){
			this.errors = errors+"'"+tokens.get(cont)+"' ";
			cont++;
		}
	}
	
	private void escreverErro(Token token, RegraGramatica regra){
		this.hasError = true;
		//System.out.println("On line: "+token.getnLinha()+
		//		"\n\tEXPECTED: "+regra+"\n\tbut recieved: "+
		//		token.getLexema()+"\n\n");
		this.errors = errors+"On line: "+token.getnLinha()+
				"\n\tEXPECTED: "+regra+"\n\tbut recieved: "+
				token.getLexema()+"\n\n";
	}
	
	private void escreverErroRegra(Token token, LinkedList<RegraTerminal> regra){
		this.hasError = true;
		//System.out.println("On line: "+token.getnLinha()+"\n\tEXPECTED: "+regra+
		//		"\n\tbut recieved: "+token.getLexema()+"\n\n");
		this.errors = errors+"On line: "+token.getnLinha()+"\n\tEXPECTED: ";
		for(RegraTerminal regraTerminal : regra){
			this.errors= errors+" "+regraTerminal;
		}
		this.errors = errors+"\n\tbut recieved: "+token.getLexema()+"\n\n";
	}
	
	private int getPosicion(RegraNaoTerminal r, RegraTerminal terminalAtual,
			LinkedList<RegraTerminal> allFollows) throws ErrorAtFirstGrammar{
		int posicaoRegra = 0;
		//se retornar null eh pq o simbolo n faz parte do conjunto FIRST
		if(r.getPrimeiroHM().get(terminalAtual.getSimbolo()) == null){
			//se o nTerminal possui o simbolo como follow
			//if(r.getSeguinte().contains(terminalAtual)){
			if(r.getSeguinte().contains(terminalAtual) && r.getGeraVazio()){
				posicaoRegra = -1;
			}//nao esta no first e nem no follow deste
			else{//se deu erro no token 0 -> program, continue a analise, e registra o erro depois
				if(cont==0){
					posicaoRegra = 0;					
				}else{
					LinkedList<RegraTerminal> a = new LinkedList<>();
					a.addAll(r.getPrimeiro());
					if(r.getGeraVazio()){										
						a.addAll(r.getSeguinte());				
					}
					a.remove(new RegraTerminal(Constants.PRODUCAO_VAZIA));
					this.escreverErroRegra(tokens.get(cont), a);
					RegraTerminal aux = comparador.tokenToTerminal(tokens.get(cont));
					while(cont < tokens.size()-1 &&
							!allFollows.contains(aux) &&						
							!r.getPrimeiro().contains(aux)){		
						cont++;
						aux = comparador.tokenToTerminal(tokens.get(cont));						
					}
					//se achar no first, continue dessa regra mesmo
					if(r.getPrimeiro().contains(aux)){
						aux = comparador.tokenToTerminal(tokens.get(cont));
						posicaoRegra = r.getPrimeiroHM().get(aux.getSimbolo());
					}else if(r.getSeguinte().contains(aux)){
						posicaoRegra = -1;
					}
					//se achou no allFollows, retorne ate a regra que tem esse token como follow
					else if(allFollows.contains(aux)){
						throw new ErrorAtFirstGrammar(aux);
					}					
				}				
			}
		//ACHOU NO FIRST 
		}else{
			posicaoRegra = r.getPrimeiroHM().get(terminalAtual.getSimbolo());
		}
		return posicaoRegra;
	}
	
	public boolean getHasError(){
		return this.hasError;
	}
	
	private void gerarSaida(String arquivo){
		try {
			File pasta = new File(Constants.PASTA_SAIDA_SIN);
			pasta.mkdir();
			File n = new File(pasta.getName() + File.separator +
					"Out_Syn_"+arquivo.split("\\.")[0]+ Constants.ARQUIVO_EXTENSAO_SIN);			
			BufferedWriter bw = new BufferedWriter(new FileWriter(n));			
			if(this.hasError){//se houve erros		
				bw.write("------------------------------ERROS SINTATICOS IDENTIFICADOS------------------------------");
				bw.newLine();
				bw.flush();
				bw.write(this.errors);
			}else{//se nao houve erros				
				bw.write("SUCESSO NA ANALISE SINTATICA DO ARQUIVO: "+arquivo);
				System.out.println("Analise Sintatica para o arquivo: " + arquivo + ": Sucesso.");
			}
			bw.close();			
		} catch (IOException ex) {
			System.out.println("Deu merda na escrita do arquivo."
					+ "\nVerifique as permissoes de execucao do codigo.");
		}
	}
	
	public Folha getTree(){
		return this.arvore;
	}
	
	public void cleanLists() {
		this.hasError = false;
		this.errors = "";
	}
	
}
