TITLE   CIS 335 - Project 1
INCLUDE Irvine32.inc

;---------------------------------------;
;   Name:           Kyle Colantonio     ;
;   CSU ID:         2595744             ;
;   Assignment:     1                   ;
;   Description:    N/A                 ;
;   Date:           1-15-2015           ;
;                                       ;
;   Updated:        2-11-2015           ;
;---------------------------------------;

.data
    myMessage BYTE "CIS 335 Kyle Colantonio 2595744",0dh,0ah,0

.code
    main PROC
        call    Clrscr

        mov     edx,OFFSET myMessage
        call    WriteString

        exit
    main ENDP
END main