TITLE   CIS 335 - Project 1
INCLUDE Irvine32.inc

;---------------------------------------;
;   Name:           Kyle Colantonio     ;
;   CSU ID:         2595744             ;
;   Assignment:     1                   ;
;   Description:    Printing a String   ;
;   Date:           1-27-2015           ;
;                                       ;
;   Updated:        3-2-2015            ;
;---------------------------------------;

.DATA
    myMessage   BYTE    "CIS 335 Kyle Colantonio 2595744", 0dh, 0ah, 0

.CODE
    main PROC
        call    Clrscr
        mov     edx, OFFSET myMessage
        call    WriteString

        exit
    main ENDP
END main