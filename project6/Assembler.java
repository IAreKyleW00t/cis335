import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class Assembler {
	private static SICXE table = null;
	private static File source, obj, lst;
	
	/**
	 * Main method
	 */
	public static void main(String[] args) throws Exception {
		
		/* Check for valid arguments */
		if (args.length != 1) {
			System.err.println("Usage: ./Assembler <*.asm>"); //Print usage
		}
		
		/* Save SIC/XE files */
		source = new File(args[0]);
		obj = new File(source.getName().substring(0, source.getName().lastIndexOf('.')) + ".obj");
		lst = new File(source.getName().substring(0, source.getName().lastIndexOf('.')) + ".lst");
		
		/* ================================================== */
		/* ===================== PASS 1 ===================== */
		/* ================================================== */
		
		String line; int count = 1;
		BufferedReader br = new BufferedReader(new FileReader(source));
		while ((line = br.readLine()) != null) {
			String[] str = line.trim().split("\\s+"); //Split by spaces
			
			/* Convert any custom SIC/XE statements to real 
			 * SIC/XE statements before we attempt to use them. */
				for (int i = 0; i < str.length; i++) {
					str[i] = str[i].replace("AX", "A").replace("BX", "B").replace("LX", "L").replace("SX", "S").replace("TX", "T").replace("XX", "X");
					if (str[i].equalsIgnoreCase("LD") || str[i].equalsIgnoreCase("+LD")) {
						try {
							String[] temp = str[i+1].split(",");
							str[i] = str[i] + temp[0].charAt(0);
							str[i+1] = temp[1];
							break; //Done
						} catch (ArrayIndexOutOfBoundsException e) {
							br.close();
							System.err.println("Error in " + source.getName() + " at line " + count + "\n\tInvalid instruction; LD requires 2 arguments.");
							System.exit(1);
						}
					} else if (str[i].equalsIgnoreCase("ST") || str[i].equalsIgnoreCase("+ST")) {
						try {
							String[] temp = str[i+1].split(",");
							str[i] = str[i] + temp[1].charAt(0);
							str[i+1] = temp[0];
							break; //Done
						} catch (ArrayIndexOutOfBoundsException e) {
							br.close();
							System.err.println("Error in " + source.getName() + " at line " + count + "\n\tInvalid instruction; ST requires 2 arguments.");
							System.exit(1);
						}
					}
				}
			
			if (str.length == 3) { //contains label
				/* If our statement is "START" then create a new SIC/XE table.
				 * We can safely put this here because we know the first line will
				 * always be the "START" instruction. */
				if (str[1].equalsIgnoreCase("START")) {
					if (table != null) {
						br.close();
						System.err.println("Error in " + source.getName() + " at line " + count + "\n\tSTART cannot be called twice.");
						System.exit(1);
					}
					table = new SICXE(str[0], Integer.parseInt(str[2])); //Create new SIC/XE Table
				}
			}
			
			/* Add each Statement to the table. */
			try {
				table.add(array2str(str, " "), line);
			} catch (Exception e) {
				br.close();
				System.err.println("Error in " + source.getName() + " at line " + count + "\n\t" + e.getMessage());
				System.exit(1);
			}
			count++;
			
		}
		br.close(); //Close BufferedReader
		
		/* ================================================== */
		/* ===================== PASS 2 ===================== */
		/* ================================================== */
		
		count = 1;
		for (Statement s : table.statements()) {
			Statement next = null;
			if (count >= table.statements().size()-1) {
				table.get(table.statements().size()-1);
			} else {
				next = table.get(count);
			}
			
			if (s.instruction() != null) {
				boolean[] nixbpe = s.nixbpe();
				int left = s.instruction().opcode();
				int middle = 0x0;
				int end = 0x000, eend = 0x00000;
				
				if (s.instruction().mnemonic().equals("COMPR") || s.instruction().mnemonic().equals("DIVR") || s.instruction().mnemonic().equals("MULR") || s.instruction().mnemonic().equals("SUBR")) {
					Register r1 = parseRegister(s.args()[0]);
					Register r2 = parseRegister(s.args()[1]);
					s.opcode(String.format("%02X%01X%01X", left, r1.register(), r2.register()));
				} else if (s.instruction().mnemonic().equals("CLEAR") || s.instruction().mnemonic().equals("TIXR")) {
					Register r = parseRegister(s.args()[0]);
					s.opcode(String.format("%02X%01X%01X", left, r.register(), 0x0));
				} else {
					
					/* Calculate the value of NIXBPE */
					if (nixbpe[0] && nixbpe[1]) { //Normal; 11----
						left += 0x03;
					} else if (nixbpe[0] && !nixbpe[1]) { //Indirect; 10----
						left += 0x02;
					} else if (!nixbpe[0] && nixbpe[1]) { //Immediate; 01----
						left += 0x01;
					}

					if (!nixbpe[2] && !nixbpe[3] && !nixbpe[4] && !nixbpe[5]) { //--0000
						middle = 0b0000;
					} else if (!nixbpe[2] && !nixbpe[3] && !nixbpe[4] && nixbpe[5]) { //--0001
						middle = 0b0001;
					} else if (!nixbpe[2] && !nixbpe[3] && nixbpe[4] && !nixbpe[5]) { //--0010
						middle = 0b0010;
					} else if (!nixbpe[2] && nixbpe[3] && !nixbpe[4] && !nixbpe[5]) { //--0100
						middle = 0b0100;
					} else if (nixbpe[2] && !nixbpe[3] && !nixbpe[4] && !nixbpe[5]) { //--1000
						middle = 0b1000;
					} else if (nixbpe[2] && !nixbpe[3] && !nixbpe[4] && nixbpe[5]) { //--1001
						middle = 0b1001;
					} else if (nixbpe[2] && !nixbpe[3] && nixbpe[4] && !nixbpe[5]) { //--1010
						middle = 0b1010;
					} else if (nixbpe[2] && nixbpe[3] && !nixbpe[4] && !nixbpe[5]) { //--1100
						middle = 0b1100;
					}
					
					if (nixbpe[5]) {
						if (s.args()[0].startsWith("#")) {
							Statement tmp = table.getByLabel(s.args()[0].substring(1));
							if (tmp != null) {
								eend = Integer.parseInt(tmp.location(), 16);
							} else {
								try {
									eend = Integer.parseInt(s.args()[0].substring(1));
								} catch (NumberFormatException e) {
									System.err.println("Error in " + source.getName() + " at line " + count + "\n\tInvalid instruction; Incorrect number format.");
									System.exit(1);
								}
							}
						} else {
							Statement tmp = table.getByLabel(s.args()[0]);
							if (tmp == null) {
								System.err.println("Error in " + source.getName() + " at line " + count + "\n\tInvalid instruction; " + s.args()[0] + " is not a valid label.");
								System.exit(1);
							}

							eend = Integer.parseInt(tmp.location(), 16);
						}
						
						s.opcode(String.format("%02X%01X%05X", left, middle, eend).toUpperCase());
					} else {
						if (s.args().length == 2) {
							Statement tmp = table.getByLabel(s.args()[0]);
							if (tmp == null) {
								System.err.println("Error in " + source.getName() + " at line " + count + "\n\tInvalid instruction; " + s.args()[0] + " is not a valid label.");
								System.exit(1);
							}
							
							end = Integer.parseInt(tmp.location(), 16) - Integer.parseInt(next.location(), 16);
							
							/* Check base */
							if (end > 2047 || end < -2048) {
								Statement base = table.getByDirective("BASE");
								if (base == null) {
									System.err.println("Error in " + source.getName() + " at line " + count + "\n\tInvalid instruction; Bad BASE reference.");
									System.exit(1);
								}
								
								Statement b = table.getByLabel(base.args()[0]);
								middle = 0b1100;
								end = Integer.parseInt(tmp.location(), 16) - Integer.parseInt(b.location(), 16);
							}
							s.opcode(String.format("%02X%01X%03X", left, middle, end & 0xfff));
						} else if (s.args().length == 1) {
							if (s.args()[0].startsWith("#")) {
								Statement tmp = table.getByLabel(s.args()[0].substring(1));
								if (tmp != null) {
									end = Integer.parseInt(tmp.location(), 16) - Integer.parseInt(next.location(), 16);
								} else {
									end = Integer.parseInt(s.args()[0].substring(1));
								}
							} else if (s.args()[0].startsWith("@")) {
								Statement tmp = table.getByLabel(s.args()[0].substring(1));
								if (tmp == null) {
									System.err.println("Error in " + source.getName() + " at line " + count + "\n\tInvalid instruction; Indirect mode can only be used on variables.");
									System.exit(1);
								}
								
								end = Integer.parseInt(tmp.location(), 16) - Integer.parseInt(next.location(), 16);
							} else {
								Statement tmp = table.getByLabel(s.args()[0]);
								if (tmp == null) {
									System.err.println("Error in " + source.getName() + " at line " + count + "\n\tInvalid instruction; " + s.args()[0] + " is not a valid label.");
									System.exit(1);
								}
								
								end = Integer.parseInt(tmp.location(), 16) - Integer.parseInt(next.location(), 16);

								/* Check base */
								if (end > 2047 || end < -2048) {
									Statement base = table.getByDirective("BASE");
									if (base == null) {
										System.err.println("Error in " + source.getName() + " at line " + count + "\n\tInvalid instruction; Bad BASE reference.");
										System.exit(1);
									}
									
									Statement b = table.getByLabel(base.args()[0]);
									middle = 0b0100;
									end = Integer.parseInt(tmp.location(), 16) - Integer.parseInt(b.location(), 16);
								}
							}
							s.opcode(String.format("%02X%01X%03X", left, middle, end & 0xfff));
						} else {
							s.opcode(String.format("%02X%01X%03X", left, middle, end));
						}
					}
				}
			}
			count++;
		}

		/* Create Listing and Object files */;
		Statement start = table.getByDirective("START");
		Statement end = table.getByDirective("END");
		
		PrintWriter lout = new PrintWriter(lst);
		for (Statement s : table.statements()) {
			lout.println(s.toString());
		}
		lout.close();
		
		PrintWriter oout = new PrintWriter(obj);
		oout.println(String.format("H%s\t%06X%06X", table.name(), Integer.parseInt(start.location(), 16), Integer.parseInt(end.location(), 16)));
		
		String op = "";
		count = 0;
		while (count < table.statements().size()) {
			int saddr = Integer.parseInt(table.get(count).location(), 16);
			
			while (op.length() <= 60 && count < table.statements().size()) {
				String o = table.get(count).opcode();
				if (o == null)
					o = "";
				
				if (op.length() + o.length() > 60)
					break;
				op += o;
				count++;
			}
			
			oout.println(String.format("T%06X%02X%s", saddr, op.length() / 2, op));
			op = "";
		}
		
		oout.println(String.format("E%06x", Integer.parseInt(start.location(), 16)));
		
		oout.close();
		
	}
	
	/**
	 * Helper method to convert generic Arrays into a Strings.
	 **/
	private static String array2str(Object[] objs, String delim) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < objs.length; i++) {
			sb.append(objs[i]);
			if (i != objs.length-1)
				sb.append(delim);
		}
		
		return sb.toString().trim();
	}

	/**
	 * Parses the raw Directive from a String.
	 **/
	private static Register parseRegister(String str) {
		str = str.replaceAll("[\\+|\\@|\\#]", ""); //Remove any +,#, or @ symbols
		for (Register r : Register.values()) {
			if (str.equalsIgnoreCase(r.mnemonic())) {
				return r;
			}
		}
		return null;
	}
}