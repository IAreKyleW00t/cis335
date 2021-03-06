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