public enum Register {
	A("A", 0, 3),
	X("X", 1, 3),
	L("L", 2, 3),
	B("B", 3, 3),
	S("S", 4, 3),
	T("T", 5, 3);
	
	private final String mnemonic;
	private final int register, size;
	Register(final String mnemonic, final int register, final int size) {
		this.mnemonic = mnemonic;
		this.register = register;
		this.size = size;
	}
	
	public final String mnemonic() {
		return mnemonic;
	}
	
	public final int register() {
		return register;
	}
	
	public final int size() {
		return size;
	}
}