public enum Directive {
	BASE("BASE", 0),
	BYTE("BYTE", 1),
	END("END", 0),
	NOBASE("NOBASE", 0),
	RESB("RESB", 1),
	RESW("RESW", 3),
	START("START", 0),
	WORD("WORD", 3);
	
	private final String mnemonic;
	private final int size;
	Directive(final String mnemonic, final int size) {
		this.mnemonic = mnemonic;
		this.size = size;
	}
	
	public final String mnemonic() {
		return mnemonic;
	}
	
	public final int size() {
		return size;
	}
}