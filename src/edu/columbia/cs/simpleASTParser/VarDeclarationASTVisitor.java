package edu.columbia.cs.simpleASTParser;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class VarDeclarationASTVisitor extends ASTVisitor{
	
	private final CompilationUnit cu;
	private String filename;

	public VarDeclarationASTVisitor(char[] arr,String file_name) {
		
		this.filename = file_name;
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(arr);

		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		cu = (CompilationUnit) parser.createAST(null);

	}
	
	/*
	public boolean visit(VariableDeclarationFragment  node) {
		
		System.out.println("====> " + this.filename + " <<|>> " + 
				cu.getLineNumber(node.getStartPosition()) + 
				" <<|>> VariableDeclarationFragment <<|>> " + node.getName());
		return true;
	
	}
	*/
	
	public boolean visit(VariableDeclarationStatement  node) {
		
		System.out.println("====> " + this.filename + " <<|>> " + 
				cu.getLineNumber(node.getStartPosition()) + 
				" <<|>> VariableDeclarationStatement <<|>> " + node.getType());
		return true;
	
	}
	
	
	public void parse() {
		cu.accept(this);
	}
}

