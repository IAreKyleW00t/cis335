TITLE	CIS 335 - Project 2
INCLUDE	Irvine32.inc

;---------------------------------------;
;   Name:           Kyle Colantonio     ;
;   CSU ID:         2595744             ;
;   Assignment:     2                   ;
;	Description:	N/A					;
;   Date:           2-10-2015           ;
;										;
;   Updated:        2-11-2015           ;
;---------------------------------------;


.data
	area1   DWORD   01234567h
	area2   SBYTE   -7

.code
	main PROC
		mov     eax,area1
		add     eax,89EFh
		mov     bl,area2
		mov     bh,87h
		add     bx,ax
		mov     cx,bx
		sub     cx,6789h
		and     eax,0f0f0f0fh
		mov     edx,0
		mov     dl, BYTE PTR area1
		
		exit
	main ENDP
END main