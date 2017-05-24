package lexical;

public interface ExpressoesRegulares {
    
    /*
    PRESTAR ATEN��O COM A ORDEM, POIS UM ACABA AFETANDO O OUTRO
    */
    public enum ESTRUTURALEXICA {
        RESERVADA("(program)|(const)|(var)|(function)|(begin)|(end)|(if)|"
                + "(then)|(else)|(while)|(do)|(read)|(write)|(integer)|(real)|"
                + "(boolean)|(true)|(false)|(string)|(char)"),//feito
        NUMERO("(((\\s))?(\\d)+(\\.\\d+)?)"),
        OPERADOR_ARITMETICO("(\\+)|(\\-)|(\\*)|(\\/)|(\\%)"),//feito
        OPERADOR_RELACIONAL("(<=)|(>=)|(!=)|(<)|(>)|(==)"),//feito
        OPERADOR_DE_ATRIBUICAO("(=)"),//feito
        OPERADOR_LOGICO("(!)|(\\&\\&)|(\\|\\|)"),//feito
		DELIMITADOR("(;)|(,)|(\\(|(\\)))|(\\[)|(\\])|(:)"),        
        IDENTIFICADOR("([a-zA-Z])([a-zA-Z]|(\\d)|(_))*?"),
        CARACTERE("'([[a-zA-Z]\\d ]?)'"), // verificar
        CADEIA_DE_CARACTERES("\"[a-zA-Z]([[ -~&&[^\"]]\\\"])*\"")
        ;
        
        public String valor;

        private ESTRUTURALEXICA(String valor) {
            this.valor = valor;
        }
    }

    /**
     * Olhar melhor
     */
    public enum ExpressaoErro {
        NRO_MAL_FORMADO("(\\d+\\.+)"),
        NRO_MAL_FORMADO_2("(\\.(.\\d+)?)"),
        CARACTERE_MAL_FORMADO("^(')(.+)?")
        ;
        
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