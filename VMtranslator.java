import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class VMtranslator {

    private static String pushSegment(String segment, String i){

        String res = "";
        res += "@"+segment+"\n";
        if (segment.equals(5) ){res += "D=A"+"\n";}
        else{res += "D=M"+"\n";}
        res += "@"+i+"\n";
        res += "A=D+A"+"\n";
        res += "D=M"+"\n";
        res += "@SP"+"\n";
        res += "A=M"+"\n";
        res += "M=D"+"\n";
        res += "@SP"+"\n";
        res += "M=M+1"+"\n";

        return res;
    }

    private static String popSegment(String segment, String i){

        String res = "";
        res += "@"+segment+"\n";
        if (segment.equals('5') ){res += "D=A"+"\n";}
        else{res += "D=M"+"\n";}
        res += "@"+i+"\n";
        res += "D=D+A"+"\n";
        res += "@R13"+"\n";
        res += "M=D"+"\n";
        res += "@SP"+"\n";
        res += "AM=M-1"+"\n";
        res += "D=M"+"\n";
        res += "@R13"+"\n";
        res += "A=M"+"\n";
        res += "M=D"+"\n";

        return res;
    }

    private static String pushConstant (String i){

        String res = "";

        res += "@"+i+"\n";
        res += "D=A"+"\n";
        res += "@SP"+"\n";
        res += "A=M"+"\n";
        res += "M=D"+"\n";
        res += "@SP"+"\n";
        res += "M=M+1"+"\n";
        
        return res;
    }

    private static String pushStatic(String i) {
        String res = "";
        res += "@" + i + "\n";
        res += "D=M" + "\n";
        res += "@SP" + "\n";
        res += "A=M" + "\n";
        res += "M=D" + "\n";
        res += "@SP" + "\n";
        res += "M=M+1" + "\n";
    
        return res;
    }
    
    private static String popStatic(String i) {
        String res = "";
        res += "@SP" + "\n";
        res += "AM=M-1" + "\n";
        res += "D=M" + "\n";
        res += "@" + i + "\n";
        res += "M=D" + "\n";
    
        return res;
    }
    
    private static String pushPointer(String i) {
        String res = "";
        if (i.equals("0")) {
            res += "@THIS" + "\n";
        } else if (i.equals("1")) {
            res += "@THAT" + "\n";
        }
        res += "D=M" + "\n";
        res += "@SP" + "\n";
        res += "A=M" + "\n";
        res += "M=D" + "\n";
        res += "@SP" + "\n";
        res += "M=M+1" + "\n";
    
        return res;
    }
    
    private static String popPointer(String i) {
        String res = "";
        res += "@SP" + "\n";
        res += "AM=M-1" + "\n";
        res += "D=M" + "\n";
        if (i.equals("0")) {
            res += "@THIS" + "\n";
        } else if (i.equals("1")) {
            res += "@THAT" + "\n";
        }
        res += "M=D" + "\n";
    
        return res;
    }
    

    private static String arithSegment (String segment){

        String res = "";
        if (segment.equals("sub")){ res+="@SP"+"\n"+"AM=M-1"+"\n"+"D=M"+"\n"+"A=A-1"+"\n"+"M=M-D"+"\n"; }
        else if (segment.equals("neg")){res+="@SP"+"\n"+"M=M-1"+"\n"+"M=-M";}
        else if (segment.equals("add")){res+="@SP"+"\n"+"AM=M-1"+"\n"+"D=M"+"\n"+"A=A-1"+"\n"+"M=M+D"+"\n";}
        else if (segment.equals("eq")){}
        else if (segment.equals("gt")){}
        else if (segment.equals("lt")){}
        else if (segment.equals("and")){res+="@SP"+"\n"+"AM=M-1"+"\n"+"D=M"+"\n"+"A=A-1"+"\n"+"M=M&D"+"\n";}
        else if (segment.equals("or")){res+="@SP"+"\n"+"AM=M-1"+"\n"+"D=M"+"\n"+"A=A-1"+"\n"+"M=M|D"+"\n";}
        else if (segment.equals("not")){res+="@SP"+"\n"+"M=M-1"+"\n"+"M=!M"+"\n";}
        return res;
        
    }


    public static void main(String[] args) throws IOException {
        
        BufferedReader reader = new BufferedReader(new FileReader("BasicTest.vm"));
        List<String> lines = new ArrayList<>();
        String line = reader.readLine();

        String outputFileName = "Assembly.asm"; 
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));


        while (line != null) {
            lines.add(line);
            line = reader.readLine();
        }
        reader.close();
        
        ArrayList<String> Segment = new ArrayList<>();
        Segment.add("add");
        Segment.add("sub");
        Segment.add("neg");
        Segment.add("eq");
        Segment.add("gt");
        Segment.add("lt");
        Segment.add("and");
        Segment.add("or");
        Segment.add("not");

        HashMap<String,String> segmentMap = new HashMap<>();
        segmentMap.put("temp", "5" );
        segmentMap.put("local", "LCL" );
        segmentMap.put("argument","ARG" );
        segmentMap.put("this","THIS" );
        segmentMap.put("that","THAT" );
        segmentMap.put("static","16" );


        for (String s : lines){
            String res="";

            if (s.equals("") || s.equals("\n") || s.startsWith("/")) continue;

            else{
                if (s.contains("constant")){

                    res = pushConstant(s.split(" ")[2]);

                }

                else if (s.contains("temp") || s.contains("argument") || s.contains("local") || s.contains("that") || s.contains("this")){

                    if (s.contains("pop")){  res = popSegment(segmentMap.get(s.split(" ")[1]),s.split(" ")[2]); }
                    else if(s.contains("push")){  res = pushSegment(segmentMap.get(s.split(" ")[1]),s.split(" ")[2]); }
                
                }

                else if (s.contains("static")){

                    if (s.contains("pop")){ res = popStatic(s.split(" ")[2]); }
                    else if (s.contains("push")){ res = pushStatic(s.split(" ")[2]); }

                }

                else if (s.contains("pointer")){

                    if (s.contains("pop")){ res = popPointer(s.split(" ")[2]); }
                    else if (s.contains("push")){ res = pushPointer(s.split(" ")[2]); }

                }

                else if (Segment.contains(s)){ res = arithSegment(s); }

            }

            writer.write(res);
            writer.newLine();

        }
        
        writer.close();
        reader.close();

    }
}