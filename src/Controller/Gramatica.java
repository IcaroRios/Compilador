package Controller;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import Model.Constants;
import Model.RegraGramatica;
import Model.RegraNaoTerminal;
import Model.RegraTerminal;


public class Gramatica {
	private String arquivoGramatica;
	LinkedList<RegraNaoTerminal> regras;
	HashMap<String, RegraNaoTerminal> regrasHM;
	LinkedList<RegraTerminal> terminais;
	
	public Gramatica(String arquivoGramatica){		
		this.arquivoGramatica = arquivoGramatica;
		this.regras = new LinkedList<>();
		this.terminais = new LinkedList<>();
		this.regrasHM = new HashMap<>();
	}
	
	//TODO ler o arquivo de gramática
	public void LerGramatica(){

		BufferedReader br = null;
		FileReader fr = null;
		try {
			fr = new FileReader(arquivoGramatica);
			br = new BufferedReader(fr);
			String linha;
			Boolean geraVazio = false;
			//Para cada linha
			while ((linha = br.readLine()) != null) {
				if(linha.equals("") || linha.charAt(0)=='!'){
					//se for linha vazia ou comentário n fazer nada
				}
				else{
					//System.out.println("linha: "+linha);
					String[] aux = linha.split("::=");
					
					//1° parte da regra, n terminal a ser derivado, 2° parte regras de derivação
					String simbolo = aux[0].trim();
					//System.out.println("tam aux:"+aux.length);
					//System.out.println("símbolo: "+simbolo);					
					//criando o nTerminal da regra
					RegraNaoTerminal nTerminal = new RegraNaoTerminal(simbolo);
					LinkedList<LinkedList<RegraGramatica>> regra = new LinkedList<>();
					LinkedList<RegraGramatica> r = null;
					//obtendo as produções geradas pela regra
					String[] producoes = aux[1].split(" (\\|)"); 		
					//System.out.println("Qtd produções: "+producoes.length);
					for(int cont1 = 0; cont1 < producoes.length; cont1++){
						r = new LinkedList<>();
						//se for produção vazia
						if(producoes[cont1].equals(" ")){
							//System.out.println("\tProdução vazia!!!!!!");
							r.add(new RegraTerminal(Constants.PRODUCAO_VAZIA));
							geraVazio = true;
						}
						else{
							//System.out.println("\tProdução: "+producoes[cont1]);
							//adicionando uma produção
							String[] producao = (producoes[cont1].trim()).split(" ");
												
							for(int cont = 0; cont < producao.length; cont++){
								//se começar com < e terminar com > é nTerminal 
								if(producao[cont].charAt(0)=='<' && 
										producao[cont].charAt(producao[cont].length()-1)=='>' ){
									//System.out.println("\t\tprodução nTerminal "+cont+" - "+producao[cont]);
									//adicionando a nova regra nTerminal							
									r.add(new RegraNaoTerminal(producao[cont].trim()));
								}
								//é terminal
								else{
									RegraTerminal novoTerminal = new RegraTerminal(producao[cont].trim());
									//adicionando na lista de terminais caso n contenha
									if(!terminais.contains(novoTerminal)){
										terminais.add(novoTerminal);
									}
									//adicionando a nova regra terminal
									r.add(novoTerminal);
									//System.out.println("\t\tprodução Terminal "+cont+" - "+producao[cont]);
								}
							}												
						}
						//System.out.println("REGRA "+regra.size());
						regra.add(r);
						
					}
					/*
					for(LinkedList<RegraGramatica> a1 : regra){
						System.out.println("REGRA-----------");
						for(RegraGramatica a2 : a1)
							System.out.println(a2.getSimbolo());
					}
					*/
					//adicionando as regras de produção de um não terminal
					nTerminal.addRegra(regra);
					if(geraVazio){
						geraVazio = false;
						nTerminal.setGeraVazio();
					}					
					regras.add(nTerminal);
					regrasHM.put(nTerminal.getSimbolo(), nTerminal);
				}
				
			}
		} catch (IOException e){//se der merda na leitura
			e.printStackTrace();		
		} finally {//vamos fechar?
			try {
				if (br != null)	br.close();
				if (fr != null) fr.close();
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
	}
	
	//TODO criar os firsts para cada n terminal
	public void CriarFirsts(){
		System.out.println("FIRSTS");
		/*Se o 1° elemento derivado é um nTerminal, este faz parte do conjunto First
		Também pega a regra: se a regra deriva vazio, então vazio faz parte do conjunto 1°*/
		for (RegraNaoTerminal regraNT : regras) {
			for(LinkedList<RegraGramatica> producoes: regraNT.getRegra()){
				if(producoes.getFirst() instanceof RegraTerminal){//se o 1° é um nTerminal
					RegraTerminal aux = (RegraTerminal) producoes.getFirst(); 
					System.out.println("1° é: "+aux.getSimbolo());
					regraNT.addPrimeiro(aux);
					if(!aux.getSimbolo().equals(Constants.PRODUCAO_VAZIA)){
						System.out.println(regraNT.getSimbolo()+" - "+regraNT.getRegra());
					}
					else{
						System.out.println(regraNT.getSimbolo()+" -  PRODUÇÃO VAZIA");
					}
				}
			}
		}
		
		/*se X-> Y1 Y2 Y3 ... YK; 1° ed Y1 está em 1° de X, se 1° Y1 possuir vazio então 1° Y2
		também está em 1° de X e assim por diante*/
		for(RegraNaoTerminal regraNT : regras){
			for(LinkedList<RegraGramatica> producoes: regraNT.getRegra()){				
				for (int i = 0; i < producoes.size(); i++) {				
					//se a produção é um não terminal e gera vazio 
					if(producoes.get(i).isTerminal()== false &&
							regrasHM.get(producoes.get(i).getSimbolo()).getGeraVazio()){
						//adicione a próxima produção ao conjunto 1°
						if(i+1 < producoes.size()){//se n estourar a regra
							RegraNaoTerminal aux = (RegraNaoTerminal) producoes.get(i+1);
							regraNT.addPrimeiro(aux);
							//TODO REVER REGRA DE FIRST, OU TIPO TA ERRADO OU CALCULO DE FIRST
						}
						
					}
				}
				for(RegraGramatica producao : producoes){

				}
			}
		}
		
		
		/*se X -> a ALPHA; a faz parte do conjunto 1° de X*/
	}
	
	public void inserirFirst(RegraNaoTerminal regra, String key, LinkedList<RegraTerminal> firsts){
		HashMap<String, LinkedList<RegraTerminal>> f;
		f = new HashMap<>();
		f.put(key, firsts);
		regra.setFirsts(f);
	}
	
	//TODO criar os follows para cada n terminal
	public void CriarFollows(){
		
	}
	public void printGramatica() {
		System.out.println("-----------------------------------------------------------------");
		System.out.println("Gramática lida do arquivo: "+arquivoGramatica);
		System.out.println("Não Terminais:");
		for(RegraNaoTerminal nTerminal : regras){
			if(nTerminal.getGeraVazio())
				System.out.println("\t"+nTerminal.getSimbolo()+"\t\t\t\t\tGERA VAZIO");
			else
				System.out.println("\t"+nTerminal.getSimbolo());
		}
		System.out.println("----------------------------------------------------");
		System.out.println("Terminais:");
		for(RegraTerminal terminal : terminais){
			System.out.println("\t"+terminal.getSimbolo());
		}
		System.out.println("----------------------------------------------------");
		System.out.println("Regras de Produção:");
		for(RegraNaoTerminal nTerminal : regras){
			System.out.println("\t"+nTerminal.toString());
		}
	}
}
