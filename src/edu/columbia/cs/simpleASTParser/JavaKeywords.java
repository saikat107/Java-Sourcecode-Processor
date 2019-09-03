package edu.columbia.cs.simpleASTParser;

import java.util.Arrays;
import java.util.List;

public class JavaKeywords {
	public static final String[] keywords = {"abstract", "continue", "for", "new", "switch", "assert",  "default", "goto", "package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw", "byte",	"else",	"import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class",	"finally", "long", "strictfp", "volatile", "const", "float", "native", "super",	"while"};
	public static List<String> keywordList = Arrays.asList(keywords);
	
	public static boolean isKeyWord(String word){
		return keywordList.contains(word);
	}
}
