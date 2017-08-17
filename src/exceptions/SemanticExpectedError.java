package exceptions;

public class SemanticExpectedError extends Exception{
	
	private static final long serialVersionUID = 1L;
	private int nLinha;
	private String expected;
	private String recieved;
	
	public SemanticExpectedError(int nLinha, String expected, String recieved){
		this.nLinha = nLinha;
		this.expected = expected;
		this.recieved = recieved;
	}
	
	public int getNLinha(){
		return this.nLinha;
	}
	
	public String getExpected(){
		return this.expected;
	}
	
	public String getRecieved(){
		return this.recieved;
	}
}
