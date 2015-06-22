import java.util.Scanner;

public class Interpreter {
	private static String[] tokens = null;
	private static String token = null;
	private static int index = 0;
	private static Node<String> root;
	
	public static void main(String[] args) throws Exception {
		Scanner in = new Scanner(System.in);
		
		while (true) {
			System.out.print("$ "); // Prompt
			
			/* Get user input */
			String line = in.nextLine();
			tokens = line.split("\\s+"); //Split by any number of spaces

			/* Skip empty lines */
			if (line.isEmpty())
				continue;
			
			/* Quit the program if our input equals a period */
			if (line.equals("."))
				break;
			
			/* Set initial values */
			index = 0; // Start index at 0
			
			/* Attempt to parse our expression */
			lexme();
			root = expr();
			
			/* This loop will make sure we get all tokens */
			while (index != tokens.length) {
				Node<String> op = new Node<String>(token); // Save the operand Node
				lexme();
				
				/* Run expr() again */
				Node<String> tmp = expr();
				
				/* Add our current root and term as children Nodes and
				 * then set it as our new root Node */
				op.addChild(root);
				op.addChild(tmp);
				root = op;
			}
			
			/* Output result */
			System.out.println("  " + root + " = " + eval(root));
		}
		
		/* Close Scanner */
		in.close();
	}

	/**
	 * Evaluate the Tree of Nodes by recursively calling itself and getting
	 * the children Nodes. It will continue to call itself until a finite Integer
	 * has been found and then trace back up.
	 **/
	private static int eval(Node<String> node) {
		if (node.data().equals("+"))
			return eval(node.getChild(0)) + eval(node.getChild(1));
		else if (node.data().equals("-"))
			return eval(node.getChild(0)) - eval(node.getChild(1));
		else if (node.data().equals("*"))
			return eval(node.getChild(0)) * eval(node.getChild(1));
		else if (node.data().equals("/"))
			return eval(node.getChild(0)) / eval(node.getChild(1));
		
		return Integer.parseInt(node.data());
	}
	
	/**
	 * Method to handle Addition and Subtraction.
	 **/
	private static Node<String> expr() {
		Node<String> left, right;
		left = term(); // Parse the left term

		/* If our token is a + or -, then find next token and add that
		 * to the stack before we add the operand. */
		if (token.equals("+") || token.equals("-")) {
			Node<String> op = new Node<String>(token); // Save the operand Node
			lexme();
			right = term(); // Parse the right term

			/* Add the children to the operand Node */
			op.addChild(left);
			op.addChild(right);
			return op;
		}
		
		return left;
	}
	
	/**
	 * Method to handle Multiplication and Division.
	 **/
	private static Node<String> term() {
		Node<String> left, right;
		left = factor(); // Parse the left factor
		
		/* If our token is a * or /, then find next token and add that
		 * to the stack before we add the operand. */
		if (token.equals("*") || token.equals("/")) {
			Node<String> op = new Node<String>(token); // Save the operand Node
			lexme();
			right = factor(); // Parse the right factor
			
			/* Add the children to the operand Node */
			op.addChild(left);
			op.addChild(right);
			return op;
		}
		
		return left;
	}
	
	/**
	 * Method to search for any parenthesis or return
	 * a finite number.
	 **/
	private static Node<String> factor() {
		if (isInteger(token)) {			
			Node<String> num = new Node<String>(token); // Finite number Node
			
			if (index != tokens.length)
				lexme(); // Move to the next token
			
			return num;
		} else if (token.equals("(")) {
			lexme(); // Skip the opening parenthesis
			Node<String> expr = expr(); // Parse the expression within our parenthesis
			
			/* Handle long parenthesis */
			if (!token.equals(")")) {
				Node<String> op = new Node<String>(token); // Save the operand Node
				lexme();
				
				/* Run term() again */
				Node<String> tmp = expr();
				
				/* Add our current expr and tmp as children Nodes and
				 * then set it as our new expr Node */
				op.addChild(expr);
				op.addChild(tmp);
				expr = op;
			}
			
			if (index != tokens.length)
				lexme(); // Skip our ending parenthesis
			
			return expr;
		} else { // Invalid operand; throw error message
			System.err.println("Invalid token at index " + index + "; " + token + ".");
			System.exit(1);
			return null; // This will never be called because of System.exit(1)
		}
	}
	
	/**
	 * Move to the next token in our Array of tokens.
	 **/
	private static void lexme() {
		token = tokens[index++];
	}
	
	/**
	 * Determines if a String is a valid Integer.
	 **/
	private static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}