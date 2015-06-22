public class Statement {
	private String location = null;
	private String label = null;
	private Instruction instruction = null;
	private String[] args = null;
	private Directive directive = null;
	private String opcode = null;
	private int length = 0;
	private boolean[] nixbpe = {true, true, false, false, true, false};
	private String raw = null;
	
	public Statement(int location, String statement, String raw) throws Exception {
		this(location, statement.split("\\s+"), raw);
	}
	
	public Statement(int location, String[] statement, String raw) throws Exception {
		/* Save original Statement */
		this.raw = raw;
		
		/* Save the location of the statement */
		this.location = String.format("%04X", location);
		
		/* Parse Statement information */
		if (statement.length == 3) { //contains label
			this.label = statement[0];
			this.directive = parseDirective(statement[1]);
			this.instruction = parseInstruction(statement[1]);
			this.args = statement[2].split(",");

			/* Validate Data */
			if (this.directive != null && this.instruction != null)
				throw new Exception("Invalid statement; Cannot contain both a Directive AND Instruction.");
			else if (this.directive == null && this.instruction == null)
				throw new Exception("Invalid statement; Must contain a Directive OR Instruction.");
			
			if (this.directive != null) {
				if (this.args.length != 1)
					throw new Exception("Invalid directive; " + this.directive.mnemonic() + " must have only 1 argument.");
				
				if (this.directive.mnemonic().startsWith("RES") || this.directive.mnemonic().equals("START")) {
					try {
						Integer.parseInt(this.args[0]);
					} catch (NumberFormatException e) {
						throw new Exception("Invalid directive; " + this.directive.mnemonic() + " takes a single positive integer.");
					}
				} else {
					if (!this.args[0].startsWith("C") && !this.args[0].startsWith("X"))
						throw new Exception("Invalid directive; " + this.directive.mnemonic() + " can only take a Character or raw Hex as the data type.");
				}
			} else if (this.instruction != null) {
				if (this.args.length != this.instruction.args())
					throw new Exception("Invalid instruction; " + this.instruction.mnemonic() + " takes " + this.instruction.args() + " arguments. (" + this.instruction.effect() + ")");
				
				if (this.instruction.mnemonic().endsWith("R") && !this.instruction.mnemonic().equals("CLEAR") && !this.instruction.mnemonic().equals("TIXR")) {
					Register a = parseRegister(this.args[0]);
					Register b = parseRegister(this.args[1]);
					if (a == null || b == null)
						throw new Exception("Invalid instruction; " + this.instruction.mnemonic() + " requires 2 valid Registers. (" + array2str(Register.values(), " ") + ")");
				}
			}
			
			/* Check NIXBPE */
			if (statement[1].startsWith("+")) {
				if (this.instruction.extendable()) {
					this.nixbpe[5] = true;
					this.nixbpe[4] = false;
					this.length++;
				} else {
					throw new Exception("Invalid statement; " + this.instruction.mnemonic() + " cannot be used in extended mode.");
				}
			}
			
			for (String a : this.args) {
				if (a.startsWith("@"))
					this.nixbpe[1] = false;
				else if (a.startsWith("#")) {
					this.nixbpe[0] = false;
					try {
						Integer.parseInt(a.substring(1));
						this.nixbpe[4] = false;
					} catch (NumberFormatException e) {
						this.nixbpe[4] = true;
					}
				}
			}
			
			if (this.args.length == 2 && this.args[1].equals("X"))
				this.nixbpe[2] = true;
			
			if (this.directive != null) {
				/* Calculate opcode for Directives if possible */
				if (this.directive.mnemonic().equals("BASE") || this.directive.mnemonic().equals("NOBASE") || this.directive.mnemonic().equals("START")) {
					this.opcode = null; //no opcode
					this.length = this.directive.size();
				} else if (this.directive.mnemonic().equals("BYTE") || this.directive.mnemonic().equals("WORD")) {
					if (this.args[0].startsWith("C")) { //Character
						char[] values = this.args[0].substring(2, this.args[0].length()-1).toCharArray();
						if (values.length > 3)
							throw new Exception("Invalid directive; Cannot fit " + this.args[0] + " into " + this.label);
						
						this.opcode = ""; //Clear opcode
						this.length += this.directive.size() * values.length;
						
						/* Parse hex value */
						for (char c : values)
							this.opcode = this.opcode + String.format("%02X", (int)c);
					} else if (this.args[0].startsWith("X")) { //Hex
						char[] values = this.args[0].substring(2, this.args[0].length()-1).toCharArray();
						if (values.length > 6)
							throw new Exception("Invalid directive; Cannot fit" + this.args[0] + " into" + this.label);
						
						this.opcode = "";
						this.length += this.directive.size() * (values.length/2);
						
						for (char c : values)
							this.opcode += c;
					}
				} else if (this.directive.mnemonic().startsWith("RES")) {
					this.opcode = null; //no opcode
					this.length += this.directive.size() * Integer.parseInt(args[0]);
				}
			} else if (this.instruction != null) {
				this.opcode = null; //Calculated later
				this.length += this.instruction.size();
			}
		} else if (statement.length == 2) { //no label
			this.label = null;
			this.directive = parseDirective(statement[0]);
			this.instruction = parseInstruction(statement[0]);
			this.args = statement[1].split(",");
			
			/* Validate Data */
			if (this.directive != null && this.instruction != null)
				throw new Exception("Invalid statement; Cannot contain both a Directive AND Instruction.");
			else if (this.directive == null && this.instruction == null)
				throw new Exception("Invalid statement; Must contain a Directive OR Instruction.");
			
			if (this.directive != null) {
				if (this.args.length != 1)
					throw new Exception("Invalid directive; " + this.directive.mnemonic() + " must have only 1 argument.");
				
				if (!this.directive.mnemonic().equals("BASE") && !this.directive.mnemonic().equals("NOBASE") && !this.directive.mnemonic().equals("END"))
					throw new Exception("Invalid directive; " + this.directive.mnemonic() + " requies a label.");
			} else if (this.instruction != null) {
				if (this.args.length != this.instruction.args())
					throw new Exception("Invalid instruction; " + this.instruction.mnemonic() + " takes " + this.instruction.args() + " arguments. (" + this.instruction.effect() + ")");
				
				if (this.instruction.mnemonic().endsWith("R") && !this.instruction.mnemonic().equals("CLEAR") && !this.instruction.mnemonic().equals("TIXR")) {
					Register a = parseRegister(this.args[0]);
					Register b = parseRegister(this.args[1]);
					if (a == null || b == null)
						throw new Exception("Invalid instruction; " + this.instruction.mnemonic() + " requires 2 valid Registers. (" + array2str(Register.values(), " ") + ")");
				}
			}
			
			/* Check NIXBPE */
			if (statement[0].startsWith("+")) {
				if (this.instruction.extendable()) {
					this.nixbpe[5] = true;
					this.nixbpe[4] = false;
					this.length++;
				} else {
					throw new Exception("Invalid statement; " + this.instruction.mnemonic() + " cannot be used in extended mode.");
				}
			}
			
			for (String a : this.args) {
				if (a.startsWith("@"))
					this.nixbpe[1] = false;
				else if (a.startsWith("#")) {
					this.nixbpe[0] = false;
					try {
						Integer.parseInt(a.substring(1));
						this.nixbpe[4] = false;
					} catch (NumberFormatException e) {
						this.nixbpe[4] = true;
					}
				}
			}
			
			if (this.args.length == 2 && this.args[1].equals("X"))
				this.nixbpe[2] = true;
			
			if (this.directive != null) {
				this.opcode = null; //no opcode
				this.length = this.directive.size();
			} else if (this.instruction != null) {
				this.opcode = null; //Calculated later
				this.length += this.instruction.size();
			}
		} else { //single instruction

			this.label = null;
			this.directive = parseDirective(statement[0]);
			this.instruction = parseInstruction(statement[0]);
			this.args = new String[0]; //no arguments
			
			/* Validate Data */
			if (this.directive != null && this.instruction != null)
				throw new Exception("Invalid statement; Cannot contain both a Directive AND Instruction.");
			else if (this.directive == null && this.instruction == null)
				throw new Exception("Invalid statement; Must contain a Directive OR Instruction.");
			
			if (this.directive != null) {
				throw new Exception("Invalid directive; " + this.directive.mnemonic() + " requires 1 argument.");
			} else if (this.instruction != null) {
				if (this.args.length != this.instruction.args())
					throw new Exception("Invalid instruction; " + this.instruction.mnemonic() + " takes " + this.instruction.args() + " arguments. (" + this.instruction.effect() + ")");
			}
			
			/* Check NIXBPE */
			if (statement[0].startsWith("+")) {
				if (this.instruction.extendable()) {
					this.nixbpe[5] = true;
					this.nixbpe[4] = false;
					this.length++;
				} else {
					throw new Exception("Invalid statement; " + this.instruction.mnemonic() + " cannot be used in extended mode.");
				}
			}
			
			if (this.args.length == 2 && this.args[1].equals("X"))
				this.nixbpe[2] = true;
			
			nixbpe[4] = false;

			this.opcode = null; //Calculated later
			this.length += this.instruction.size();
		}
	}
	
	/**
	 * Returns the address/location of the Statement.
	 **/
	public String location() {
		return location;
	}

	/**
	 * Returns the label of the Statement.
	 **/
	public String label() {
		return label;
	}

	/**
	 * Returns the Instruction of the Statement.
	 * NULL if there is no Instruction.
	 **/
	public Instruction instruction() {
		return instruction;
	}

	/**
	 * Returns the arguments of the Statement.
	 **/
	public String[] args() {
		return args;
	}

	/**
	 * Returns the Directive of the Statement.
	 * NULL if there is no Directive.
	 **/
	public Directive directive() {
		return directive;
	}

	/**
	 * Returns the address/location of the Statement.
	 * NULL if opcode has not been set.
	 **/
	public String opcode() {
		return opcode;
	}

	/**
	 * Sets the opcode of the Statement.
	 **/
	public void opcode(String opcode) {
		this.opcode = opcode;
	}

	/**
	 * Returns the length of the Statement.
	 **/
	public int length() {
		return length;
	}

	/**
	 * Returns the NIXBPE flags for the Statement.
	 **/
	public boolean[] nixbpe() {
		return nixbpe;
	}
	
	/**
	 * Sets the NIXBPE flags for the Statement.
	 **/
	public void nixbpe(boolean[] nixbpe) {
		this.nixbpe = nixbpe;
	}
	
	/**
	 * Returns the original SIC/XE line.
	 **/
	public String raw() {
		return raw;
	}
	
	/**
	 * Parses raw Instruction from a String.
	 **/
	private Instruction parseInstruction(String str) {
		str = str.replaceAll("[\\+|\\@|\\#]", ""); //Remove any +,#, or @ symbols
		for (Instruction i : Instruction.values()) {
			if (str.equalsIgnoreCase(i.mnemonic())) {
				return i;
			}
		}
		return null;
	}

	/**
	 * Parses the raw Directive from a String.
	 **/
	private Directive parseDirective(String str) {
		str = str.replaceAll("[\\+|\\@|\\#]", ""); //Remove any +,#, or @ symbols
		for (Directive d : Directive.values()) {
			if (str.equalsIgnoreCase(d.mnemonic())) {
				return d;
			}
		}
		return null;
	}

	/**
	 * Parses the raw Directive from a String.
	 **/
	private Register parseRegister(String str) {
		str = str.replaceAll("[\\+|\\@|\\#]", ""); //Remove any +,#, or @ symbols
		for (Register r : Register.values()) {
			if (str.equalsIgnoreCase(r.mnemonic())) {
				return r;
			}
		}
		return null;
	}
	
	/**
	 * Helper method to convert generic Arrays into a Strings.
	 **/
	private String array2str(Object[] objs, String delim) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < objs.length; i++) {
			sb.append(objs[i]);
			if (i != objs.length-1)
				sb.append(delim);
		}
		
		return sb.toString().trim();
	}
	
	public String toString() {
		String loc = location;
		String op = opcode;
		if (directive != null && (directive.mnemonic().equals("BASE") || directive.mnemonic().equals("END"))) {
			loc = "    ";
		}
		if (opcode == null)
			op = "";
		
		return String.format("%s  %-32s  %s", loc, raw, op);
	}
}