
package Controller;

import Model.AnalisadorLexico;

import java.io.File;

public class Controlador {

	AnalisadorLexico lexico = new AnalisadorLexico(); 
	File dir;
	
	public Controlador(String diretorioEntrada){		 
		this.dir = new File(diretorioEntrada);        
        if (!dir.exists()) {
            System.out.println("A pasta "+diretorioEntrada+" não existe.");
            System.exit(0);
        } //se conseguir pegar os arquivos, inicia a analise
        analiseLexica();
	}
	
	public void analiseLexica(){
		lexico.Executar(dir);
		System.exit(0);
	}
}
