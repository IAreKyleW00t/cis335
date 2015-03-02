TITLE   CIS 335 - Project 3
INCLUDE Irvine32.inc

;---------------------------------------;
;   Name:           Kyle Colantonio     ;
;   CSU ID:         2595744             ;
;   Assignment:     3                   ;
;   Description:    Reversing a String  ;
;   Date:           2-24-2015           ;
;                                       ;
;   Updated:        3-2-2015            ;
;---------------------------------------;

.DATA
    source  BYTE    "CIS 335/535 is a great course", 0
    revstr  BYTE    SIZEOF source DUP(0)
    revwrd  BYTE    SIZEOF source DUP(0)
    ecxbkp  DWORD   ?

.CODE
    main    PROC
        ; Print the original string ;
        mov     edx, 0
        mov     edx, OFFSET source
        call    WriteString
        call    Crlf
        
        ; Clear registers ;
        mov     esi, SIZEOF source
        sub     esi, 2      ; skip the NULL terminator byte
        mov     edi, 0
        mov     ecx, SIZEOF source
        mov     eax, 0
        
        ; reverse the string char by char from source to revstr ;
        L1:
            ; move the character in source at index ESI into register AL ;
            mov     al, source[esi]

            ; move the character in AL into revstr at index EDI
            mov     revstr[edi], al

            ; Increment/Decrement pointers ;
            dec     esi
            inc     edi
        loop    L1
        mov     revstr[edi], 0      ; Add NUL character

        ; Print revstr ;
        mov     edx, 0
        mov     edx, OFFSET revstr
        call    WriteString
        call    Crlf
        
        ; Clear registers ;
        mov     esi, 0
        mov     edi, 0
        mov     ecx, SIZEOF revstr
        mov     eax, 0
        mov     ebx, 0
        
        ; reverse each word in revstr and store in revwrd ;
        L2:
            mov     al, revstr[esi]

            ; Check if al is a space ;
            cmp     al, 32
            je      space

            ; Check if al is a NUL character ;
            cmp     al, 0
            je      ending

            ; Increment ESI if we haven't reached a space ;
            inc     esi
        loop L2

        space:
            ; Backup ECX and then set ECX = ESI ;
            ; Increment through (ECX - EDI) ;
            mov     ecxbkp, ecx
            mov     ecx, esi
            sub     ecx, edi
            mov     ebx, esi
            dec     esi ; skip the space character

            L3:
                ; move the character in revstr at index ESI into AL ;
                mov     al, revstr[esi]

                ; move the character in AL into revwrd at index EDI ;
                mov     revwrd[edi], al
                
                ; Increment/Decrement pointers ;
                dec     esi
                inc     edi
            loop L3
            mov     revwrd[edi], 32 ; Add space

            ; Restore L2-loop values and jump back into L2 ;
            mov     ecx, ecxbkp
            mov     esi, ebx
            inc     esi ; move 1 index foward
            mov     edi, ebx
            inc     edi ; move 1 index forward
            jmp     L2

        ending:
            ; Increment through (ECX - EDI) ;
            mov     ecx, esi
            sub     ecx, edi
            mov     ebx, esi
            dec     esi         ; skip the space character

            L4:
                ; move the character in revstr at index ESI into AL ;
                mov     al, revstr[esi]

                ; move the character in AL into revwrd at index EDI ;
                mov     revwrd[edi], al
                
                ; Increment/Decrement pointers ;
                dec     esi
                inc     edi
            loop L4
            mov     revwrd[edi], 0 ; Add NUL character
            
            ; Print revwrd ;
            mov     edx, 0
            mov     edx, OFFSET revwrd
            call    WriteString
            call    Crlf

        exit
    main ENDP
END main