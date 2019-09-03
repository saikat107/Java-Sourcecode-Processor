package edu.columbia.cs.simpleASTParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

public class JavaASTTokenizer extends ASTVisitor{

	private String filePath;
	private String documentText;
	private String outputFile;
	
	private List<List<Token>> sequenceList;
	
	public JavaASTTokenizer(String content) {
		this.documentText = content;
		sequenceList = new ArrayList<>();
	}
	
	
	public boolean visit(MethodDeclaration node){
		//System.out.println(node);
		List <Token> tokenStream = traverse(documentText , node);
		sequenceList.add(tokenStream);
		return true;
	}
	
	public void tokenize() {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		parser.setCompilerOptions(options);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(this.documentText.toCharArray());
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		String[] sources = { "" };
		String[] classpath = { System.getProperty("java.home") + "/lib/rt.jar" };
		parser.setUnitName("A");
		parser.setEnvironment(classpath, sources, new String[] { "UTF-8" }, true);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.accept(this);
	}

	public static List<Token> traverse(String document, ASTNode node) throws IllegalArgumentException {
		return traverse(document, node, 1, 0);
	}

	private static final int[] ignore = { ASTNode.JAVADOC};
	@SuppressWarnings("rawtypes")
	private static List<Token> traverse(String document, ASTNode node, int depth, int offset) throws IllegalArgumentException {
		List<Token> tokens = new ArrayList<Token>();
		if (contains(ignore, node.getNodeType())) {
			return tokens;
		}
		int start = node.getStartPosition();
		int end = start + node.getLength();
		if (node.getLength() > 1000000000) {
			throw new IllegalArgumentException("Syntax error in current file near node at " + start + ", content: " + node.toString());
		}
		String type = "";
		if (node.getNodeType() == ASTNode.SIMPLE_NAME) {
			String nodeBody = node.toString().trim();
			ITypeBinding binding = ((SimpleName) node).resolveTypeBinding();
			if (binding != null)
				type = binding.getQualifiedName();
			if(nodeBody.trim().compareTo(type) != 0){
				if(! type.trim().endsWith(nodeBody))
					type = type + "_VAR";
			}
		}
		else if(node.getNodeType() == ASTNode.NUMBER_LITERAL){
			type = "NUMBER";
		}
		else if(node.getNodeType() == ASTNode.STRING_LITERAL){
			type = "STRING";
		}
		
		List list = node.structuralPropertiesForType();
		int pos = start;
		for (int i = 0; i < list.size(); i++) {
			StructuralPropertyDescriptor curr = (StructuralPropertyDescriptor) list.get(i);
			Object child = node.getStructuralProperty(curr);
			if (child instanceof ASTNode) {
				pos = addChild(document, node, depth, offset, tokens, type, pos, child);
			}
			else if (child instanceof List) {
				List children = (List) child;
				for (Object el : children) {
					if (el instanceof ASTNode) {
						pos = addChild(document, node, depth, offset, tokens, type, pos, el);
					}
				}
			}
			else if (child != null) {
				String childValue = child.toString().trim();
				if (childValue.matches("true|false") && node.getNodeType() != 9) {
					if (curr.toString().startsWith("SimpleProperty[org.eclipse.jdt.core.dom.WildcardType.")) {
						childValue = "?";
					} else {
						continue;
					}
				}
				else if (childValue.matches("[0-9]+") && node.getNodeType() != 34) continue;
			}
		}
		if (pos < end) {
			String intermediate = document.substring(pos, end);
			if (intermediate.startsWith("[")) {
				String rep = intermediate.trim().replaceAll("[\n\r]+", "");
				if (rep.length() > 2) intermediate = "[";
			}
			checkComment(intermediate, tokens, type, pos, depth);
		}
		return tokens;
	}

	
	

	private static int checkBlockComment(int depth, List<Token> tokens, String type, int pos, String intermediate) {
		if(intermediate.contains("/*")){
			int start_comment = intermediate.indexOf("/*");
			String beforeComment = intermediate.substring(0 , start_comment);
			checkComment(beforeComment, tokens, type, pos, depth);
			String comment = intermediate.substring(start_comment);
			int end_comment = comment.indexOf("*/");
			if(end_comment != -1){
				String afterComment = comment.substring(end_comment + 2);
				comment = comment.substring(0, end_comment + 2);
				//appendTerminals(comment,  depth + 1, tokens, "BLOCK_COMMENT", pos + start_comment);
				checkComment(afterComment, tokens, type, pos + start_comment + end_comment, depth);
			}
			return 1;
		}
		return 0;
	}


	private static int checkLineComment(int depth, List<Token> tokens, String type, int pos, String intermediate) {
		if(intermediate.contains("//")){
			int start_comment = intermediate.indexOf("//");
			String beforeComment = intermediate.substring(0 , start_comment);
			String comment = intermediate.substring(start_comment);
			int end_comment = comment.indexOf("\n");
			checkComment(beforeComment, tokens, type, pos, depth);
			if(end_comment != -1){
				String afterComment = comment.substring(end_comment);
				comment = comment.substring(0, end_comment);
				checkComment(afterComment, tokens, type, pos + start_comment + end_comment, depth);
			}
			return 1;
		}
		return 0;
	}
	
	
	

