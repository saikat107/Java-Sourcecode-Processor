package edu.columbia.cs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
//import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

import edu.columbia.cs.simpleASTParser.JavaASTTokenizer;

public class JavaFileProcessor extends ASTVisitor{
	private String javaFileContent = null;
	private String javaFilePath = "";
	private List<String> javaTokenList = null;
	private CompilationUnit cu;
	private boolean javaDocOnly = false;
	private String codeTreeStr = null;
	private String tokenizedCodeType = null;
	private String tokenizedCodeString = null;
	private String javaDocTreeString = null;
	private String javaDocFilteredText = null;
	private String javaDocFullString = null;
	public boolean parsed = true;
	public Node node = null;
	
	public String extractJavaDocFullText(Node node) {
		String javaDocText = "";
		if (node == null) {
			parsed = false;
			return null;
		}
		if (node.isLeafNode()) return node.getLabel();
		else {
			for (Node child : node.getChildren()) {
				javaDocText += (extractJavaDocFullText(child).trim() + " ");
			}
		}
		return javaDocText;
	}
	
	public String tokenizedTypeCode() {
		return this.tokenizedCodeType;
	}
	
	public String getTokenizedCode() {
		return tokenizedCodeString;
	}
	
	public String getCodeTree() {
		return codeTreeStr;
	}
	
	public String getJavaDocFilteredText() {
		return javaDocFilteredText;
	}
	
	public String getJavaDocTree() {
		return javaDocTreeString;
	}
	
	public String getJavaDocFullString() {
		return javaDocFullString;
	}
	
	public String extractJavaDocText(Node node) {
		if (node == null) {
			parsed = false;
			return null;
		}
		String javaDocText = "";
		for (Node child : node.getChildren()) {
			//if(child.isLeafNode()) continue;
			if (child.getChild(0).getType().compareToIgnoreCase("tagName") != 0) {
				for (Node grandChild : child.getChildren()) {
					for (Node grandGrandChild :  grandChild.getChildren()) {
						if (grandGrandChild.getType().compareToIgnoreCase("text") == 0) {
							javaDocText += (grandGrandChild.getLabel() + " ");
						}
					}
				}
			}
		}
		return javaDocText;
	}
	
	public boolean isJavaDocOnly() {
		return javaDocOnly;
	}
	
	public JavaFileProcessor(String fileContent, boolean javaDocOnly) {
		this.javaFileContent = fileContent;
		this.javaDocOnly = javaDocOnly;
	}
	
	public JavaFileProcessor(File javaFile) throws FileNotFoundException {
		Scanner scanner = new Scanner(javaFile);
		String fileString = "";
		while (scanner.hasNextLine()) {
			fileString = fileString + "\n" + scanner.nextLine();
		}
		scanner.close();
		this.javaFileContent = fileString;
		this.javaFilePath = javaFile.getAbsolutePath();
	}
	
	public String getFileContent() {
		return javaFileContent;
	}
	
	public String getFilePath() {
		return javaFilePath;
	}
	
	public List<String> getTokenList() {
		return javaTokenList;
	}
	
