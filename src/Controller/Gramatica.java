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
		////////////////////////////////ADICIONANDO TERMINAIS E NTERMINAIS
		try {
			fr = new FileReader(arquivoGramatica);
			br = new BufferedReader(fr);
			String linha;
			//Para cada linha
			while ((linha = br.readLine()) != null) {
				if(linha.equals("") || linha.charAt(0)=='!'){
					//se for linha vazia ou comentário n fazer nada
				}
				else{
					String[] aux = linha.split("::=");
					RegraNaoTerminal nTerminal = new RegraNaoTerminal(aux[0].trim());	
					//System.out.println("NTERMINAL "+aux[0].trim());
					regras.add(nTerminal);
					regrasHM.put(nTerminal.getSimbolo(), nTerminal);
					//obtendo as producoes geradas pela regra
					String[] producoes = aux[1].split(" (\\|)");					
					for(int cont1 = 0; cont1 < producoes.length; cont1++){
						//olhando uma producao
						String[] producao = (producoes[cont1].trim()).split(" ");
						//System.out.println(producao.length);
						//cadeia vazia tem tam = 1, e n precisa ser analisada
						if(producao.length > 1){
							for(int cont = 0; cont < producao.length; cont++){
								//se comecar com < e terminar com > eh nTerminal							
								if(!(producao[cont].charAt(0)=='<' &&
										producao[cont].charAt(producao[cont].length()-1)=='>' )){
									//adicionando a nova regra nTerminal
								RegraTerminal novoTerminal = 
											new RegraTerminal(producao[cont].trim());
									//adicionando na lista de terminais caso n contenha
									if(!terminais.contains(novoTerminal)){
										terminais.add(novoTerminal);
									}
								}
							}
						}						
					}
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
		///////////////////////////////////ADICIONANDO AS  PRODUCOES////////////
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
					String[] aux = linha.split("::=");
					//1° parte da regra, n terminal a ser derivado, 2° parte regras de derivação
					String simbolo = aux[0].trim();
					//criando o nTerminal da regra
					//RegraNaoTerminal nTerminal = new RegraNaoTerminal(simbolo);
					RegraNaoTerminal nTerminal = regrasHM.get(simbolo);
					LinkedList<LinkedList<RegraGramatica>> regra = new LinkedList<>();
					LinkedList<RegraGramatica> r = null;
					//obtendo as produções geradas pela regra
					String[] producoes = aux[1].split(" (\\|)");
					for(int cont1 = 0; cont1 < producoes.length; cont1++){
						r = new LinkedList<>();
						//se for produção vazia
						if(producoes[cont1].equals(" ")){
							r.add(new RegraTerminal(Constants.PRODUCAO_VAZIA));
							geraVazio = true;
						}
						else{
							//adicionando uma produção
							String[] producao = (producoes[cont1].trim()).split(" ");

							for(int cont = 0; cont < producao.length; cont++){
								//se começar com < e terminar com > é nTerminal
								if(producao[cont].charAt(0)=='<' &&
										producao[cont].charAt(producao[cont].length()-1)=='>' ){
									//adicionando a nova regra nTerminal
									r.add(regrasHM.get(producao[cont].trim()));
								}
								//nTerminal
								else{
									RegraTerminal novoTerminal = new RegraTerminal(producao[cont].trim());
									//adicionando a nova regra terminal
									r.add(novoTerminal);									
								}
							}
						}
						regra.add(r);

					}
					//adicionando as regras de producaoo de um nao terminal
					nTerminal.addRegra(regra);
					if(geraVazio){
						geraVazio = false;
						nTerminal.setGeraVazio();
					}
					//regras.add(nTerminal);
					//regrasHM.put(nTerminal.getSimbolo(), nTerminal);
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
		/*
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
								//nTerminal
								else{
									RegraTerminal novoTerminal = new RegraTerminal(producao[cont].trim());
									//adicionando na lista de terminais caso n contenha
									if(!terminais.contains(novoTerminal)){
										terminais.add(novoTerminal);
									}
									//adicionando a nova regra terminal
									r.add(novoTerminal);
									//System.out.println("\t\tproducao Terminal "+cont+" - "+producao[cont]);
								}
							}
						}
						//System.out.println("REGRA "+regra.size());
						regra.add(r);

					}

					//adicionando as regras de producaoo de um nao terminal
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
		 */
	}

	//TODO criar os firsts para cada n terminal
	public void CriarFirsts(){
		System.out.println("FIRSTS");
		/*Se o 1 elemento derivado eh um nTerminal, este faz parte do conjunto First
		Tambem pega a regra: se a regra deriva vazio, entao vazio faz parte do conjunto 1*/
		for (RegraNaoTerminal regraNT : regras) {
			for(LinkedList<RegraGramatica> producoes: regraNT.getRegra()){
				if(producoes.getFirst() instanceof RegraTerminal){//se o 1 eh  um nTerminal
					RegraTerminal aux = (RegraTerminal) producoes.getFirst();
					regraNT.addPrimeiro(aux);

					System.out.println("1� : "+aux.getSimbolo());					
					if(!aux.getSimbolo().equals(Constants.PRODUCAO_VAZIA)){
						System.out.println(regraNT.getSimbolo()+" - "+regraNT.getRegra());
					}
					else{
						System.out.println(regraNT.getSimbolo()+" -  PRODUCAO VAZIA");
					}
				}
			}
		}
		//assinalando quem j� est� calculado
		for(RegraNaoTerminal regraNT : regras){
			Boolean soTemTerminal = true;
			for(LinkedList<RegraGramatica> producoes: regraNT.getRegra()){
				if(!producoes.getFirst().isTerminal()){
					soTemTerminal = false;
				}
			}
			//se so tem terminal, o 1 ja esta calculado
			if(soTemTerminal){
				regraNT.setfirstEstaPronto();
			}
		}

		for(RegraNaoTerminal regraNT : regras){
			System.out.println("1: "+regraNT.getSimbolo() +" - "+regraNT.getFirstEstaPronto());
			System.out.println("	1: "+regraNT.getSimbolo() +" - "+regraNT.getPrimeiro());
		}

		//atualizando os firsts
		this.atualizarFirsts();
		
		for(RegraNaoTerminal regraNT : regras){
			System.out.println("2: "+regraNT.getSimbolo() +" - "+regraNT.getFirstEstaPronto());
			System.out.println("	2: "+regraNT.getSimbolo() +" - "+regraNT.getPrimeiro());
		}
		
		boolean houveAtualizacao = false;
		int c = 0;
		do {
			System.out.println("AQUIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII: "+c);
			houveAtualizacao = false;
			//atualizarFirsts();
			//assinalando quem esta com o 1 pronto
			for(RegraNaoTerminal regraNT : regras){
				Boolean todosProntos = true;
				for(LinkedList<RegraGramatica> producoes: regraNT.getRegra()){
					//se alguma das 1 producoes n tiver o seu 1 calculado
					if(!producoes .getFirst().getFirstEstaPronto()){
						todosProntos = false;
					}
				}
				//todos os 1 estao prontos, o 1 ja esta calculado. E seu 1 n esta calculado
				if(todosProntos && (regraNT.getFirstEstaPronto() == false)){
					System.out.println(c);
					System.out.println(regraNT);
					System.out.println("\t"+regraNT.getPrimeiro());
					atualizarFirsts();
					regraNT.setfirstEstaPronto();
					houveAtualizacao = true;
				}
			}
			c++;
		} while(houveAtualizacao);
		
		for(RegraNaoTerminal regraNT : regras){
			System.out.println(regraNT.getSimbolo()+" - "+regraNT.getFirstEstaPronto());
			System.out.println("\t"+regraNT.getPrimeiro());
		}
		for(RegraNaoTerminal regraNT : regras){
			if(!regraNT.getFirstEstaPronto())
				System.out.println("FALTA->"+regraNT.getSimbolo());		
		}
		for(RegraNaoTerminal regraNT : regras){
			if(regraNT.getPrimeiro().isEmpty())
				System.out.println("FALTA->"+regraNT.getSimbolo());		
		}
		/*
		for (RegraNaoTerminal regraNaoTerminal : regras) {
			System.out.println(regraNaoTerminal.getSimbolo()+"-"+System.identityHashCode(regraNaoTerminal));
			for (LinkedList<RegraGramatica> a : regraNaoTerminal.getRegra()) {
				for (RegraGramatica regraGramatica : a){
					System.out.println("\t"+regraGramatica.getSimbolo()+"-"+System.identityHashCode(regraGramatica));
				}
			}
		}
		*/		
	}

	private void atualizarFirsts() {
		/*se X -> a ALPHA; a faz parte do conjunto 1 de X*/
		for(RegraNaoTerminal regraNT : regras){//para cada regra
			for(LinkedList<RegraGramatica> producoes: regraNT.getRegra()){//para cada producao
				//se a 1� producao for um nTerminal
				if(producoes.getFirst() instanceof RegraNaoTerminal){
					RegraNaoTerminal primeiraProducao = (RegraNaoTerminal) producoes.getFirst();
					//System.out.println("1REGRA: "+ regraNT);					
					///*
					//se o nTerminal ja estiver com seu 1� calculado
					if(primeiraProducao.getFirstEstaPronto()){
						//System.out.println("2REGRA: "+ regraNT);
						for(RegraTerminal a : primeiraProducao.getPrimeiro()){
						//	System.out.println("\t"+a.getSimbolo());
							regraNT.addPrimeiro(a);
						}
					}
					//*/					
				}
			}
		}
		/*se X-> Y1 Y2 Y3 ... YK; 1 ed Y1 esta em 1 de X, se 1 Y1 possuir vazio entao 1 Y2
		tambem esta� em 1 de X e assim por diante*/
		for(RegraNaoTerminal regraNT : regras){//para cada regra			
			for(int c = 0; c < regraNT.getRegra().size(); c++){
				LinkedList<RegraGramatica> producoes = regraNT.getRegra().get(c); 
				//se o 1� da producao for um nTerminal
				if(producoes.getFirst() instanceof RegraNaoTerminal && producoes.size()>1){					
					//se o 1 nTerminal gera vazio, ent�o a regra[2] faz parte do conjunto 1
					//se o 2 nTerminal gera vazio, ent�o o regra[3] faz parte do conjunto 1
					//e assim por diante
					int cont = 1;					
					boolean addNext = true;
					while(addNext ){
						//se o elemento n-1 gera vazio, add o elemento n
						addPrimeiro(regraNT, producoes.get(cont));
						cont++;
						addNext = false;
						if(cont < producoes.size()){
							if(producoes.get(cont) instanceof RegraNaoTerminal){
								RegraNaoTerminal aux = (RegraNaoTerminal) producoes.get(cont);
								if(aux.getGeraVazio()){
									addNext = true;
								}								
							}							
						}
					}
				}
			}
		}
	}

	public void addPrimeiro(RegraNaoTerminal regra, RegraGramatica r){
		if(r instanceof RegraTerminal){
			RegraTerminal regraT = (RegraTerminal) r;
			regra.addPrimeiro(regraT);
		}
		else if(r instanceof RegraNaoTerminal){
			RegraNaoTerminal regraNT = (RegraNaoTerminal) r;
			if(regraNT.getFirstEstaPronto()){
				for(RegraTerminal regraT : regraNT.getPrimeiro()){
					regra.addPrimeiro(regraT);
				}
			}			
		}
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
		System.out.println("Gramatica lida do arquivo: "+arquivoGramatica);
		System.out.println("Nao Terminais:");
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
		System.out.println("Regras de Producao:");
		for(RegraNaoTerminal nTerminal : regras){
			System.out.println("\t"+nTerminal.toString());
		}
	}
}
