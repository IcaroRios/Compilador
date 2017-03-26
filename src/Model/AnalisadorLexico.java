package Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Util.ExpressoesRegulares;
import Util.ComentarioBloco;
import Util.Token;


public class AnalisadorLexico implements ExpressoesRegulares {

    private final String pastaSaida = "saida";
    private final List<Token> tokens;
    private final List<Token> tokensError;
    private int numeroLinha;

    public AnalisadorLexico() {
        tokens = new LinkedList<>();
        tokensError = new LinkedList<>();
        numeroLinha = 1;
    }

    public void Executar(File arquivos) {
        try {
            
            File listaDeArquivos[] = arquivos.listFiles();

            //Percorre os arquivos na pasta
            for (int i = 0; i < listaDeArquivos.length; i++) {
                //Se for diretorio ou um tipo de arquivo ignorado, passa pro proximo
                if (listaDeArquivos[i].isDirectory()) {
                    continue;
                }

                //verificando se o arquivo existe para comecar a analisar
                if (listaDeArquivos[i].exists()) {
                    BufferedReader leitor = new BufferedReader(new FileReader(listaDeArquivos[i]));
                    boolean iniciouComentario = false;
                    numeroLinha = 1;

                    //pair para verificar o estado dos comentarios
                    ComentarioBloco pIB;
                    //Lendo do Arquivo
                    for (String linha = leitor.readLine(); linha != null; linha = leitor.readLine()) {
                        //verifica se abriu algum comentario
                        pIB = analisaComentario(0, iniciouComentario, linha);

                        //System.out.println(linha +"fora do metodo");
                        iniciouComentario = pIB.isIniciouComentario();

                        verificaRegex(pIB.getLinha());
                        //atuliza contador de linha
                        numeroLinha++;
                    }
                    //se nao fechou comentario gera token de erro de comentario
                    if (iniciouComentario) {
                        tokensError.add(new Token("{comentario", "COMENTARIO_MAL_FORMADO", numeroLinha, true));
                    }

                    //gerando saidas
                    gerarSaida(listaDeArquivos[i].getName());
                    leitor.close();
                }
                // Fim do Arquivo Atual
            }
        } catch (FileNotFoundException ex) {
            System.out.println("ERRO: Arquivo nao encontrado");
        } catch (NullPointerException | IOException ex) {
            ex.printStackTrace();
        }
    }

    private void verificaRegex(String entrada) {
        char analisar[] = entrada.toCharArray();
        String acumulador = "";

        for (int i = 0; i < analisar.length; i++) {
            acumulador += analisar[i];
            String atual = "" + analisar[i];
            Pattern patern = Pattern.compile(ExpressaoAuxiliar.SEPARADORES.valor);
            Matcher m = patern.matcher(atual);

            //verificar separadores
            if (m.matches()) {
                //Remove o separador
                acumulador = removeUltimoElemento(acumulador);
                if (!acumulador.equals("")) {
                    //Se passa cria tokenn
                    if (!verificaRegexCriandoToken(acumulador)) {
                        //token de error
                        tokensError.add(new Token(acumulador, numeroLinha, true));
                    }
                }
                acumulador = "";
                //não é espaco
                if (!m.group().matches("\\s+")) {
                    char separador = m.group().charAt(0);
                    int formador = buscadorDeSeparador(separador, entrada, i + 1);
                    //outro separador
                    if (formador > 0) {
                        String palavra = "";
                        for (int z = i; z <= formador; z++) {
                            //Remonta a palavra
                            palavra += analisar[z];
                        }
                        //cria token
                        if (!verificaRegexCriandoToken(palavra)) {
                            //Se deu erro em string
                            if (m.group().equals("\"")) {
                                tokensError.add(new Token(palavra, "CADEIA_ERRADA", numeroLinha, true));
                            } //Outros erros ele auto-identifica na classe Token
                            else {
                                tokensError.add(new Token(palavra, numeroLinha, true));
                            }
                        }
                        i = formador;
                        continue;
                    } //Terminou a linha e nao achou o separador
                    else {
                        String palavra = "";
                        for (int z = i; z < analisar.length; z++) {
                            //Remonta a palavra
                            palavra += analisar[z];
                        }
                        //Se deu erro em string
                        if (m.group().equals("\"")) {
                            tokensError.add(new Token(palavra, "CADEIA_ERRADA", numeroLinha, true));
                        } else {
                            tokensError.add(new Token(palavra, numeroLinha, true));
                        }

                        i = analisar.length;
                        continue;
                    }
                } //Caso seja espaco, continua
                else {
                    continue;
                }
            }
            
            if (Pattern.matches(ExpressaoAuxiliar.CASOESPECIAL.valor, acumulador)) {
                int proximo = i + 1;
                if (proximo < analisar.length) {
                    String prox = "" + analisar[proximo];
                    try {
                        int temp = Integer.parseInt(prox);
                        acumulador += temp;
                        i = proximo;
                    } catch (NumberFormatException e) {
                        tokensError.add(new Token(acumulador, numeroLinha, true));
                        acumulador = "";
                        continue;
                    }
                }
            }
            
            if (isEntradaValida(acumulador) && i + 1 == analisar.length) {
                verificaRegexCriandoToken(acumulador);
            } else if (!isEntradaValida(acumulador)) {
                boolean precisaCompensar = false;
                if (acumulador.length() > 1) {
                    acumulador = removeUltimoElemento(acumulador);
                    precisaCompensar = true;
                }

                if (!verificaRegexCriandoToken(acumulador)) {
                    tokensError.add(new Token(acumulador, numeroLinha, true));
                }

                acumulador = "";
                if (precisaCompensar) //decrementA o contador para criar a nova sentenca
                {
                    i--;
                }
            }
        }
    }

