package edu.columbia.cs.simpleASTParser;

import edu.columbia.cs.simpleASTParser.Token;

public class ASTToken extends Token {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2997206275657174543L;

	private final int depth;
	private  String type;
	
	private final int startLine;
	private final int endLine;

	public ASTToken(String text, int depth) {
		this(text, depth, "");
	}

	public ASTToken(String text, int depth, String type) {
		this(text, depth, type, 0, 0);
	}

	public ASTToken(String text, int depth, String type, int startLine, int endLine) {
		super(text);
		this.depth = depth;
		this.type = type;
		this.startLine = startLine;
		this.endLine = endLine;
	}
	
	private final char delim = '\t';
	@Override
	public String text() {
		StringBuilder sb = new StringBuilder();
		//sb.append("Text : " + this.text + "\t\tType : " + this.type);
		//sb.append("<\"" + this.text + "\"");
		 if(this.type != null && this.type.length()!=0){
		 	sb.append(this.type );
		}
		 else{
			sb.append(this.text);
		}
		//sb.append(">");
		return sb.toString();
	}

	public String getContent() {
		return this.text;
	}
	
	public String getType(){
		return this.type;
	}
	
	public void setCOntent(String content){
		super.text = content;
		this.text = content;
	}
	
	public void setType(String type){
		this.type = type;
	}

	public int getDepth() {
		// TODO Auto-generated method stub
		return 0;
	}
}