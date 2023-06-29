import java.io.*;

public class HackVMTranslator {
    private static int labelCounter = 0;
    private BufferedWriter outputFile;
    private String currentFileName;

    public HackVMTranslator(BufferedWriter writer) {
        this.outputFile = writer;
    }

    public void translateCommand(String command, String arg1, int arg2) throws IOException {
        CommandType commandType = getCommandType(command);
        switch (commandType) {
            case C_ARITHMETIC:
                translateArithmetic(command);
                break;
            case C_PUSH:
            case C_POP:
                translatePushPop(commandType, arg1, arg2);
                break;
            case C_LABEL:
                translateLabel(arg1);
                break;
            case C_GOTO:
                translateGoto(arg1);
                break;
            case C_IF:
                translateIf(arg1);
                break;
            case C_FUNCTION:
                translateFunction(arg1, arg2);
                break;
            case C_RETURN:
                translateReturn();
                break;
            case C_CALL:
                translateCall(arg1, arg2);
                break;
            default:
                throw new IllegalArgumentException("Invalid command: " + command);
        }
    }

    private CommandType getCommandType(String command) {
        if (command.equals("add") || command.equals("sub") || command.equals("neg") ||
                command.equals("eq") || command.equals("gt") || command.equals("lt") ||
                command.equals("and") || command.equals("or") || command.equals("not")) {
            return CommandType.C_ARITHMETIC;
        } else if (command.equals("push")) {
            return CommandType.C_PUSH;
        } else if (command.equals("pop")) {
            return CommandType.C_POP;
        } else if (command.equals("label")) {
            return CommandType.C_LABEL;
        } else if (command.equals("goto")) {
            return CommandType.C_GOTO;
        } else if (command.equals("if-goto")) {
            return CommandType.C_IF;
        } else if (command.equals("function")) {
            return CommandType.C_FUNCTION;
        } else if (command.equals("return")) {
            return CommandType.C_RETURN;
        } else if (command.equals("call")) {
            return CommandType.C_CALL;
        } else {
            throw new IllegalArgumentException("Invalid command: " + command);
        }
    }

    private void translateArithmetic(String command) throws IOException {
        String translatedCode;
        switch (command) {
            case "add":
                translatedCode = "@SP\n"
                        + "AM=M-1\n"
                        + "D=M\n"
                        + "A=A-1\n"
                        + "M=D+M\n";
                break;
            case "sub":
                translatedCode = "@SP\n"
                        + "AM=M-1\n"
                        + "D=M\n"
                        + "A=A-1\n"
                        + "M=M-D\n";
                break;
            case "neg":
                translatedCode = "@SP\n"
                        + "A=M-1\n"
                        + "M=-M\n";
                break;
            case "eq":
                translatedCode = "@SP\n"
                        + "AM=M-1\n"
                        + "D=M\n"
                        + "A=A-1\n"
                        + "D=M-D\n"
                        + "@TRUE" + getUniqueLabel() + "\n"
                        + "D;JEQ\n"
                        + "@SP\n"
                        + "A=M-1\n"
                        + "M=0\n"
                        + "@CONTINUE" + getUniqueLabel() + "\n"
                        + "0;JMP\n"
                        + "(TRUE" + (labelCounter - 1) + ")\n"
                        + "@SP\n"
                        + "A=M-1\n"
                        + "M=-1\n"
                        + "(CONTINUE" + (labelCounter - 1) + ")\n";
                break;
            case "gt":
                translatedCode = "@SP\n"
                        + "AM=M-1\n"
                        + "D=M\n"
                        + "A=A-1\n"
                        + "D=M-D\n"
                        + "@TRUE" + getUniqueLabel() + "\n"
                        + "D;JGT\n"
                        + "@SP\n"
                        + "A=M-1\n"
                        + "M=0\n"
                        + "@CONTINUE" + getUniqueLabel() + "\n"
                        + "0;JMP\n"
                        + "(TRUE" + (labelCounter - 1) + ")\n"
                        + "@SP\n"
                        + "A=M-1\n"
                        + "M=-1\n"
                        + "(CONTINUE" + (labelCounter - 1) + ")\n";
                break;
            case "lt":
                translatedCode = "@SP\n"
                        + "AM=M-1\n"
                        + "D=M\n"
                        + "A=A-1\n"
                        + "D=M-D\n"
                        + "@TRUE" + getUniqueLabel() + "\n"
                        + "D;JLT\n"
                        + "@SP\n"
                        + "A=M-1\n"
                        + "M=0\n"
                        + "@CONTINUE" + getUniqueLabel() + "\n"
                        + "0;JMP\n"
                        + "(TRUE" + (labelCounter - 1) + ")\n"
                        + "@SP\n"
                        + "A=M-1\n"
                        + "M=-1\n"
                        + "(CONTINUE" + (labelCounter - 1) + ")\n";
                break;
            case "and":
                translatedCode = "@SP\n"
                        + "AM=M-1\n"
                        + "D=M\n"
                        + "A=A-1\n"
                        + "M=D&M\n";
                break;
            case "or":
                translatedCode = "@SP\n"
                        + "AM=M-1\n"
                        + "D=M\n"
                        + "A=A-1\n"
                        + "M=D|M\n";
                break;
            case "not":
                translatedCode = "@SP\n"
                        + "A=M-1\n"
                        + "M=!M\n";
                break;
            default:
                throw new IllegalArgumentException("Invalid arithmetic command: " + command);
        }

        outputFile.write(translatedCode);
    }

