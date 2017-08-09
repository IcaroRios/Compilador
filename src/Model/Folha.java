package Model;

import java.util.LinkedList;

import syntactic.RegraNaoTerminal;

public class Folha{
	private LinkedList<Folha> list;
	//private Token token;
	private Object value;
	private boolean isTerminal;
	
	public Folha(Object token, boolean isTerminal){
		this.value = token;
		this.list = new LinkedList<>();
		this.isTerminal = isTerminal;
	}
	
	public void addLeaf(Folha node){
		this.list.add(node);
	}
	
	public Object getValue(){
		return this.value;
	}
	
	public LinkedList<Folha> getList(){
		return this.list;
	}
	
	public boolean getIsTerminal(){
		return this.isTerminal;
	}
	
	@Override
	public String toString(){
		String text = "";
		if(this.isTerminal){
			Token tk = (Token) value;
			//text = tk.getTipo()+"->"+tk.getLexema()+"\n";
			//text = tk.toString()+"\n";
			text = tk.toString();
		}else{
			RegraNaoTerminal rnt = (RegraNaoTerminal) value;
			//text = rnt.getSimbolo()+"\n";
			text = rnt.getSimbolo();
		}		
		for(Folha node : list){
			//text = text +"\t"+node.toString() ;
			text = text +"{"+node.toString()+"}" ;
		}
		return text;		
	}
}
