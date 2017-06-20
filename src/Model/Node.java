package Model;

import java.util.LinkedList;

public class Node {
	private LinkedList<Node> list;
	private Token token;
	
	public Node(Token token){
		this.token = token;
	}
	
	public void addNode(Node node){
		this.list.add(node);
	}
	
	public Token getToken(){
		return this.token;
	}
	
	public LinkedList<Node> getList(){
		return this.list;
	}
	
	@Override
	public String toString(){
		String text = "";
		text = this.token.getLexema()+"\n";
		for (Node node : list) {
			text = text +"\t"+node.toString() + "\n";
		}
		return text;		
	}
}
