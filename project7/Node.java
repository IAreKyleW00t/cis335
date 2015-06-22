import java.util.ArrayList;
import java.util.List;

public class Node<T extends Comparable<T>> {
	private final T data;
	private List<Node<T>> children;
	
	/**
	 * Node construction which creates
	 * an empty list of children.
	 **/
	public Node(final T data) {
		this.data = data;
		this.children = new ArrayList<Node<T>>();
	}
	
	/**
	 * Node construction which copies the provided
	 * list of children.
	 **/
	public Node(final T data, List<Node<T>> children) {
		this.data = data;
		this.children = new ArrayList<Node<T>>(children);
	}
	
	/**
	 * Returns the data/value of the Node.
	 **/
	public final T data() {
		return data;
	}
	
	/**
	 * Returns the list of children Nodes.
	 **/
	public List<Node<T>> children() {
		return children;
	}

	/**
	 * Returns a specific index from the list
	 * of children.
	 **/
	public Node<T> getChild(int index) {
		return children.get(index);
	}
	
	/**
	 * Adds a child Node to the list of children.
	 * Returns true if successful.
	 **/
	public boolean addChild(Node<T> node) {
		return children.add(node);
	}
	
	/**
	 * Removes a child Node from the list of children
	 * and returns the Node that was removed.
	 **/
	public Node<T> removeChild(int index) {
		return children.remove(index);
	}
	
	/**
	 * Output the String representation of the Node
	 * by getting the data/value of the Node and all
	 * children nodes.
	 **/
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(data + " ");
		for (Node<T> child : children) {
			sb.append(child + " ");
		}
		
		return sb.toString().trim();
	}
}