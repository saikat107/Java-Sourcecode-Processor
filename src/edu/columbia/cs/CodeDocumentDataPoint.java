package edu.columbia.cs;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class CodeDocumentDataPoint implements Serializable{
	public static final long serialVersionUID = 2836909126359374521L;
	
	public String originalCode = null;
	public String tokenizedCode = null;
	public String originalJavaDoc = null;
	public String filteredJavaDoc = null;
	public String codeTree = null;
	public String javaDocTree = null;
	public String typeCodeTokens = null;
	public int pid = -1;
	public String fid = null;
	
	public String toString() {
		return "code : " + originalCode + "\n" + 
			   "tokens : " + tokenizedCode + "\n" +  
			   "tree : " + codeTree +  "\n" + 
			   "javadoc : " + originalJavaDoc + "\n" +
			   "javadoc_text : " + filteredJavaDoc +  "\n" + 
			   "javadoc_tree : " + javaDocTree +  "\n";
	}
	
	public JSONObject getJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("code", originalCode);
		object.put("tokens", tokenizedCode);
		object.put("tree", codeTree);
		object.put("javadoc", originalJavaDoc);
		object.put("jdoc_text", filteredJavaDoc);
		object.put("jdoc_tree", javaDocTree);
		object.put("pid", pid);
		object.put("fid", fid);
		return object;
	}

	public boolean hasAnyNull() {
		return (
				originalCode == null ||
				tokenizedCode == null ||
				originalJavaDoc == null ||
				filteredJavaDoc == null ||
				codeTree == null ||
				javaDocTree == null ||
				typeCodeTokens == null
				);
	}

}
