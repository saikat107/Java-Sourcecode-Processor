package edu.columbia.cs;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Node {
	private String type;
	private String label;
	private Node parent;
	private List<Node> children;
	private int depth = 0;
	
	public Node(String type, String label) {
		this.type = type;
		this.label = label;
		this.parent = null;
		this.children = new ArrayList<Node>();
	}
	
	public void addChild(Node child) {
		this.children.add(child);
		child.parent = this;
		child.depth = this.depth + 1;
	}
	
	public boolean isLeafNode() {
		return this.children.isEmpty();
	}
	
	public boolean isRoot() {
		return this.parent == null;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	private void print(Node node, PrintStream out, int level) {
		for (int i = 0; i < level; i++) {
			out.print("\t");
		}
		out.println(node.type + "\t" + node.label);
		for (Node child : node.children) {
			print(child, out, level + 1);
		}
	}
	
	public void dfsPrint(PrintStream out) {
		print(this, out, 0);
	}
	
	public List<Node> getChildren() {
		return this.children;
	}
	
	public Node getChild(int position) {
		if (position >=  children.size()) {
			return null;
		}
		return children.get(position);
	}
	
}
