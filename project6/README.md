Project 6
---------
#### Description
  A program that will convert SIC/XE assembly code into the object code. I will take an ASM file as input and output a Listing file and Object file. The program will only check for the following instructions, directives, and registers...
  
  Directives: `START`, `BYTE`, `WORD`, `RESB`, `RESW`, `END`, `BASE`, and `NOBASE`  
  Instructions: `ADDR`, `COMPR`, `SUBR`, `MULR`, `DIVR`, `ADD`, `SUB`, `MUL`, `DIV`, `COMP`, `J`, `JEQ`, `JGT`, `JLT`, `JSUB`, `LDCH`, `RSUB`, `TIX`, `TIXR`, `RD`, `TD`, `STCH`, `CLEAR`, `LD` (`LD*`), and `ST` (`ST*`)  
  Registers: `AX`, `BX`, `LX`, `SX`, `TX`, `XX`
  
  There is no output to the screen other than error message. Filenames will be relative to your input file (ie: main.asm will output to main.lst and main.obj). These files will be created automatically and be overwritten if they exist already.
  
#### Example Input (main.asm)
  ```
  COPY    START   0
  FIRST   ST      RETADR,LX
          LD      BX,#LENGTH
          BASE    LENGTH
  CLOOP  +JSUB    RDREC
          LD      AX,LENGTH
          COMP    #0
          JEQ     ENDFIL
         +JSUB    WRREC
          J       CLOOP
  ENDFIL  LD      AX,EOF
          ST      BUFFER,AX
          LD      AX,#3
          ST      LENGTH,AX
         +JSUB    WRREC
          J       @RETADR
  EOF     BYTE    C'EOF'
  RETADR  RESW    1
  LENGTH  RESW    1
  BUFFER  RESB    4096
  RDREC   CLEAR   XX
          CLEAR   AX
          CLEAR   SX
         +LD      TX,#4096
  RLOOP   TD      INPUT
          JEQ     RLOOP
          RD      INPUT
          COMPR   AX,SX
          JEQ     EXIT
          STCH    BUFFER,XX
          TIXR    TX
          JLT     RLOOP
  EXIT    ST      LENGTH,XX
          RSUB
  INPUT   BYTE    X'F3'
  WRREC   CLEAR   XX
          LD      TX,LENGTH
  WLOOP   TD      OUTPUT
          JEQ     WLOOP
          LDCH    BUFFER,XX
          WD      OUTPUT
          TIXR    TX
          JLT     WLOOP
          RSUB
  OUTPUT  BYTE    X'05'
          END     FIRST
  ```
#### Example Output (main.lst, main.obj)
  ```
  0000  COPY    START   0                 
  0000  FIRST   ST      RETADR,LX         17202D
  0003          LD      BX,#LENGTH        69202D
                BASE    LENGTH            
  0006  CLOOP  +JSUB    RDREC             4B101036
  000A          LD      AX,LENGTH         032026
  000D          COMP    #0                290000
  0010          JEQ     ENDFIL            332007
  0013         +JSUB    WRREC             4B10105D
  0017          J       CLOOP             3F2FEC
  001A  ENDFIL  LD      AX,EOF            032010
  001D          ST      BUFFER,AX         0F2016
  0020          LD      AX,#3             010003
  0023          ST      LENGTH,AX         0F200D
  0026         +JSUB    WRREC             4B10105D
  002A          J       @RETADR           3E2003
  002D  EOF     BYTE    C'EOF'            454F46
  0030  RETADR  RESW    1                 
  0033  LENGTH  RESW    1                 
  0036  BUFFER  RESB    4096              
  1036  RDREC   CLEAR   XX                B410
  1038          CLEAR   AX                B400
  103A          CLEAR   SX                B440
  103C         +LD      TX,#4096          75101000
  1040  RLOOP   TD      INPUT             E32019
  1043          JEQ     RLOOP             332FFA
  1046          RD      INPUT             DB2013
  1049          COMPR   AX,SX             A004
  104B          JEQ     EXIT              332008
  104E          STCH    BUFFER,XX         57C003
  1051          TIXR    TX                B850
  1053          JLT     RLOOP             3B2FEA
  1056  EXIT    ST      LENGTH,XX         134000
  1059          RSUB                      4F0000
  105C  INPUT   BYTE    X'F3'             F3
  105D  WRREC   CLEAR   XX                B410
  105F          LD      TX,LENGTH         774000
  1062  WLOOP   TD      OUTPUT            E32011
  1065          JEQ     WLOOP             332FFA
  1068          LDCH    BUFFER,XX         53C003
  106B          WD      OUTPUT            DF2008
  106E          TIXR    TX                B850
  1070          JLT     WLOOP             3B2FEF
  1073          RSUB                      4F0000
  1076  OUTPUT  BYTE    X'05'             05
                END     FIRST             
  ```
  
  ```
  HCOPY	000000001077
  T0000001D17202D69202D4B1010360320262900003320074B10105D3F2FEC032010
  T00001D1D0F20160100030F200D4B10105D3E2003454F46B410B400B44075101000
  T0010401DE32019332FFADB2013A00433200857C003B8503B2FEA1340004F0000F3
  T00105D1AB410774000E32011332FFA53C003DF2008B8503B2FEF4F000005
  E000000
  ```
