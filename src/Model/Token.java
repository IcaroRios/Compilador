package Model;

import java.util.regex.Pattern;

public class Token implements ExpressoesRegulares {

    private int id;
    private int nLinha;
    private int tipo;
    private String lexema;
    private String erro;
    private boolean isError;    

    /**
     * Construtor de Tokens comuns
     * @param id identificador do token
     * @param lexema lexema do token
     * @param nLinha numero da linha em que foi criado
     * @param tipo tipo do lexema, para diferenciar lexemas do mesmo
     * identificador. Esta variavel contem o grupo do lexema no regex
     */
    public Token(int id, String lexema, int nLinha, int tipo) {
        this.id = id;
        this.lexema = lexema;
        this.nLinha = nLinha;
        this.isError = false;
        this.erro = "";
        this.tipo = tipo;      

    }

    /**
     * Construtor de erro auto-detectavel
     * @param lexema lexema do erro
     * @param nLinha numero da linha do erro
     * @param error flag de erro
     */
    public Token(String lexema, int nLinha, boolean error) {
        this.lexema = lexema;
        this.nLinha = nLinha;
        isError = error;
        erro = "";
        id = 0;
        tipo = 0;       
        
        if (isError) {
            detectarError(lexema);
        }
    }

    public Token(String lexema, String mensagem, int linha, boolean error) {
        this.lexema = lexema;
        erro = mensagem;
        nLinha = linha;
        isError = error;
        tipo = 0;       
        
    }

    public int getId() {
        return id;
    }

    public String getLexema() {
        return lexema;
    }

    public int getnLinha() {
        return nLinha;
    }

    public String getErro() {
        return erro;
    }

    public boolean isIsError() {
        return isError;
    }

    public int getTipo() {
        return tipo;
    }
    
    @Override
    public String toString() {
        if (isError) {
			return "Erro na linha: "+ nLinha + " <" + erro.toLowerCase() + " - " + lexema +">";
        }
		return tipo+"<"+ ESTRUTURALEXICA.values()[id].name().toLowerCase() + " - " + lexema+">";

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token) {
            Token aux = (Token) obj;
            if (aux.getLexema().equals(this.lexema)) {
                return true;
            }
        }
        return false;
    }

    private void detectarError(String error) {
        for (ExpressaoErro regex : ExpressaoErro.values()) {
            if (Pattern.matches(regex.valor, error)) {
                erro += regex.name();
                return;
            }
        }
        erro += "SIMBOLO_INVALIDO";
    }

}