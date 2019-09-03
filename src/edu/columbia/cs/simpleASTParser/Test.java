package edu.columbia.cs.simpleASTParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;


public class Test {
	public static int testStaticInt = 10;
	public static void main(String args[]) throws IOException {
		int count = 0;
		//String infile  = "tests/input.java";
		try (BufferedReader br = new BufferedReader(new FileReader("tests/files.txt"))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	// process the line.
//		    	String infile = "tests/GPUImageLaplacianFilter.java";
		    	String infile = line;
		    	System.out.println(++count);
				String outFileName = "tests/all_method_body_new.txt";
				if(args.length > 0) 
		           infile = args[0];
				/*
				 * JavaASTTokenizer tokenizer = new JavaASTTokenizer(infile, outFileName);
				 * tokenizer.tokenize(); tokenizer.printOutputToFile();
				 */		    }
		} 
		
	 }
	
	private static void writeTree(File file, Stream<Token> lines) throws IOException {
		  try (BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
			   lines.forEachOrdered(x -> {
				    try {
					     fw.append(x.toString());
					     fw.append('\n');
				    } catch (IOException e) {
				    	System.err.println(e );
				    }
			   });
		  }
	}
	
	
	private static void parseVarDecl(char[] arr, String infile_name){

		VarDeclarationASTVisitor astv = new VarDeclarationASTVisitor(arr, infile_name);
		astv.parse();
	}
	
	private static void parseMethodInvoc(char[] arr, String infile_name){
		MethodInvocationASTVisitor astv = new MethodInvocationASTVisitor(arr, infile_name);
		astv.parse();
	}
	
	private static void parse(char[] arr, String infile_name){
		SimpleASTVisitor astv = new SimpleASTVisitor(arr, infile_name);
		astv.parse();
	}
	
}