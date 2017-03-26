package Util;

public interface ExpressoesRegulares {
    
    /*
    PRESTAR ATENÇÃO COM A ORDEM, POIS UM ACABA AFETANDO O OUTRO*/
    public enum ESTRUTURALEXICA {
        RESERVADA("(program)|(const)|(var)|(function)|(begin)|(end)|(end)|(if)|"
                + "(then)|(else)|(while)|(do)|(read)|(write)|(integer)|(real)|"
                + "(boolean)|(true)|(false)|(string)|(char)"),//feito
        OPERADORARITMETICO("(\\+)|(\\-)|(\\*)|(\\/)|(\\%)"),//feito
        OPERADORRELACIONAL("(!=)|(=)|(<)|(<=)|(>)|(>=)"),//feito
        //COMENTARIOBLOCO("\\/\\*(.*)\\*\\/") // isto foi removido pois agora estou verificando enquanto leio o arquivo
        IDENTIFICADOR("([a-zA-Z])([a-zA-Z]|(\\d)|(_))*?"),
        CARACTERE("'([a-zA-Z]|\\d)'"), // verificar
        CADEIA_DE_CARACTERES("\"[a-zA-Z]([a-zA-Z]|\\d|\\p{Blank})*?\""),
        NUMERO("-?\\d+(\\.\\d+)?"); //  acho que está errado.
        
        public String valor;

        private ESTRUTURALEXICA(String valor) {
            this.valor = valor;
        }
    }

    /**
     * Olhar melhor
     */
    public enum ExpressaoErro {
        NRO_MAL_FORMADO("(\\d+\\.(.+)?)"),
        CARACTERE_MAL_FORMADO("^(')(.+)?");

        public String valor;

        private ExpressaoErro(String valor) {
            this.valor = valor;
        }
    }

    /**
     * olhar melhor
     */
    public enum ExpressaoAuxiliar {
        SEPARADORES("(\")|(\\s+)|(')"),
        CASOESPECIAL("\\d+\\.");

        public String valor;

        private ExpressaoAuxiliar(String valor) {
            this.valor = valor;
        }
    }

}