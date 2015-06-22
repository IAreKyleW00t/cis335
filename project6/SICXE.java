import java.util.ArrayList;
import java.util.List;

public class SICXE {
	private String name = null;
	private int location = 0;
	private String start = null;
	private List<Statement> statements = new ArrayList<Statement>();
	
	public SICXE(String name, int location) {
		this.name = name;
		this.location = location;
		this.start = Integer.toHexString(location);
	}
	
	/**
	 * Returns the name of the SIC/XE program.
	 **/
	public String name() {
		return name;
	}

	/**
	 * Returns the current location of the SIC/XE program.
	 **/
	public int location() {
		return location;
	}

	/**
	 * Returns the starting location of the SIC/XE program.
	 **/
	public String start() {
		return start;
	}

	/**
	 * Returns a list of all Statements in the SIC/XE program.
	 **/
	public List<Statement> statements() {
		return statements;
	}

	/**
	 * Adds a new Statement to the List.
	 **/
	public void add(String str, String raw) throws Exception {
		Statement s = new Statement(location, str, raw);
		statements.add(s);
		location += s.length();
	}
	
	/**
	 * Returns the first instance of a Statement by address location.
	 **/
	public Statement getByAddress(String location) {
		for (Statement s : statements) {
			if (s.location().equals(location))
				return s;
		}
		return null;
	}

	/**
	 * Returns the first instance of a Statement by label.
	 **/
	public Statement getByLabel(String label) {
		for (Statement s : statements) {
			if (s.label()!= null && s.label().equals(label))
				return s;
		}
		return null;
	}

	/**
	 * Returns the first instance of a Statement by instruction.
	 **/
	public Statement getByInstruction(String instruction) {
		for (Statement s : statements) {
			if (s.instruction() != null && s.instruction().mnemonic().equals(instruction))
				return s;
		}
		return null;
	}

	/**
	 * Returns the first instance of a Statement by directive.
	 **/
	public Statement getByDirective(String directive) {
		for (Statement s : statements) {
			if (s.directive() != null && s.directive().mnemonic().equals(directive))
				return s;
		}
		return null;
	}
	
	public Statement get(int index) {
		return statements.get(index);
	}

	/**
	 * Adds a new Statement to the List.
	 **/
	public void add(Statement s) {
		statements.add(s);
		location += s.length();
	}

	/**
	 * Returns a formatted String representation of the SIC/XE program.
	 **/
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("%s  %-32s  %s\n", "Addr", "Instruction", "Opcode"));
		sb.append("------------------------------------------------\n");
		
		for (Statement s : statements) {
			sb.append(s.toString() + "\n");
		}
		
		return sb.toString();
	}
}