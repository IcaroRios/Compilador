package syntactic;

import java.util.Stack;

public class AnalisadorSintatico {
	//A PILHA VAI SER A PRÓPRIA PILHA DE EXECUÇÃO DO CÓDIGO
	private Stack<String> stack;
	
	public AnalisadorSintatico(){
		this.stack = new Stack<>(); 
	}
	
	public void Executar(){
		//TODO
		/*
		se o que tem no topo da pilha é um terminal --- consome
		
		se o que tem no topo da pilha é um n terminal --- vai pra matriz e gera a produção dele
		
		segue até a lista de tokens acabar
		
		lista de tokens acabou e a pilha está vazia o código está correto sintaticamente,
		se não está errado
		*/
		
		
		//TODO
		/*TRATAMENTO DE ERROS
		pegue todos os follows da sua produção e de todos os seus antecessores                        
		se for um não terminal: ignore tokens até chegar em um follow desses                        
		                       
		se for um terminal: assuma que o token foi inserido.                        
		e continua
		*/
		
	}
	
}
