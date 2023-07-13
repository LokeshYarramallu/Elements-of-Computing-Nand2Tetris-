import java.io.*;

public class VM {

    private static int labelCounter = 0;

    private static String pushConstant(String i) {
        String res = "";
        res += "@" + i + "\n";
        res += "D=A" + "\n";
        res += "@SP" + "\n";
        res += "A=M" + "\n";
        res += "M=D" + "\n";
        res += "@SP" + "\n";
        res += "M=M+1" + "\n";
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

    private static String pushSegment(String segment, String i) {
        String res = "";
        res += "@" + i + "\n";
        res += "D=A" + "\n";
        if (segment.equals("local")) {
            res += "@LCL" + "\n";
        } else if (segment.equals("argument")) {
            res += "@ARG" + "\n";
        } else if (segment.equals("this")) {
            res += "@THIS" + "\n";
        } else if (segment.equals("that")) {
            res += "@THAT" + "\n";
        } else if (segment.equals("temp")) {
            res += "@R5" + "\n";
        }
        res += "A=D+M" + "\n";
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

    private static String popSegment(String segment, String i) {
        String res = "";
        res += "@" + i + "\n";
        res += "D=A" + "\n";
        if (segment.equals("local")) {
            res += "@LCL" + "\n";
        } else if (segment.equals("argument")) {
            res += "@ARG" + "\n";
        } else if (segment.equals("this")) {
            res += "@THIS" + "\n";
        } else if (segment.equals("that")) {
            res += "@THAT" + "\n";
        } else if (segment.equals("temp")) {
            res += "@R5" + "\n";
        }
        res += "D=D+M" + "\n";
        res += "@R13" + "\n";
        res += "M=D" + "\n";
        res += "@SP" + "\n";
        res += "AM=M-1" + "\n";
        res += "D=M" + "\n";
        res += "@R13" + "\n";
        res += "A=M" + "\n";
        res += "M=D" + "\n";
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

    private static String arithSegment(String segment) {
        String res = "";
        if (segment.equals("add")) {
            res += "@SP" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "A=A-1" + "\n" + "M=M+D" + "\n";
        } else if (segment.equals("sub")) {
            res += "@SP" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "A=A-1" + "\n" + "M=M-D" + "\n";
        } else if (segment.equals("neg")) {
            res += "@SP" + "\n" + "A=M-1" + "\n" + "M=-M" + "\n";
        } else if (segment.equals("eq")) {
            res += "@SP" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "A=A-1" + "\n"
                    + "D=M-D" + "\n" + "@EQ_TRUE" + labelCounter + "\n" + "D;JEQ" + "\n" + "@SP" + "\n" + "A=M-1" + "\n"
                    + "M=0" + "\n" + "@EQ_END" + labelCounter + "\n" + "0;JMP" + "\n" + "(EQ_TRUE" + labelCounter + ")\n"
                    + "@SP" + "\n" + "A=M-1" + "\n" + "M=-1" + "\n" + "(EQ_END" + labelCounter + ")\n";
            labelCounter++;
        } else if (segment.equals("gt")) {
            res += "@SP" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "A=A-1" + "\n"
                    + "D=M-D" + "\n" + "@GT_TRUE" + labelCounter + "\n" + "D;JGT" + "\n" + "@SP" + "\n" + "A=M-1" + "\n"
                    + "M=0" + "\n" + "@GT_END" + labelCounter + "\n" + "0;JMP" + "\n" + "(GT_TRUE" + labelCounter + ")\n"
                    + "@SP" + "\n" + "A=M-1" + "\n" + "M=-1" + "\n" + "(GT_END" + labelCounter + ")\n";
            labelCounter++;
        } else if (segment.equals("lt")) {
            res += "@SP" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "A=A-1" + "\n"
                    + "D=M-D" + "\n" + "@LT_TRUE" + labelCounter + "\n" + "D;JLT" + "\n" + "@SP" + "\n" + "A=M-1" + "\n"
                    + "M=0" + "\n" + "@LT_END" + labelCounter + "\n" + "0;JMP" + "\n" + "(LT_TRUE" + labelCounter + ")\n"
                    + "@SP" + "\n" + "A=M-1" + "\n" + "M=-1" + "\n" + "(LT_END" + labelCounter + ")\n";
            labelCounter++;
        } else if (segment.equals("and")) {
            res += "@SP" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "A=A-1" + "\n" + "M=D&M" + "\n";
        } else if (segment.equals("or")) {
            res += "@SP" + "\n" + "AM=M-1" + "\n" + "D=M" + "\n" + "A=A-1" + "\n" + "M=D|M" + "\n";
        } else if (segment.equals("not")) {
            res += "@SP" + "\n" + "A=M-1" + "\n" + "M=!M" + "\n";
        }
        return res;
    }

    private static String label(String label) {
        return "(" + label + ")\n";
    }

    private static String gotoLabel(String label) {
        return "@" + label + "\n0;JMP\n";
    }

    private static String ifGotoLabel(String label) {
        return "@SP\nAM=M-1\nD=M\n@" + label + "\nD;JNE\n";
    }

    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Dinesh\\Desktop\\ASSEMBLY CODE\\out1.vm"));
            BufferedWriter bw = new BufferedWriter(new FileWriter("output.asm"));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }
                String[] parts = line.split("\\s+");
                String command = parts[0];
                String arg1 = null;
                String arg2 = null;
                if (parts.length > 1) {
                    arg1 = parts[1];
                }
                if (parts.length > 2) {
                    arg2 = parts[2];
                }
                String asmCode = "";
                if (command.equals("push")) {
                    if (arg1.equals("constant")) {
                        asmCode = pushConstant(arg2);
                    } else if (arg1.equals("static")) {
                        asmCode = pushStatic(arg2);
                    } else if (arg1.equals("pointer")) {
                        asmCode = pushPointer(arg2);
                    } else {
                        asmCode = pushSegment(arg1, arg2);
                    }
                } else if (command.equals("pop")) {
                    if (arg1.equals("static")) {
                        asmCode = popStatic(arg2);
                    } else if (arg1.equals("pointer")) {
                        asmCode = popPointer(arg2);
                    } else {
                        asmCode = popSegment(arg1, arg2);
                    }
                } else if (command.equals("label")) {
                    asmCode = label(arg1);
                } else if (command.equals("goto")) {
                    asmCode = gotoLabel(arg1);
                } else if (command.equals("if-goto")) {
                    asmCode = ifGotoLabel(arg1);
                } else {
                    asmCode = arithSegment(command);
                }
                bw.write("// " + line + "\n");
                bw.write(asmCode);
            }
            br.close();
            bw.close();
            System.out.println("Translation completed successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
