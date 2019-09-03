package edu.columbia.cs.simpleASTParser;



import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class MethodInvocationASTVisitor extends ASTVisitor{
	
	private final CompilationUnit cu;
	private String filename;

	public MethodInvocationASTVisitor(char[] arr,String file_name) {
		
		this.filename = file_name;
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(arr);

		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		cu = (CompilationUnit) parser.createAST(null);

	}
	
	
	public boolean visit(PackageDeclaration  node) {
		
//		System.out.println("====> " + this.filename + " <<|>> " + 
//				cu.getLineNumber(node.getStartPosition()) + 
//				" <<|>> PackageDeclaration <<|>> " + node.getName() );
		return true;
	
	}
	
	
	
	public boolean visit(ImportDeclaration  node) {
		
//		System.out.println("====> " + this.filename + " <<|>> " + 
//				cu.getLineNumber(node.getStartPosition()) + 
//				" <<|>> ImportDeclaration <<|>> " + node.getName() );
		return true;
	
	}
	
	private class Node{
		ASTNode node;
		int level;
		public Node(ASTNode n, int l){
			this.node = n;
			this.level = l;
		}
		public int getLevel(){
			return level;
		}
		public ASTNode getNode(){
			return node;
		}
	}
	
	public boolean visit(IfStatement node){
		System.out.println(node.getNodeType());
		Queue<Node> queue = new ArrayDeque<Node>();
		queue.add(new Node(node, 0));
		while(!queue.isEmpty()){
			Node root = queue.poll();
			ASTNode rootNode = root.getNode();
			int l = root.getLevel();
			if(rootNode instanceof SimpleName){
				SimpleName sl = ((SimpleName) rootNode);
				System.out.println(sl.getClass() + " ---- " + sl.getIdentifier())	;
			}
			System.out.println(l + " " + rootNode.properties() + " " + rootNode.toString());
			List<ASTNode> children = getChildren(rootNode);
			for(ASTNode child:children){
				queue.add(new Node(child, l+1));
			}
		}
		return true;
	}
	
	public  List<ASTNode> getChildren(ASTNode node) {
	    List<ASTNode> children = new ArrayList<ASTNode>();
	    List list = node.structuralPropertiesForType();
	    for (int i = 0; i < list.size(); i++) {
	        Object child = node.getStructuralProperty((StructuralPropertyDescriptor)list.get(i));
	        if(child instanceof ASTNode){
	        	children.add((ASTNode) child);
	        }
	        else if (child instanceof List){
	        	List clist = (List)child;
	        	for(Object obj:clist){
	        		children.add((ASTNode)obj);
	        	}
	        }
	        else if(child != null){
	        	//System.out.println(child.getClass());
	        }
	    }
	    return children;
	}
	
	public boolean visit(MethodInvocation  node) {
		
		//System.out.println(">>>>");
		
//		if(node.getExpression()!=null) {
//			
//			Expression exp = node.getExpression();
//			
//			String name = exp.getClass().getTypeName();
//			
//			
//			System.out.println(name);
//				 
//		    
//			System.out.println("====> " + this.filename + " <<|>> " + 
//					cu.getLineNumber(node.getStartPosition()) + 
//					" <<|>> MethodInvocation <<|>> " + node.getExpression()
//					+ "--->" + node.getName()
//					+ "," + exp.getClass());
//		}
//		else {
//			System.out.println("====> " + this.filename + " <<|>> " + 
//					cu.getLineNumber(node.getStartPosition()) + 
//					" <<|>> MethodInvocation <<|>> " + node.getName());
//			
//		}
		
		/*
		
		System.out.println(this.filename + " <<|>> " + 
				cu.getLineNumber(node.getStartPosition()) + 
				"<<|>> MethodInvocation <<|>> " + node.getName());
		*/
		
		return true;
	}

	public void parse() {
		cu.accept(this);
	}
}

