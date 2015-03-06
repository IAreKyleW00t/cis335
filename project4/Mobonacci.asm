;---------------------------------------;
;   Name:           Kyle Colantonio     ;
;   CSU ID:         2595744             ;
;   Assignment:     4                   ;
;   Description:    Mobonacci           ;
;   Date:           3-17-2015           ;
;                                       ;
;   Updated:        3-6-2015            ;
;---------------------------------------;

TITLE   Recursive Mobonacci Calculator

.586
.MODEL  FLAT, C

.CODE
    Mobonacci PROC
        ; Move N into EAX ;
        push    ebp
        mov     ebp, esp
        mov     eax, [ebp+8]

        ; Check if EAX <= 1 ;
        cmp     eax, 2
        jb      one                 ; N == 1
        je      two                 ; N == 2
        ja      L1                  ; N > 1

        ; Recursively call Mobonacci ;
        L1:
            ; Create room for local variables ;
            sub     esp, 8

            ; Mobonacci(N - 1) * 2 ;
            sub     eax, 1
            push    eax
            call    Mobonacci
            mov     ebx, eax
            pop     eax

            shl     ebx, 1
            mov     [ebp-4], ebx

            ; Mobonacci(N - 2) / 2 ;
            sub     eax, 1
            push    eax
            call    Mobonacci
            mov     ebx, eax  ; store eax into ebp-4
            pop     eax

            shr     ebx, 1
            mov     [ebp-8], ebx

            mov     eax, 0
            add     eax, [ebp-4]
            add     eax, [ebp-8]

            ; Clean stack ;
            mov     esp, ebp

            ; Jump to ending and return value ;
            jmp     ending
            
        ; Return 1 ;
        one:
            mov     eax, 1
            jmp     ending

        ; Return 2 ;
        two:
            mov     eax, 2
            jmp     ending

        ; Return the value in EAX ;
        ending:
            pop     ebp
            ret                     ; return eax

    Mobonacci ENDP
END