    private void translatePushPop(CommandType commandType, String segment, int index) throws IOException {
        String translatedCode;
        switch (commandType) {
            case C_PUSH:
                switch (segment) {
                    case "local":
                        translatedCode = "@LCL\n"
                                + "D=M\n"
                                + "@" + index + "\n"
                                + "A=D+A\n"
                                + "D=M\n"
                                + "@SP\n"
                                + "A=M\n"
                                + "M=D\n"
                                + "@SP\n"
                                + "M=M+1\n";
                        break;
                    case "argument":
                        translatedCode = "@ARG\n"
                                + "D=M\n"
                                + "@" + index + "\n"
                                + "A=D+A\n"
                                + "D=M\n"
                                + "@SP\n"
                                + "A=M\n"
                                + "M=D\n"
                                + "@SP\n"
                                + "M=M+1\n";
                        break;
                    case "this":
                        translatedCode = "@THIS\n"
                                + "D=M\n"
                                + "@" + index + "\n"
                                + "A=D+A\n"
                                + "D=M\n"
                                + "@SP\n"
                                + "A=M\n"
                                + "M=D\n"
                                + "@SP\n"
                                + "M=M+1\n";
                        break;
                    case "that":
                        translatedCode = "@THAT\n"
                                + "D=M\n"
                                + "@" + index + "\n"
                                + "A=D+A\n"
                                + "D=M\n"
                                + "@SP\n"
                                + "A=M\n"
                                + "M=D\n"
                                + "@SP\n"
                                + "M=M+1\n";
                        break;
                    case "constant":
                        translatedCode = "@" + index + "\n"
                                + "D=A\n"
                                + "@SP\n"
                                + "A=M\n"
                                + "M=D\n"
                                + "@SP\n"
                                + "M=M+1\n";
                        break;
                    case "static":
                        translatedCode = "@" + currentFileName + "." + index + "\n"
                                + "D=M\n"
                                + "@SP\n"
                                + "A=M\n"
                                + "M=D\n"
                                + "@SP\n"
                                + "M=M+1\n";
                        break;
                    case "temp":
                        translatedCode = "@R5\n"
                                + "D=A\n"
                                + "@" + index + "\n"
                                + "A=D+A\n"
                                + "D=M\n"
                                + "@SP\n"
                                + "A=M\n"
                                + "M=D\n"
                                + "@SP\n"
                                + "M=M+1\n";
                        break;
                    case "pointer":
                        if (index == 0) {
                            translatedCode = "@THIS\n";
                        } else if (index == 1) {
                            translatedCode = "@THAT\n";
                        } else {
                            throw new IllegalArgumentException("Invalid pointer index: " + index);
                        }
                        translatedCode += "D=M\n"
                                + "@SP\n"
                                + "A=M\n"
                                + "M=D\n"
                                + "@SP\n"
                                + "M=M+1\n";
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid push segment: " + segment);
                }
                break;
            case C_POP:
                switch (segment) {
                    case "local":
                        translatedCode = "@LCL\n"
                                + "D=M\n"
                                + "@" + index + "\n"
                                + "D=D+A\n"
                                + "@R13\n"
                                + "M=D\n"
                                + "@SP\n"
                                + "AM=M-1\n"
                                + "D=M\n"
                                + "@R13\n"
                                + "A=M\n"
                                + "M=D\n";
                        break;
                    case "argument":
                        translatedCode = "@ARG\n"
                                + "D=M\n"
                                + "@" + index + "\n"
                                + "D=D+A\n"
                                + "@R13\n"
                                + "M=D\n"
                                + "@SP\n"
                                + "AM=M-1\n"
                                + "D=M\n"
                                + "@R13\n"
                                + "A=M\n"
                                + "M=D\n";
                        break;
                    case "this":
                        translatedCode = "@THIS\n"
                                + "D=M\n"
                                + "@" + index + "\n"
                                + "D=D+A\n"
                                + "@R13\n"
                                + "M=D\n"
                                + "@SP\n"
                                + "AM=M-1\n"
                                + "D=M\n"
                                + "@R13\n"
                                + "A=M\n"
                                + "M=D\n";
                        break;
                    case "that":
                        translatedCode = "@THAT\n"
                                + "D=M\n"
                                + "@" + index + "\n"
                                + "D=D+A\n"
                                + "@R13\n"
                                + "M=D\n"
                                + "@SP\n"
                                + "AM=M-1\n"
                                + "D=M\n"
                                + "@R13\n"
                                + "A=M\n"
                                + "M=D\n";
                        break;
                    case "static":
                        translatedCode = "@SP\n"
                                + "AM=M-1\n"
                                + "D=M\n"
                                + "@" + currentFileName + "." + index + "\n"
                                + "M=D\n";
                        break;
                    case "temp":
                        translatedCode = "@R5\n"
                                + "D=A\n"
                                + "@" + index + "\n"
                                + "D=D+A\n"
                                + "@R13\n"
                                + "M=D\n"
                                + "@SP\n"
                                + "AM=M-1\n"
                                + "D=M\n"
                                + "@R13\n"
                                + "A=M\n"
                                + "M=D\n";
                        break;
                    case "pointer":
                        if (index == 0) {
                            translatedCode = "@SP\n"
                                    + "AM=M-1\n"
                                    + "D=M\n"
                                    + "@THIS\n"
                                    + "M=D\n";
                        } else if (index == 1) {
                            translatedCode = "@SP\n"
                                    + "AM=M-1\n"
                                    + "D=M\n"
                                    + "@THAT\n"
                                    + "M=D\n";
                        } else {
                            throw new IllegalArgumentException("Invalid pointer index: " + index);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid pop segment: " + segment);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid command type: " + commandType);
        }

        outputFile.write(translatedCode);
    }

    private void translateLabel(String label) throws IOException {
        String translatedCode = "(" + label + ")\n";
        outputFile.write(translatedCode);
    }

    private void translateGoto(String label) throws IOException {
        String translatedCode = "@" + label + "\n"
                + "0;JMP\n";
        outputFile.write(translatedCode);
    }

    private void translateIf(String label) throws IOException {
        String translatedCode = "@SP\n"
                + "AM=M-1\n"
                + "D=M\n"
                + "@" + label + "\n"
                + "D;JNE\n";
        outputFile.write(translatedCode);
    }

    private void translateFunction(String functionName, int numVars) throws IOException {
        String translatedCode = "(" + functionName + ")\n";
        for (int i = 0; i < numVars; i++) {
            translatedCode += "@SP\n"
                    + "A=M\n"
                    + "M=0\n"
                    + "@SP\n"
                    + "M=M+1\n";
        }
        outputFile.write(translatedCode);
    }

    private void translateReturn() throws IOException {
        String translatedCode = "@LCL\n"
                + "D=M\n"
                + "@R13\n"
                + "M=D\n"
                + "@5\n"
                + "A=D-A\n"
                + "D=M\n"
                + "@R14\n"
                + "M=D\n"
                + "@SP\n"
                + "AM=M-1\n"
                + "D=M\n"
                + "@ARG\n"
                + "A=M\n"
                + "M=D\n"
                + "@ARG\n"
                + "D=M+1\n"
                + "@SP\n"
                + "M=D\n"
                + "@R13\n"
                + "AM=M-1\n"
                + "D=M\n"
                + "@THAT\n"
                + "M=D\n"
                + "@R13\n"
                + "AM=M-1\n"
                + "D=M\n"
                + "@THIS\n"
                + "M=D\n"
                + "@R13\n"
                + "AM=M-1\n"
                + "D=M\n"
                + "@ARG\n"
                + "M=D\n"
                + "@R13\n"
                + "AM=M-1\n"
                + "D=M\n"
                + "@LCL\n"
                + "M=D\n"
                + "@R14\n"
                + "A=M\n"
                + "0;JMP\n";
        outputFile.write(translatedCode);
    }

    private void translateCall(String functionName, int numArgs) throws IOException {
        String returnLabel = "RETURN_LABEL" + getUniqueLabel();
        String translatedCode = "@" + returnLabel + "\n"
                + "D=A\n"
                + "@SP\n"
                + "A=M\n"
                + "M=D\n"
                + "@SP\n"
                + "M=M+1\n"
                + "@LCL\n"
                + "D=M\n"
                + "@SP\n"
                + "A=M\n"
                + "M=D\n"
                + "@SP\n"
                + "M=M+1\n"
                + "@ARG\n"
                + "D=M\n"
                + "@SP\n"
                + "A=M\n"
                + "M=D\n"
                + "@SP\n"
                + "M=M+1\n"
                + "@THIS\n"
                + "D=M\n"
                + "@SP\n"
                + "A=M\n"
                + "M=D\n"
                + "@SP\n"
                + "M=M+1\n"
                + "@THAT\n"
                + "D=M\n"
                + "@SP\n"
                + "A=M\n"
                + "M=D\n"
                + "@SP\n"
                + "M=M+1\n"
                + "@SP\n"
                + "D=M\n"
                + "@" + (numArgs + 5) + "\n"
                + "D=D-A\n"
                + "@ARG\n"
                + "M=D\n"
                + "@SP\n"
                + "D=M\n"
                + "@LCL\n"
                + "M=D\n"
                + "@" + functionName + "\n"
                + "0;JMP\n"
                + "(" + returnLabel + ")\n";
        outputFile.write(translatedCode);
    }

    private String getUniqueLabel() {
        return String.valueOf(labelCounter++);
    }

    public void close() throws IOException {
        outputFile.close();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java HackVMTranslator <inputFile>");
            return;
        }

        String inputFile = args[0];
        String outputFile = inputFile.replace(".vm", ".asm");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            HackVMTranslator translator = new HackVMTranslator(writer);

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }

                if (line.startsWith("/*") && !line.endsWith("*/")) {
                    while ((line = reader.readLine()) != null) {
                        if (line.endsWith("*/")) {
                            break;
                        }
                    }
                    continue;
                }

                String[] parts = line.split("\\s+");
                String command = parts[0];
                String arg1 = null;
                int arg2 = -1;
                if (parts.length > 1) {
                    arg1 = parts[1];
                }
                if (parts.length > 2) {
                    arg2 = Integer.parseInt(parts[2]);
                }

                if (command.equals("function")) {
                    translator.setCurrentFileName(arg1);
                }

                translator.translateCommand(command, arg1, arg2);
            }

            reader.close();
            translator.close();
            System.out.println("Translation completed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCurrentFileName(String fileName) {
        currentFileName = fileName;
    }
}

enum CommandType {
    C_ARITHMETIC, C_PUSH, C_POP, C_LABEL, C_GOTO, C_IF, C_FUNCTION, C_RETURN, C_CALL
}