	public void process() {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		parser.setCompilerOptions(options);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(this.javaFileContent.toCharArray());
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		String[] sources = { "" };
		String[] classpath = { System.getProperty("java.home") + "/lib/rt.jar" };
		parser.setUnitName(this.javaFilePath);
		parser.setEnvironment(classpath, sources, new String[] { "UTF-8" }, true);	
		cu = (CompilationUnit) parser.createAST(null);
		cu.accept(this);
		
		if (!javaDocOnly) {
			JavaASTTokenizer tokenizer = new JavaASTTokenizer(this.javaFileContent);
			try {
				tokenizer.tokenize();
				this.tokenizedCodeString = tokenizer.tokenizedText();
				this.tokenizedCodeType = tokenizer.typedCode();
			} catch (Exception ex) {
				parsed = false;
			}
		}
		//dfsVisit(cu.getRoot(), 0);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes" })
	public void dfsVisit(ASTNode node, int tabNo) {
		if (node == null) return;
		List<StructuralPropertyDescriptor> descriptorList = node.structuralPropertiesForType();
        for (StructuralPropertyDescriptor descriptor : descriptorList) {
            Object child = node.getStructuralProperty(descriptor);
            if (child instanceof List) {
            	for(int i = 0; i < tabNo; i++) System.out.print("\t");
            	System.out.println(node.getNodeType() + " " + descriptor.getId());
                for (Object c : (List)child) {
                	dfsVisit((ASTNode)c, tabNo  + 1);
                }
            } else if (child instanceof ASTNode) {
            	for(int i = 0; i < tabNo; i++) System.out.print("\t");
            	System.out.println(node.getNodeType() + " " + descriptor.getId());
                dfsVisit((ASTNode) child, tabNo + 1);
            } else if (child != null) {
            	for(int i = 0; i < tabNo; i++) System.out.print("\t");
            	System.out.println(node.getNodeType() + " " + descriptor.getId() + "\t" + child.toString());
            }
        }
	}
	
	@SuppressWarnings({"unchecked", "rawtypes" })
	public Node createTree(ASTNode node) {
		if (node == null) {
			parsed = false;
			return null;
		}
		Node root = new Node(String.valueOf(node.getNodeType()), "");
		List<StructuralPropertyDescriptor> descriptorList = node.structuralPropertiesForType();
        for (StructuralPropertyDescriptor descriptor : descriptorList) {
            Object child = node.getStructuralProperty(descriptor);
            if (child instanceof List) {
            	// System.out.println(node.getNodeType() + " " + descriptor.getId());
                for (Object c : (List)child) {
                	Node childNode = createTree((ASTNode)c);
                	childNode.setType(descriptor.getId());
                	root.addChild(childNode);
                }
            } else if (child instanceof ASTNode) {
            	// System.out.println(node.getNodeType() + " " + descriptor.getId());
            	Node childNode = createTree((ASTNode)child);
            	childNode.setType(descriptor.getId());
            	root.addChild(childNode);
            } else if (child != null) {
            	Node childNode = new Node(descriptor.getId() , child.toString());
            	// System.out.println(node.getNodeType() + " " + descriptor.getId() + "\t" + child.toString());
            	root.addChild(childNode);
            }
        }
        return root;
	}
	
	 @SuppressWarnings("unused")
	private void serializeChildList(
			 List<ASTNode> children, StructuralPropertyDescriptor descriptor, int tabNo){
         if (children.size() < 1) {
             return;
         }
         for (ASTNode node : children) {
             dfsVisit(node, tabNo + 1);
         }
     }
	
	public boolean visit(MethodDeclaration root) {	
		if (javaDocOnly) {
			Javadoc javadoc = root.getJavadoc();
			node = createTree(javadoc);
			javaDocFilteredText = extractJavaDocText(node);
			javaDocTreeString = "JavaDocRoot " + serializeTree(node);
			javaDocFullString = extractJavaDocFullText(node);
		} else {
			if (root.getParent().toString().compareTo(cu.getRoot().toString()) == 0) {			
				node = createTree(root);
				codeTreeStr = "ROOT " + serializeTree(node);
			}
		}
		return true;
	}
	
	public String serializeTree(Node root) {
		if (root == null) {
			parsed = false;
			return null;
		}
		String returnStr = "";
		List<Node> children = root.getChildren();
		returnStr += ("` " + root.getType() + " ");
		if(children.size() == 0){
			Object name = null;
			if(name == null){
				name = root.getLabel();
			}
			returnStr += ("` " + name + " `` `` ");
		}
		else{
			for(Node child : children){
				returnStr += serializeTree(child);
			}
			returnStr += " `` ";
		}
		return returnStr;
	}

}
