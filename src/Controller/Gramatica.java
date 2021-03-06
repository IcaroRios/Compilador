package Controller;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import syntactic.RegraNaoTerminal;
import syntactic.RegraTerminal;
import exceptions.GramaticaIsNotSimplified;
import exceptions.GramaticaTem2FirstNaMesmaRegra;
import exceptions.RuleHasNoFirstException;
import exceptions.RuleHasNoFollowException;
import Model.Constants;
import Model.RegraGramatica;


public class Gramatica {
	private String arquivoGramatica;
	LinkedList<RegraNaoTerminal> regras;
	HashMap<String, RegraNaoTerminal> regrasHM;
	LinkedList<RegraTerminal> terminais;	
	RegraTerminal fimDeLinha;

	public Gramatica(String arquivoGramatica){		
		this.arquivoGramatica = arquivoGramatica;
		this.regras = new LinkedList<>();
		this.terminais = new LinkedList<>();
		this.regrasHM = new HashMap<>();
		this.fimDeLinha = new RegraTerminal(Constants.PRODUCAO_FIM_DO_ARQUIVO);
	}

	public void lerGramatica(){
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
					//se for linha vazia ou comentario n fazer nada
				}
				else{
					String[] aux = linha.split("::=");
					RegraNaoTerminal nTerminal = new RegraNaoTerminal(aux[0].trim());	
					regras.add(nTerminal);
					regrasHM.put(nTerminal.getSimbolo(), nTerminal);
					//obtendo as producoes geradas pela regra
					String[] producoes = aux[1].split(" (\\|)");					
					for(int cont1 = 0; cont1 < producoes.length; cont1++){
						//olhando uma producao
						String[] producao = (producoes[cont1].trim()).split(" ");
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
					//se for linha vazia ou comentario n fazer nada
				}
				else{
					String[] aux = linha.split("::=");
					//1 parte da regra, n terminal a ser derivado, 2 parte regras de derivacao
					String simbolo = aux[0].trim();
					//criando o nTerminal da regra
					RegraNaoTerminal nTerminal = regrasHM.get(simbolo);
					LinkedList<LinkedList<RegraGramatica>> regra = new LinkedList<>();
					LinkedList<RegraGramatica> r = null;
					//obtendo as produções geradas pela regra
					String[] producoes = aux[1].split(" (\\|)");
					for(int cont1 = 0; cont1 < producoes.length; cont1++){
						r = new LinkedList<>();
						//se for producao vazia
						if(producoes[cont1].equals(" ")){
							r.add(new RegraTerminal(Constants.PRODUCAO_VAZIA));
							geraVazio = true;
						}
						else{
							//adicionando uma producao
							String[] producao = (producoes[cont1].trim()).split(" ");

							for(int cont = 0; cont < producao.length; cont++){
								//se comecar com < e terminar com > e nTerminal
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

									//adicionando na lista de terminais caso n contenha									
									if(!terminais.contains(novoTerminal)){
										terminais.add(novoTerminal);
									}
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


	public void criarFirsts() throws RuleHasNoFirstException{		
		/*Se o 1 elemento derivado eh um nTerminal, este faz parte do conjunto First
		Tambem pega a regra: se a regra deriva vazio, entao vazio faz parte do conjunto 1*/
		for (RegraNaoTerminal regraNT : regras) {
			int a = 0;			
			for(LinkedList<RegraGramatica> producoes: regraNT.getRegra()){
				if(producoes.getFirst() instanceof RegraTerminal){//se o 1 eh  um nTerminal
					RegraTerminal aux = (RegraTerminal) producoes.getFirst();
					regraNT.addPrimeiro(aux);
					regraNT.addPrimeiroHM(aux.getSimbolo(), a);										
				}
				a++;
			}
		}
		//assinalando quem ja esta calculado
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

		//atualizando os firsts
		this.atualizarFirsts();

		//atualiza firsts e os recalcula ate que n hajam mais alteracoes nos conjuntos
		boolean houveAtualizacao = false;
		do {
			houveAtualizacao = false;
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
					atualizarFirsts();
					regraNT.setfirstEstaPronto();
					houveAtualizacao = true;
				}
			}
		} while(houveAtualizacao);

		for(RegraNaoTerminal regraNT : regras){
			if(!regraNT.getFirstEstaPronto() || regraNT.getPrimeiro().isEmpty())
				throw new RuleHasNoFirstException(regraNT.getSimbolo());
		}

	}
	/*
	private void atualizarFirsts() {
		//se X -> a ALPHA; a faz parte do conjunto 1 de X
		for(RegraNaoTerminal regraNT : regras){//para cada regra
			int a = 0;
			for(LinkedList<RegraGramatica> producoes: regraNT.getRegra()){//para cada producao
				//se a 1 producao for um nTerminal
				if(producoes.getFirst() instanceof RegraNaoTerminal){
					RegraNaoTerminal primeiraProducao = (RegraNaoTerminal) producoes.getFirst();
					//se o nTerminal ja estiver com seu 1 calculado
					if(primeiraProducao.getFirstEstaPronto()){
						for(RegraTerminal aux : primeiraProducao.getPrimeiro()){
							regraNT.addPrimeiro(aux);
							regraNT.addPrimeiroHM(aux.getSimbolo(), a);					
						}
					}
				}
				a++;
			}
		}
		//se X-> Y1 Y2 Y3 ... YK; 1 ed Y1 esta em 1 de X, se 1 Y1 possuir vazio entao 1 Y2
		//tambem esta em 1 de X e assim por diante
		for(RegraNaoTerminal regraNT : regras){//para cada regra
			int a = 0;
			for(int c = 0; c < regraNT.getRegra().size(); c++){
				LinkedList<RegraGramatica> producoes = regraNT.getRegra().get(c); 
				//se tem mais de uma producao e o 1� da producao for um nTerminal
				if(producoes.size() > 1 && producoes.getFirst() instanceof RegraNaoTerminal){					
					RegraNaoTerminal f = (RegraNaoTerminal) producoes.getFirst();
					//se o 1 gerar vazio entra na regra
					if(f.getGeraVazio()){
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
								//se o elemento n-1 e nTerminal e o elemento n terminal
								else if(producoes.get(cont) instanceof RegraTerminal){
									//add como 1 e termina o ciclo
									RegraTerminal aux = (RegraTerminal) producoes.get(cont);
									regraNT.addPrimeiro(aux);
									regraNT.addPrimeiroHM(aux.getSimbolo(), a);					
									a++;
									addNext = false;
								}
							}
						}
					}					
				}
				a++;
			}
		}
	}
	*/
	private void atualizarFirsts() {
		/*se X -> a ALPHA; a faz parte do conjunto 1 de X*/
		for(RegraNaoTerminal regraNT : regras){//para cada regra
			int a = 0;
			for(LinkedList<RegraGramatica> producoes: regraNT.getRegra()){//para cada producao
				//se a 1 producao for um nTerminal
				if(producoes.getFirst() instanceof RegraNaoTerminal){
					RegraNaoTerminal primeiraProducao = (RegraNaoTerminal) producoes.getFirst();
					//se o nTerminal ja estiver com seu 1 calculado
					if(primeiraProducao.getFirstEstaPronto()){
						for(RegraTerminal aux : primeiraProducao.getPrimeiro()){
							regraNT.addPrimeiro(aux);
							regraNT.addPrimeiroHM(aux.getSimbolo(), a);					
						}
					}
				}
				a++;
			}
		}
		/*se X-> Y1 Y2 Y3 ... YK; 1 ed Y1 esta em 1 de X, se 1 Y1 possuir vazio entao 1 Y2
		tambem esta em 1 de X e assim por diante*/
		for(RegraNaoTerminal regraNT : regras){//para cada regra
			int a = 0;
			for(int c = 0; c < regraNT.getRegra().size(); c++){
				LinkedList<RegraGramatica> producoes = regraNT.getRegra().get(c); 
				//se tem mais de uma producao e o 1� da producao for um nTerminal
				if(producoes.size() > 1 && producoes.getFirst() instanceof RegraNaoTerminal){					
					RegraNaoTerminal f = (RegraNaoTerminal) producoes.getFirst();
					//se o 1 gerar vazio entra na regra
					if(f.getGeraVazio()){
						int cont = 1;
						boolean addNext = true;
						while(addNext ){
							//se o elemento n-1 gera vazio, add o elemento n
							RegraGramatica r =producoes.get(cont); 
							if(r instanceof RegraTerminal){
								RegraTerminal regraT = (RegraTerminal) r;
								regraNT.addPrimeiro(regraT);
								regraNT.addPrimeiroHM(regraT.getSimbolo(), a);
							}
							else if(r instanceof RegraNaoTerminal){
								RegraNaoTerminal rn = (RegraNaoTerminal) r;
								if(rn.getFirstEstaPronto()){
									for(RegraTerminal regraT : rn.getPrimeiro()){
										rn.addPrimeiro(regraT);
										regraNT.addPrimeiroHM(regraT.getSimbolo(), a);
									}
								}			
							}
							cont++;
							addNext = false;
							if(cont < producoes.size()){
								if(producoes.get(cont) instanceof RegraNaoTerminal){
									RegraNaoTerminal aux = (RegraNaoTerminal) producoes.get(cont);
									if(aux.getGeraVazio()){
										addNext = true;
									}								
								}
								//se o elemento n-1 e nTerminal e o elemento n terminal
								else if(producoes.get(cont) instanceof RegraTerminal){
									//add como 1 e termina o ciclo
									RegraTerminal aux = (RegraTerminal) producoes.get(cont);
									regraNT.addPrimeiro(aux);
									regraNT.addPrimeiroHM(aux.getSimbolo(), a);					
									a++;
									addNext = false;
								}
							}
						}
					}					
				}
				a++;
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

	public void criarFollows() throws RuleHasNoFollowException{
		//Marcar o fim do arquivo de entrada no nTerminal da regra inicial
		regras.getFirst().addSeguinte(this.fimDeLinha);

		//A -> BCD, tudo em First(C), exceto vazio, esta em Follow(B)
		//se First(C) tem vazio, entao add First(D) em Follow(B), e assim por diante
		for (RegraNaoTerminal regraNT : regras) {			
			for (LinkedList<RegraGramatica> regra : regraNT.getRegra()) {
				for(int c = 0; c < regra.size(); c++){
					//caso seja um nTerminal
					if(!regra.get(c).isTerminal()){
						//nTerminal a ser analisado agora
						RegraNaoTerminal producao = (RegraNaoTerminal) regra.get(c);
						//comece do proximo e va ate o final
						for(int i = 1; c+i < regra.size(); i++){
							this.addSeguinte(producao, regra.get(c+i));							
							///*
							//TODO AQUI
							//se esta producao n gerar vazio quebra esse la�o
							if(!regra.get(c+i).getGeraVazio()){
								break;
							}
							//*/
							//A -> BC, entao Follow(A) esta em Follow(C)
							//se chegou na ultima producao da regra
							if(c+i == regra.size()-1){
								this.addSeguinte(producao, regraNT);
							}
						}
					}
				}
			}
		}

		//A -> BC, entao Follow(A) esta em Follow(C)
		for(RegraNaoTerminal regraNT : regras){
			for(LinkedList<RegraGramatica> regra : regraNT.getRegra()){
				if(!regra.getLast().isTerminal()){
					RegraNaoTerminal producao = (RegraNaoTerminal)regra.getLast();
					this.addSeguintePorFollow(producao,regraNT);
				}
			}
		}

		//A -> BCD, entao Follow(A) esta em Follow(C) se First(D) gera vazio
		for(RegraNaoTerminal regraNT : regras){
			for(LinkedList<RegraGramatica> regra : regraNT.getRegra()){				
				for(int c = 0; c < regra.size(); c++){
					int i = 1;
					//caso nTerminal
					if(!regra.get(c).isTerminal()){
						RegraNaoTerminal rNT = (RegraNaoTerminal) regra.get(c);
						while(c+i < regra.size() && !regra.get(c+i).isTerminal()){
							//System.out.println("ENTREI1 "+regraNT.getSimbolo());
							//System.out.println("\tENTREI2 "+rNT.getSimbolo());
							RegraNaoTerminal rN = (RegraNaoTerminal) regra.get(c+i);
							//caso os proximos dele n gerem vazio, quebra laco
							if(!rN.getGeraVazio()){
								break;
							}
							i++;//vai ate o fim olhando
							if(c+i == regra.size()){
								this.addSeguintePorFollow(rNT,regraNT);
								//System.out.println("ENTREI1 "+regraNT.getSimbolo());
								//System.out.println("ENTREI2 "+rNT.getSimbolo());
							}
						}
						//caso seja um terminal, add
						if(c+i < regra.size() && regra.get(c+i).isTerminal()){
							RegraTerminal f = (RegraTerminal) regra.get(c+i);
							rNT.addSeguinte(f);
							//System.out.println(rNT.getSimbolo()+" adicionou "+f.getSimbolo());
						}						
					}
				}
			}
		}

		for(RegraNaoTerminal regraNT : regras){
			if(regraNT.getSeguinte().size() == 0){
				throw new RuleHasNoFollowException(regraNT.getSimbolo());				
			}
			if(regraNT.getSeguinte().contains(new RegraTerminal(Constants.PRODUCAO_VAZIA))){
				throw new RuleHasNoFollowException(regraNT.getSimbolo());
			}				
		}
	}

	public void addSeguinte(RegraNaoTerminal regra, RegraGramatica r){
		if(r instanceof RegraTerminal){
			RegraTerminal regraT = (RegraTerminal) r;
			if(regraT.equals(new RegraTerminal(Constants.PRODUCAO_VAZIA))){

			}else{
				regra.addSeguinte(regraT);
			}			
		}
		else if(r instanceof RegraNaoTerminal){
			RegraNaoTerminal regraNT = (RegraNaoTerminal) r;
			for(RegraTerminal regraT : regraNT.getPrimeiro()){
				if(regraT.equals(new RegraTerminal(Constants.PRODUCAO_VAZIA))){

				}else{
					regra.addSeguinte(regraT);
				}				
			}
		}
	}

	public void addSeguintePorFollow(RegraNaoTerminal regra, RegraNaoTerminal r){		
		for(RegraTerminal regraT : r.getSeguinte()){
			if(regraT.equals(new RegraTerminal(Constants.PRODUCAO_VAZIA))){

			}else{
				regra.addSeguinte(regraT);
			}
		}			
	}

	public void isGramaticaAmbigua() throws GramaticaTem2FirstNaMesmaRegra, GramaticaIsNotSimplified{
		for(RegraNaoTerminal rn : regras){			
			LinkedList<RegraTerminal> primeiro = new LinkedList<>();
			for(LinkedList<RegraGramatica> regra : rn.getRegra()){
				if(!regra.getFirst().isTerminal()){
					RegraNaoTerminal r = (RegraNaoTerminal)regra.getFirst();
					primeiro.addAll(r.getPrimeiro());
					
				}else{
					RegraTerminal r = (RegraTerminal)regra.getFirst();
					primeiro.add(r);
				}
			}
			Collections.sort(primeiro, new Comparator<RegraTerminal>() {
		         @Override
		         public int compare(RegraTerminal o1, RegraTerminal o2) {
		             return  o1.getSimbolo().compareTo(o2.getSimbolo());
		         }
		     });
			for(int c = 0; c < primeiro.size(); c++){
				if(c+1< primeiro.size() && primeiro.get(c).equals(primeiro.get(c+1))){
					throw new GramaticaTem2FirstNaMesmaRegra(rn.getSimbolo());
				}
			}
		}		
		for(RegraNaoTerminal rn : regras){
			for (LinkedList<RegraGramatica> regra : rn.getRegra()) {
				if(regra.size() == 1){//produz apenas 1 item
					if(!regra.getFirst().isTerminal()){//se produz apenas 1 nTerminal
						throw new GramaticaIsNotSimplified(rn.getSimbolo(), regra.toString());
					}
				}
			}
		}
	}
	
	public void printGramatica(){
		System.out.println("-----------------------------------------------------------------");
		System.out.println("Gramatica lida do arquivo: "+arquivoGramatica);
		System.out.println("----------------------------------------------------");

		System.out.println("Regras de Producao:");
		for(RegraNaoTerminal nTerminal : regras){
			System.out.println("\t"+nTerminal.toString());
		}

		System.out.println("----------------------------------------------------");
		System.out.println("Terminais:");
		for(RegraTerminal terminal : terminais){
			System.out.println("\t"+terminal.getSimbolo());
		}

		System.out.println("----------------------------------------------------");
		System.out.println("Nao Terminais:");
		for(RegraNaoTerminal regraNT : regras){
			System.out.println(regraNT.getSimbolo());
			System.out.println("\tConjunto Primeiro:"+regraNT.getPrimeiro());
			System.out.println("\tConjunto PrimeiroHM:"+regraNT.getPrimeiroHM());
			System.out.println("\tConjunto Seguinte:"+regraNT.getSeguinte());
		}
		System.out.println("----------------------------------------------------");
	}

	public LinkedList<RegraNaoTerminal> getRegras(){
		return this.regras;
	}

	public LinkedList<RegraTerminal> getTerminais(){
		return this.terminais;
	}

	public HashMap<String, RegraNaoTerminal> getRegrasHM(){
		return this.regrasHM;
	}

	public RegraNaoTerminal getPrimeiraRegra() {
		return this.regras.getFirst();
	}

}
