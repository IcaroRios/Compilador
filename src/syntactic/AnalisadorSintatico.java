package syntactic;

import java.util.Stack;

public class AnalisadorSintatico {
	//A PILHA VAI SER A PR�PRIA PILHA DE EXECU��O DO C�DIGO
	private Stack<String> stack;
	
	public AnalisadorSintatico(){
		this.stack = new Stack<>(); 
	}
	
	public void Executar(){
		//TODO
		/*
		se o que tem no topo da pilha � um terminal --- consome
		
		se o que tem no topo da pilha � um n terminal --- vai pra matriz e gera a produ��o dele
		
		segue at� a lista de tokens acabar
		
		lista de tokens acabou e a pilha est� vazia o c�digo est� correto sintaticamente,
		se n�o est� errado
		*/
		
		
		//TODO
		/*TRATAMENTO DE ERROS
		pegue todos os follows da sua produ��o e de todos os seus antecessores                        
		se for um n�o terminal: ignore tokens at� chegar em um follow desses                        
		                       
		se for um terminal: assuma que o token foi inserido.                        
		e continua
		*/
		
	}
	
}
