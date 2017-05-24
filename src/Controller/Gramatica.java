package Controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import Model.RegraGramatica;
import Model.RegraNaoTerminal;
import Model.RegraTerminal;

public class Gramatica {
	private String arquivoGramatica;
	LinkedList<RegraNaoTerminal> regras;
	LinkedList<RegraTerminal> terminais;	
	public Gramatica(String arquivoGramatica){
		this.arquivoGramatica = arquivoGramatica;
		regras = new LinkedList<>();
		terminais = new LinkedList<>();
	}
	
	//TODO ler o arquivo de gram�tica
	public void LerGramatica(){
		BufferedReader br = null;
		FileReader fr = null;
		try {
			fr = new FileReader(arquivoGramatica);
			br = new BufferedReader(fr);
			String linha;
			//Para cada linha
			while ((linha = br.readLine()) != null) {
				if(linha.equals("")){
					//se tiver linhas vazias n fazer nada
				}
				else{
					System.out.println("linha: "+linha);
					String[] aux = linha.split("::=");
					//1� parte da regra, n terminal a ser derivado
					String simbolo = aux[0].trim();
					//System.out.println("tam aux:"+aux.length);
					System.out.println("s�mbolo: "+simbolo);					
					//criando o nTerminal da regra
					RegraNaoTerminal nTerminal = new RegraNaoTerminal(simbolo);
					regras.add(nTerminal);				
					LinkedList<LinkedList<RegraGramatica>> regra = new LinkedList<>();
					LinkedList<RegraGramatica> r = null;
					//obtendo as produ��es geradas pela regra
					//Character c = new Character('|');
					String[] producoes = aux[1].split(" (\\|)"); 		
					System.out.println("Qtd produ��es: "+producoes.length);
					for(int cont1 = 0; cont1 < producoes.length; cont1++){
						r = new LinkedList<>();
						//se for produ��o vazia
						if(producoes[cont1].equals(" ")){
							System.out.println("\tProdu��o vazia!!!!!!");
						}
						else{
							System.out.println("\tProdu��o: "+producoes[cont1]);
							//adicionando uma produ��o
							String[] producao = (producoes[cont1].trim()).split(" ");
												
							for(int cont = 0; cont < producao.length; cont++){
								//se come�ar com < e terminar com > � nTerminal 
								if(producao[cont].charAt(0)=='<' && 
										producao[cont].charAt(producao[cont].length()-1)=='>' ){
									System.out.println("\t\tprodu��o nTerminal "+cont+" - "+producao[cont]);
									//adicionando a nova regra nTerminal							
									r.add(new RegraNaoTerminal(producao[cont].trim()));
								}
								//� terminal
								else{
									RegraTerminal novoTerminal = new RegraTerminal(producao[cont].trim());
									//adicionando na lista de terminais caso n contenha
									if(!terminais.contains(novoTerminal)){
										terminais.add(novoTerminal);
									}							
									//adicionando a nova regra terminal
									r.add(novoTerminal);
									System.out.println("\t\tprodu��o Terminal "+cont+" - "+producao[cont]);
								}
							}
						}
					}
					regra.add(r);
					///*
					
					//*/
				}
				
			}
		} catch (IOException e){//se der merda na leitura
			e.printStackTrace();		
		} finally {//vamos fechar?
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
	}
	
	//TODO criar os firsts para cada n terminal
	public void CriarFirsts(){
		
	}
	
	//TODO criar os follows para cada n terminal
	public void CriarFollows(){
		
	}

	public void printGramatica() {
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		System.out.println("Gram�tica lida do arquivo: "+arquivoGramatica);
		System.out.println("N�o Terminais:");
		for(RegraNaoTerminal nTerminal : regras){
			System.out.println("\t"+nTerminal.getSimbolo());
		}
		System.out.println("----------------------------------------------------");
		System.out.println("Terminais:");
		for(RegraTerminal terminal : terminais){
			System.out.println("\t"+terminal.getSimbolo());
		}
		System.out.println("----------------------------------------------------");
		System.out.println("Regras de Produ��o:");
		for(RegraNaoTerminal nTerminal : regras){
			System.out.println("\t"+nTerminal.toString());
		}
	}
}
