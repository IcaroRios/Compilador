# Projeto de Compilador 2017.1 UEFS
Dupla Ícaro Rios e Nathan Almeida, seção de Just
MI - Processadores de Linguagem de Programação

Toda a entrada deve estar no diretório entrada.

## Testes
Códigos de testes na pasta Testes. Códigos de testes para léxico e sintático.
Os códigos para análise do semântico são os mesmos do sintático.

## Análise Léxica
O analisador léxico analisa cada arquivo separadamente e gera saída num arquivo de extensão .oLex.
Produzindo uma lista de Tokens

## Análise Sintática
A análise sintática só acontece para um arquivo caso este não gere erros léxicos. 
O analisador analisa cada arquivo separadamente e gera saída num arquivo de extensão .oSyn.
Caso a análise seja correta uma mensagem de sucesso é gerada. Caso contrário uma mensagem de erro é gerada para cada erro sintático encontrado no código.


## Análise Semmântica
Só ocorre se a análise sintática for bem sucedida. As variáveis e funções são todas listadas antes da análise ser iniciada.
