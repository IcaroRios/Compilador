package Controller;

import java.io.File;

import lexical.AnalisadorLexico;

public class Controlador {

	AnalisadorLexico lexico;	
	Gramatica gramatica;
	File dir;
	
	public Controlador(String diretorioEntrada, String arquivoGramatica){	 
		this.dir = new File(diretorioEntrada);        
		//se conseguir pegar os arquivos, inicia a analise
		if(!dir.exists()) {
            System.out.println("A pasta "+diretorioEntrada+" n�o existe.");
            System.exit(0);
        }
		gramatica = new Gramatica(arquivoGramatica);
		lexico = new AnalisadorLexico();		
	}
	
	public void analisar(){
		//analiseGramatica();		
		analiseLexica();
	}
	
	public void analiseGramatica(){
		gramatica.LerGramatica();
		//gramatica.CriarFirsts();
		gramatica.printGramatica();
	}
	
	public void analiseLexica(){
		lexico.Executar(dir);
		System.exit(0);
	}
		
}