	private static void checkComment(String text, List<Token> tokens, String type, int pos, int depth) {
		int success = checkLineComment(depth, tokens, type, pos, text);
		if(success == 0 ){
			success = checkBlockComment(depth, tokens, type, pos, text);
		}
		if(success == 0){
			if(text.trim().startsWith("()")){
				appendTerminals("(", depth, tokens, type, pos);
				appendTerminals(")", depth, tokens, type, pos+1);
			}
			else{
				appendTerminals(text, depth, tokens, type, pos);
			}
		}
	}


	private static int addChild(String document, ASTNode node, int depth, int offset, List<Token> tokens, String type, int pos, Object child) {
		ASTNode childNode = (ASTNode) child;
		int childPos = childNode.getStartPosition();
//		ASTToken last = (ASTToken) tokens.get(tokens.size() - 1);
//		if (last.getContent().equals("[")) {
//			depth = last.getDepth();
//		}

		if (childPos > pos) {
			String intermediate = document.substring(pos, childPos);
			checkComment(intermediate, tokens, type, childPos, depth);
			pos += intermediate.length();
		}
		tokens.addAll(traverse(document, childNode, depth + 1, offset + pos));
		pos += childNode.getLength();
		if (pos != childNode.getLength() + childPos) {
			pos = childPos + childNode.getLength();
		}
		return pos;
	}

	private static void appendTerminals(String intermediate, int depth, List<Token> tokens, String type, int pos) {
		appendTerminals(intermediate, depth, tokens, type, pos, pos + intermediate.length());
	}

	private static void appendTerminals(String intermediate, int depth, List<Token> tokens, String type, int pos, int end) {
		if (!intermediate.trim().isEmpty()) {
			tokens.add(new ASTToken(intermediate.trim().replaceAll("[\n\r]+", ""), depth, type, pos, end - 1));
		}
	}

	private static boolean contains(int[] ignore, int nodeType) {
		for (int i : ignore) if (i == nodeType) return true;
		return false;
	}

	public String typedCode() {
		String tokenSequence = "";
		List<Token> sequence = sequenceList.get(0);
		int tokenLength = sequence.size();
		Token []tokenList = new ASTToken[tokenLength];
		tokenList = sequence.toArray(tokenList);
		for (int i = 0; i < tokenLength; i++){
			String word = ((ASTToken)tokenList[i]).getContent();
			if(JavaKeywords.isKeyWord(word)){
				((ASTToken)tokenList[i]).setType("");
			}
			else{
				if(i < tokenLength -1){
					String nextWord = ((ASTToken)tokenList[i + 1]).getContent();
					if (nextWord.trim().compareTo("(")==0 || nextWord.trim().compareTo("()")==0){
						((ASTToken)tokenList[i]).setType("METHOD_NAME");
					}
				}
			}
		}
		for (Token token : tokenList){
			tokenSequence += (token.text() + " ");
		}
		return tokenSequence;
	}

	public String tokenizedText() {
		String tokenSequence = "";
		List<Token> sequence = sequenceList.get(0);
		int tokenLength = sequence.size();
		Token []tokenList = new ASTToken[tokenLength];
		tokenList = sequence.toArray(tokenList);
		for (int i = 0; i < tokenLength; i++){
			String word = ((ASTToken)tokenList[i]).getContent();
			/*if(JavaKeywords.isKeyWord(word)){
				((ASTToken)tokenList[i]).setType("");
			}
			else{
				if(i < tokenLength -1){
					String nextWord = ((ASTToken)tokenList[i + 1]).getContent();
					if (nextWord.trim().compareTo("(")==0 || nextWord.trim().compareTo("()")==0){
						((ASTToken)tokenList[i]).setType("METHOD_NAME");
					}
				}
			}*/
			tokenSequence += word + " ";
		}
		return tokenSequence;
	}
	
	public void printOutputToFile() throws IOException {
		
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)));
		for(List<Token> sequence : sequenceList){
			
			
			int tokenLength = sequence.size();
			Token []tokenList = new ASTToken[tokenLength];
			tokenList = sequence.toArray(tokenList);
			for (int i = 0; i < tokenLength; i++){
				String word = ((ASTToken)tokenList[i]).getContent();
				if(JavaKeywords.isKeyWord(word)){
					((ASTToken)tokenList[i]).setType("");
				}
				else{
					if(i < tokenLength -1){
						String nextWord = ((ASTToken)tokenList[i + 1]).getContent();
						if (nextWord.trim().compareTo("(")==0 || nextWord.trim().compareTo("()")==0){
							((ASTToken)tokenList[i]).setType("METHOD_NAME");
						}
					}
				}
			}
			
			
			writer.print("<METHOD_START> ");
			for (Token token : tokenList){
				writer.print("\"" + token.text() + "\" , ");
			}
			writer.println(" <METHOD_END>");
		}
		writer.close();
	}
}