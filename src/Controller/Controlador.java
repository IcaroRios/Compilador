
package Controller;

import Model.AnalisadorLexico;
import java.io.File;

public class Controlador {

    public static void main(String args[]) {
        
        File dir = new File("entrada");        
        if (!dir.exists()) {            
            System.out.println("A pasta 'entrada' não existe.");
            System.exit(0);
        } //se conseguir pegar os arquivos, inicia a analise
        else {
            AnalisadorLexico lexico = new AnalisadorLexico();
            lexico.Executar(dir);
        }
        System.exit(0);
    }
}