    private boolean isEntradaValida(String entrada) {
        for (ESTRUTURALEXICA regex : ESTRUTURALEXICA.values()) {
            if (Pattern.matches(regex.valor, entrada)) {
                return true;
            }
        }
        return false;
    }

    private boolean verificaRegexCriandoToken(String entrada) {
        for (ESTRUTURALEXICA regex : ESTRUTURALEXICA.values()) {
            Pattern patern = Pattern.compile(regex.valor);
            Matcher m = patern.matcher(entrada);
            if (m.matches()) {
                int grupo = 0;
                for (int i = 1; i <= m.groupCount(); i++) {
                    if (m.group(i) != null) {
                        grupo = i;
                        break;
                    }
                }
                tokens.add(new Token(regex.ordinal(),entrada, numeroLinha, grupo));
                return true;
            }
        }
        return false;
    }

    public String removeUltimoElemento(String original) {
        return original.substring(0, original.length() - 1);
    }

    private int buscadorDeSeparador(char separador, String palavra, int inicio) {
        char[] buscador = palavra.toCharArray();
        for (int i = inicio; i < buscador.length; i++) {
            if (separador == buscador[i]) {
                return i;
            }
        }
        return -1;
    }
    
    
    /**
     *Com este método, os comentários em bloco não checam nem a serem analisados
     * assim como o compilador ignora a análise deles, aqui ele está sendo ignorado
     * na hora de pegar os tokens.      
     */
    public ComentarioBloco analisaComentario(int is, boolean iniciouComentario, String entrada) {
        ComentarioBloco comentario = new ComentarioBloco(iniciouComentario);
        boolean isString = false, isChar = false;
        char[] analisar = entrada.toCharArray();

        //se estiver esperando fechar comentario e nao existir, retorna logo
        //esta verificação de cara só é possível pois dá para fazer comparação com string
        if ((!(entrada.contains("*/"))) && comentario.isIniciouComentario()) {
            
            comentario.setLinha("");
            return comentario;
        }

        for (int i = 0; i < analisar.length; i++) {
            //verifica se nao abriu um comentario
            if (!comentario.isIniciouComentario()) {
                //iniciou comentario e nao eh string
                /*NÃO ALTERAR A ORDEM DOS OPERADORES, COMO É UMA VERIFICAÇÃO &&
                A PRIMEIRA É VERIFICADA PARA DEPOIS A PRÓXIMA, ENTÃO ELE VERIFICA
                SE NÃO ESTÁ NO LIMITE DO TAMANHO DA LINHA PARA DEPOIS PROCURAR O '*' */
                if (analisar[i] == '/' && i < analisar.length - 1 && analisar[i + 1] == '*' && !isString && !isChar) {
                    comentario.setIniciouComentario(true);

                } else {
                    //comecou uma string
                    if (analisar[i] == '\"') {
                        isString = !isString;
                        isChar = false;
                        comentario.setIniciouComentario(false);
                    } //comecou um char
                    else if (analisar[i] == '\'') {
                        isChar = !isChar;
                        isString = false;
                        comentario.setIniciouComentario(false);
                    }
                    comentario.setLinha(comentario.getLinha() + analisar[i]);

                }
            } //se abriu um comentario, verifica se fechou e nao eh uma string
            
            else if (analisar[i] == '*' && i < analisar.length - 1 && analisar[i + 1] == '/' &&!isString && !isChar) {
                //if (i < analisar.length - 1) {
                  //  if (analisar[i + 1] == '/') {
                        i++;// duas horas para conseguir consertar esa merda                        
                        comentario.setIniciouComentario(false);
                  //  }
                //}
            }
        }

        return comentario;
    }

    private void gerarSaida(String arquivo) {
        try {
            File pasta = new File(pastaSaida);
            pasta.mkdir();
            File n = new File(pasta.getName() + File.separator + arquivo);
            BufferedWriter bw = new BufferedWriter(new FileWriter(n));
            for (Token t : tokens) {
                bw.write(t.toString());
                bw.newLine();
                bw.flush();
            }
            //se possui erros, escreve eles
            if (!tokensError.isEmpty()) {
                bw.newLine();
                bw.flush();
                for (Token t : tokensError) {
                    bw.write(t.toString());
                    bw.newLine();
                    bw.flush();
                }
            } else if (!tokens.isEmpty()) {
                
                System.out.println("Analise Lexica para o arquivo: " + arquivo + ": Sucesso.");
                
            } else {
                /**
                 * Prestar bem
                 */
                System.out.println("[*] AVISO O arquivo: [" + arquivo + "] nao gerou nenhum Token. Pulando analise sintatica.");
            }

            bw.close();
            tokens.clear();
            tokensError.clear();
        } catch (IOException ex) {
            System.out.println("Deu merda na escrita do arquivo.");
        } 
    }

}
