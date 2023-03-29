# ASSEMBLER CODE

import re;file=open('Fill.asm',"r");x = file.readlines();file.close()
def c_instruction_to_binary(instruction):
    dest_dict,jump_dict,comp_dict= {'': '000','M': '001','D': '010','MD': '011','A': '100','AM': '101','AD': '110','AMD': '111'},{'': '000','JGT': '001','JEQ': '010','JGE': '011','JLT': '100','JNE': '101','JLE': '110','JMP': '111'},{
        '0': '0101010',
        '1': '0111111',
        '-1': '0111010',
        'D': '0001100',
        'A': '0110000',
        '!D': '0001101',
        '!A': '0110001',
        '-D': '0001111',
        '-A': '0110011',
        'D+1': '0011111',
        'A+1': '0110111',
        'D-1': '0001110',
        'A-1': '0110010',
        'D+A': '0000010',
        'A+D': '0000010',
        'D-A': '0010011',
        'A-D': '0000111',
        'D&A': '0000000',
        'A&D': '0000000',
        'D|A': '0010101',
        'A|D': '0010101',
        'M': '1110000',
        '!M': '1110001',
        '-M': '1110011',
        'M+1': '1110111',
        'M-1': '1110010',
        'D+M': '1000010',
        'D-M': '1010011',
        'M-D': '1000111',
        'D&M': '1000000',
        'D|M': '1010101'
    }
    dest, comp_jump = instruction.split('=') if '=' in instruction else ('',instruction);comp, jump = comp_jump.split(';') if ';' in comp_jump else (comp_jump, '');return '111'  + comp_dict[comp] + dest_dict[dest] + jump_dict[jump]
PreDefinedSymbols,UNQ_LBLS=['R0','R1','R2','R3','R4','R5','R6','R7','R8','R9','R10','R11','R12','R13','R14','R15'],{'SCREEN':'16384','KBD':'24576','SP' :'0','LCL':'1','ARG' :'2','THIS':'3','THAT':'4'}
SYMnVAR,NO_WHITE_SPACE,NO_LBL_BRACKETS,LABLES,A_INSTRUCTIONS,MODIFIED_NO_WHITE_SPACE,A_INSTRUCTIONS_BINARY,C_INSTRUCTION_BINARY,HACK_FILE,SYMBOL_TABLE=[],[],[],[],[],[],[],[],[],{}
Numbers_for_Ains,NO_WHITE_SPACE=[str(i) for i in range(32768)],[i[:i.index('/')] if '/' in i else i for i in [re.sub("\n|\t|\s", "", i.replace(" ","")) for i in x if i!="\n" and i[0]!='/']]
NO_LBL_BRACKETS,LABLES=((re.sub(r"\[|\(|\'|\ |\)|\]",'',str(NO_WHITE_SPACE))).split(',')),(re.findall(r'\((.*?)\)',str(NO_WHITE_SPACE)))
for i in NO_WHITE_SPACE: 
    if '@' in i:
        i=i.replace("@","")
        if i not in A_INSTRUCTIONS:
            A_INSTRUCTIONS.append(i)
            if i not in Numbers_for_Ains and i not in SYMnVAR : SYMnVAR.append(i)
            if i not in Numbers_for_Ains and i not in LABLES and i not in PreDefinedSymbols and i not in UNQ_LBLS:PreDefinedSymbols.append(i)
for i in SYMnVAR:
    if i in PreDefinedSymbols : SYMBOL_TABLE.update({i:PreDefinedSymbols.index(i)})
    if i in LABLES: SYMBOL_TABLE.update({i:NO_LBL_BRACKETS.index(i)-LABLES.index(i)})
    if i in UNQ_LBLS:SYMBOL_TABLE.update({i:UNQ_LBLS[i]})
for i in NO_WHITE_SPACE:
    if "(" in i: continue
    if "@" in i :
        i=i.replace("@","")
        if i not in Numbers_for_Ains : i=str(SYMBOL_TABLE[i])
        A_INSTRUCTIONS_BINARY.append(((16-len(bin(int(i))[2:]))*'0')+(bin(int(i))[2:])),HACK_FILE.append(((16-len(bin(int(i))[2:]))*'0')+(bin(int(i))[2:]));i='@'+i
    else:HACK_FILE.append(c_instruction_to_binary(i)) , C_INSTRUCTION_BINARY.append(c_instruction_to_binary(i))
    MODIFIED_NO_WHITE_SPACE.append(i)

for i in HACK_FILE:
    print(i)
