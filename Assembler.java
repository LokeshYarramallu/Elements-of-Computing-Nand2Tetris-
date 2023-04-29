import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileWriter;
import java.util.*;

public class Assembler {

    static String C2Binary(String instruction){

        HashMap<String, String> destDict = new HashMap<>();
        destDict.put("", "000");
        destDict.put("M", "001");
        destDict.put("D", "010");
        destDict.put("MD", "011");
        destDict.put("A", "100");
        destDict.put("AM", "101");
        destDict.put("AD", "110");
        destDict.put("AMD", "111");

        HashMap<String, String> compDict = new HashMap<>();
        compDict.put("0", "0101010");
        compDict.put("1", "0111111");
        compDict.put("-1", "0111010");
        compDict.put("D", "0001100");
        compDict.put("A", "0110000");
        compDict.put("!D", "0001101");
        compDict.put("!A", "0110001");
        compDict.put("-D", "0001111");
        compDict.put("-A", "0110011");
        compDict.put("D+1", "0011111");
        compDict.put("A+1", "0110111");
        compDict.put("D-1", "0001110");
        compDict.put("A-1", "0110010");
        compDict.put("D+A", "0000010");
        compDict.put("A+D", "0000010");
        compDict.put("D-A", "0010011");
        compDict.put("A-D", "0000111");
        compDict.put("D&A", "0000000");
        compDict.put("A&D", "0000000");
        compDict.put("D|A", "0010101");
        compDict.put("A|D", "0010101");
        compDict.put("M", "1110000");
        compDict.put("!M", "1110001");
        compDict.put("-M", "1110011");
        compDict.put("M+1", "1110111");
        compDict.put("M-1", "1110010");
        compDict.put("D+M", "1000010");
        compDict.put("D-M", "1010011");
        compDict.put("M-D", "1000111");
        compDict.put("D&M", "1000000");
        compDict.put("C|M", "1010101");

        HashMap<String, String> jumpDict = new HashMap<>();
        jumpDict.put("", "000");
        jumpDict.put("JGT", "001");
        jumpDict.put("JEQ", "010");
        jumpDict.put("JGE", "011");
        jumpDict.put("JLT", "100");
        jumpDict.put("JNE", "101");
        jumpDict.put("JLE", "110");
        jumpDict.put("JMP", "111");

        String[] destAndCompJump = instruction.split("=");
        String dest = destAndCompJump.length > 1 ? destAndCompJump[0] : "";
        String compJump = destAndCompJump[destAndCompJump.length - 1];
        String[] compAndJump = compJump.split(";");
        String comp = compAndJump[0];
        String jump = compAndJump.length > 1 ? compAndJump[1] : "";
        String binary = "111" + destDict.get(dest) + compDict.get(comp) + jumpDict.get(jump);
        return binary;
    }

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader("fill.asm"));
        List<String> lines = new ArrayList<>();
        String line = reader.readLine();

        while (line != null) {
            lines.add(line);
            line = reader.readLine();
        }
        reader.close();

        
        ArrayList<String> preDefinedSymbols = new ArrayList();
        preDefinedSymbols.add("R0");
        preDefinedSymbols.add("R1");
        preDefinedSymbols.add("R2");
        preDefinedSymbols.add("R3");
        preDefinedSymbols.add("R4");
        preDefinedSymbols.add("R5");
        preDefinedSymbols.add("R6");
        preDefinedSymbols.add("R7");
        preDefinedSymbols.add("R8");
        preDefinedSymbols.add("R9");
        preDefinedSymbols.add("R10");
        preDefinedSymbols.add("R11");
        preDefinedSymbols.add("R12");
        preDefinedSymbols.add("R13");
        preDefinedSymbols.add("R14");
        preDefinedSymbols.add("R15");

        HashMap<String, Integer> unqLbls = new HashMap<>();
        unqLbls.put("SCREEN", 16384);
        unqLbls.put("KBD", 24576);
        unqLbls.put("SP", 0);
        unqLbls.put("LCL", 1);
        unqLbls.put("ARG", 2);
        unqLbls.put("THIS", 3);
        unqLbls.put("THAT", 4);

        ArrayList<String> symnVar = new ArrayList<>();
        ArrayList<String> noWhiteSpace = new ArrayList<>();
        ArrayList<String> noLblBrackets = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<String> aInstructions = new ArrayList<>();
        ArrayList<String> modifiedNoWhiteSpace = new ArrayList<>();
        ArrayList<String> aInstructionsBinary = new ArrayList<>();
        ArrayList<String> cInstructionBinary = new ArrayList<>();
        ArrayList<String> hackFile = new ArrayList<>();

        HashMap<String, Integer> symbolTable = new HashMap<>();

        ArrayList<String> numbersForAins = new ArrayList<>();
        for (int i = 0; i < 32768; i++) {
            numbersForAins.add(Integer.toString(i));
        }

        for (String l : lines) {
            String v;
            if (l.equals("")) continue;
            if (l.contains(" ")) l=l.replace(" ", "");
            if (l.equals("\n") || l.charAt(0) == '/') { continue; }
            else {
                l = l.replaceAll("[\\n\\t\\s]", "");
                if (l.indexOf("/") != -1) { v = l.substring(0, l.indexOf("/"));}
                else { v = l; }
            }
            noWhiteSpace.add(v);
        }
        
        for (String s : noWhiteSpace) {
            s = s.replaceAll("[\\[\\]\\(\\)\\'\\ ]", "");
            noLblBrackets.addAll(Arrays.asList(s.split(",")));
        }     
        
        int lbl=0;
        for(String s : noWhiteSpace){
            if (s.charAt(0)=='('){
                s=s.replace("(", "");
                s=s.replace(")","");
                labels.add(lbl, s);
                lbl+=1;
            }
        }

        for (String s : noWhiteSpace){
            if (s.charAt(0)=='@'){
                s = s.replace("@", "");
                if (!Arrays.asList(aInstructions).contains(s)) {
                    aInstructions.add(s);
                    if (!numbersForAins.contains(s) && !symnVar.contains(s)) {
                        symnVar.add(s);
                    }
                    if (!numbersForAins.contains(s) && !labels.contains(s) && preDefinedSymbols.contains(s) && unqLbls.containsKey(s)){
                        preDefinedSymbols.add(s);
                    }
                }
            }
        }
        
        for (String s:symnVar){
            if (preDefinedSymbols.contains(s)){
                symbolTable.put(s, preDefinedSymbols.indexOf(s));
            }
            if (labels.contains(s)){
                symbolTable.put(s, (noLblBrackets.indexOf(s)-labels.indexOf(s)));
            }
            if (unqLbls.containsKey(s)){
                int k = (unqLbls.get(s));
                symbolTable.put(s,k);
            }
        }

        for (String s:noWhiteSpace){
            if (s.contains("(")){continue;}
            if (s.contains("@")){
                s = s.replace("@", "");
                if (!numbersForAins.contains(s)){s = Integer.toString(symbolTable.get(s));} 
                int S = Integer.parseInt(s);
                String aBin = Integer.toBinaryString(S);
                String Abin="";
                for (int i=0;i<16-aBin.length();i++){
                    Abin = Abin+"0";
                }
                Abin = Abin + aBin;
                aInstructionsBinary.add(Abin);
                hackFile.add(Abin);
                s = '@' + s;
            }
            else{
                hackFile.add(C2Binary(s).toString());
            }
            modifiedNoWhiteSpace.add(s);
        }
        
        FileWriter Hfile = new FileWriter("HackFile.hack");
        for(String s :hackFile){

            System.out.println(s);
            Hfile.write(s);
            Hfile.append("\n");
        }
        Hfile.close();;
        

    }
}