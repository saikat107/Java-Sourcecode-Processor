package edu.columbia.cs.simpleASTParser;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class SimpleASTVisitor extends ASTVisitor{
	
	private final CompilationUnit cu;
	private String filename;

	public SimpleASTVisitor(char[] arr,String file_name) {
		
		this.filename = file_name;
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(arr);
		parser.setResolveBindings(true);
		cu = (CompilationUnit) parser.createAST(null);
		
	}
	
	/*
	public boolean visit(SimpleName node) {
		//if (this.names.contains(node.getIdentifier())) {
		
		System.out.println("Usage of '" + node + "' at line " +	cu.getLineNumber(node.getStartPosition())
				+ " type: " + node.resolveBinding());
		//}
		return true;
	}
	*/
	
	public boolean visit(TypeDeclaration  node) {
		
		System.out.println(this.filename + " , " + 
				cu.getLineNumber(node.getStartPosition()) + 
				", TypeDeclaration , " + node.getName() );
		return true;
	
	}
	
	/*
	
	public boolean visit(ImportDeclaration  node) {
		
		System.out.println(this.filename + " , " + 
				cu.getLineNumber(node.getStartPosition()) + 
				", ImportDeclaration , " + node.getName() );
		return true;
	
	}
	
	*/
	
	
	public boolean visit(MethodDeclaration  node) {
		
		/*
		System.out.println(this.filename + " , " + 
					cu.getLineNumber(node.getStartPosition()) + 
					", MethodDeclaration , " + node.getName() ); */
		
		List<SingleVariableDeclaration> params = node.parameters();
		String varNames = ""; 
		for (SingleVariableDeclaration p : params) {
			varNames += p.getName() + " ";
			//System.out.println(p.getName());
		}
		
		
		System.out.println(this.filename + " , " + 
				cu.getLineNumber(node.getStartPosition()) + 
				", MethodDeclaration , " + node.getName() + 
				" < " + varNames + ">" );
		
		return true;
	}
	

	/*
	public boolean visit( ClassInstanceCreation  node) {
		
		System.out.println(this.filename + " , " + 
					cu.getLineNumber(node.getStartPosition()) + 
					", ClassDeclaration , " + node.toString());
		return true;
	}
	*/
	
	public void parse() {
		cu.accept(this);
	}
}

