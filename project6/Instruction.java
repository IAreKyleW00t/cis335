public enum Instruction {
	ADD("ADD", 3, 0x18, "A <-- A + (m..m+2)", (byte)0b0000, true, 1),
	ADDR("ADDR", 2, 0x90, "r2 <-- (r2) + (r1)", (byte)0b0100, false, 2),
	CLEAR("CLEAR", 2, 0xB4, "r1 <-- 0", (byte)0b0100, false, 1),
	COMP("COMP", 3, 0x28, "(A) : (m..m+2)", (byte)0b0001, true, 1),
	COMPR("COMPR", 2, 0xA0, "(r1) : (r2)", (byte)0b0101, false, 2),
	DIV("DIV", 3, 0x24, "A <-- A / (m..m+2)", (byte)0b0000, true, 1),
	DIVR("DIVR", 2, 0x9C, "r2 <-- (r2) / (r1)", (byte)0b0100, false, 2),
	J("J", 3, 0x3C, "PC <-- m", (byte)0b0000, true, 1),
	JEQ("JEQ", 3, 0x30, "PC <-- m if CC set to =", (byte)0b0000, true, 1),
	JGT("JGT", 3, 0x34, "PC <-- m if CC set to >", (byte)0b0000, true, 1),
	JLT("JLT", 3, 0x38, "PC <-- m if CC set to <", (byte)0b0000, true, 1),
	JSUB("JSUB", 3, 0x48, "L <-- (PC); PC <-- m", (byte)0b0000, true, 1),
	LDA("LDA", 3, 0x00, "A <-- (m..m+2)", (byte)0b0000, true, 1),
	LDB("LDB", 3, 0x68, "B <-- (m..m+2)", (byte)0b0100, true, 1),
	LDCH("LDCH", 3, 0x50, "A[rightmost byte] <-- (m)", (byte)0b0000, true, 2),
	LDL("LDL", 3, 0x08, "L <-- (m..m+2)", (byte)0b0000, true, 1),
	LDS("LDS", 3, 0x6C, "S <-- (m..m+2)", (byte)0b0100, true, 1),
	LDT("LDT", 3, 0x74, "T <-- (m..m+2)", (byte)0b0100, true, 1),
	LDX("LDX", 3, 0x04, "X <-- (m..m+2)", (byte)0b0000, true, 1),
	MUL("MUL", 4, 0x20, "A <-- A * (m..m+2)", (byte)0b0000, true, 1),
	MULR("MULR", 2, 0x98, "r2 <-- (r2) * (r1)", (byte)0b0100, false, 2),
	RD("RD", 3, 0xD8, "A[rightmost byte] <-- data from device specified by (m)", (byte)0b1000, true, 1),
	RSUB("RSUB", 3, 0x4C, "PC <-- (L)", (byte)0b0000, true, 0),
	STA("STA", 3, 0x0C, "m..m+2 <-- A", (byte)0b0000, true, 1),
	STB("STB", 3, 0x78, "m..m+2 <-- B", (byte)0b0100, true, 1),
	STCH("STCH", 3, 0x54, "m <-- (A)[rightmost byte]", (byte)0b0000, true, 2),
	STL("STL", 3, 0x14, "m..m+2 <-- L", (byte)0b0000, true, 1),
	STS("STS", 3, 0x7C, "m..m+2 <-- S", (byte)0b0100, true, 1),
	STT("STT", 3, 0x84, "m..m+2 <-- T", (byte)0b0100, true, 1),
	STX("STX", 3, 0x10, "m..m+2 <-- X", (byte)0b0000, true, 1),
	SUB("SUB", 3, 0x1C, "A <-- A - (m..m+2)", (byte)0b0000, true, 1),
	SUBR("SUBR", 2, 0x94, "r2 <-- (r2) - (r1)", (byte)0b0100, false, 2),
	TD("TD", 3, 0xE0, "Test device specified by (m)", (byte)0b1001, true, 1),
	TIX("TIX", 3, 0x2C, "X <-- (X) + 1; (X): (m..m+2)", (byte)0b0001, true, 1),
	TIXR("TIXR", 2, 0xB8, "X <-- (X) + 1; (X): (r1)", (byte)0b0101, false, 1),
	WD("WD", 3, 0xDC, "Device specified by (m) <-- (A)[rightmost byte]", (byte)0b1000, true, 1);
	
	private final String mnemonic, effect;
	private final int opcode, size, args;
	private final byte notes;
	private final boolean extendable;
	Instruction(final String mnemonic, final int size, final int opcode, final String effect, final byte notes, final boolean extendable, final int args) {
		this.mnemonic = mnemonic;
		this.size = size;
		this.opcode = opcode;
		this.effect = effect;
		this.notes = notes;
		this.extendable = extendable;
		this.args = args;
	}
	
	public final String mnemonic() {
		return mnemonic;
	}
	
	public final int size() {
		return size;
	}
	
	public final int opcode() {
		return opcode;
	}
	
	public final String effect() {
		return effect;
	}
	
	public final byte notes() {
		return notes;
	}
	
	public final boolean extendable() {
		return extendable;
	}
	
	public final int args() {
		return args;
	}
}