package edu.columbia.cs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.columbia.cs.simpleASTParser.MethodInvocationASTVisitor;

public class Main {
	private static final String wSpaces = "[ \\n\\t\\r]+";
	private PrintStream originalCodeFile = null;
	private PrintStream tokenizedCodeFile = null;
	private PrintStream variableTypedCodeFile = null;
	private PrintStream codeTreeStringFile = null;
	private PrintStream originalJavaDocStringFile = null;
	private PrintStream filteredJavaDocStringFile = null;
	private PrintStream javaDocTreeStringFile = null;
	private PrintStream fidPidFile = null;
	
	public void initializeAll(String outputBase) throws IOException {
		originalCodeFile = new PrintStream(new File(outputBase + "/code.original"));
		tokenizedCodeFile = new PrintStream(new File(outputBase + "/code.tokenized"));
		variableTypedCodeFile = new PrintStream(new File(outputBase + "/code.type"));
		codeTreeStringFile = new PrintStream(new File(outputBase + "/code.tree"));
		originalJavaDocStringFile = new PrintStream(new File(outputBase + "/javadoc.original"));
		filteredJavaDocStringFile = new PrintStream(new File(outputBase + "/javadoc.filtered"));
		javaDocTreeStringFile = new PrintStream(new File(outputBase + "/javadoc.tree"));
		fidPidFile = new PrintStream(new File(outputBase + "/fid-pid.txt"));
	}
	
	public void printToFile(CodeDocumentDataPoint point) {
		originalCodeFile.println(point.originalCode.replaceAll(wSpaces, " "));
		tokenizedCodeFile.println(point.tokenizedCode.replaceAll(wSpaces, " "));
		variableTypedCodeFile.println(point.typeCodeTokens.replaceAll(wSpaces, " "));
		codeTreeStringFile.println(point.codeTree.replaceAll(wSpaces, " "));
		originalJavaDocStringFile.println(point.originalJavaDoc.replaceAll(wSpaces, " "));
		filteredJavaDocStringFile.println(point.filteredJavaDoc.replaceAll(wSpaces, " "));
		javaDocTreeStringFile.println(point.javaDocTree.replaceAll(wSpaces, " "));
		fidPidFile.println(point.fid + "\t" + point.pid);
		originalCodeFile.flush();
		tokenizedCodeFile.flush();
		variableTypedCodeFile.flush();
		codeTreeStringFile.flush();
		originalJavaDocStringFile.flush();
		filteredJavaDocStringFile.flush();
		javaDocTreeStringFile.flush();
		fidPidFile.flush();
	}
	
	public void closeAll() {
		originalCodeFile.close();
		tokenizedCodeFile.close();
		variableTypedCodeFile.flush();
		codeTreeStringFile.close();
		originalJavaDocStringFile.close();
		filteredJavaDocStringFile.close();
		javaDocTreeStringFile.close();
		fidPidFile.close();
	}
	
	
	public void start(String base, String fpPath) throws IOException, JSONException {
		Scanner scanner = new Scanner(new File(fpPath));
		int count = 0;
		while (scanner.hasNextLine()) {
			count++;
			if (count % 100 == 0) {
				System.out.println(count);
			}
			String input = scanner.nextLine().trim();
			String []parts = input.split("_");
			String fid = parts[0].trim();
			int pid = Integer.parseInt(parts[1]);
			String methodBody = readFile(base + "f/" + input);
			String javaDoc = readFile(base + "c/" + input);
			CodeDocumentDataPoint point = new CodeDocumentDataPoint();
			String javaContentWithJavaDoc = "public class A {\n" + javaDoc + "\n" + methodBody + "\n}";
			JavaFileProcessor docProcessor = new JavaFileProcessor(javaContentWithJavaDoc, true);
			docProcessor.process();
			if (!docProcessor.parsed) {
				System.out.println("fault " + input);
				continue;
			}
			String javaContentWithMethod = "public class A {\n" + methodBody + "\n}";
			JavaFileProcessor methodProcessor = new JavaFileProcessor(javaContentWithMethod, false);
			methodProcessor.process();
			if(!methodProcessor.parsed) {
				System.out.println("fault " + input);
				continue;
			}
			point.originalJavaDoc = docProcessor.getJavaDocFullString();
			point.filteredJavaDoc = docProcessor.getJavaDocFilteredText();
			point.javaDocTree = docProcessor.getJavaDocTree();
			point.originalCode = methodBody;
			point.tokenizedCode = methodProcessor.getTokenizedCode();
			point.codeTree = methodProcessor.getCodeTree();
			point.typeCodeTokens = methodProcessor.tokenizedTypeCode();
			point.pid = pid;
			point.fid = fid;
			if (point.hasAnyNull()) {
				System.out.println("fault " + input);
				continue;
			}
			printToFile(point);
		}
		scanner.close();
	}
	public JSONObject readJson(String filePath) throws IOException, JSONException {
		Scanner scanner = new Scanner(new File(filePath));
		String text = "";
		while (scanner.hasNextLine()) {
			text += (scanner.nextLine() + " ");
		}
		scanner.close();
		JSONObject object = new JSONObject(text);
		
		return object;
	}
	
	public static void main(String[] args) throws JSONException, Exception {
		String base = args[0];
		String fpPath = args[1];
		String output = args[2];
		Main main = new Main();
		main.initializeAll(output);
		main.start(base, fpPath);
		main.closeAll();
	}

	private String readFile(String path) throws FileNotFoundException {
		String text = "";
		Scanner scanner = new Scanner(new File(path));
		while (scanner.hasNextLine()) {
			text += (scanner.nextLine().trim() + "\n");
		}
		scanner.close();
		return text;
	}

}
