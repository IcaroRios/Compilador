
package Controller;

import java.io.File;

import lexical.AnalisadorLexico;

public class Controlador {

	AnalisadorLexico lexico = new AnalisadorLexico();
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
	}
	
	public void analisar(){
		analiseLexica();
		//analiseSintatica();
	}
	
	public void analiseLexica(){
		lexico.Executar(dir);
		System.exit(0);
	}
	
	public void analiseSintatica(){
		
	}
	
}
