//Pablo Duenas
//Project 6
//CS 3650

import java.util.HashMap;
import java.util.Map;

public class parser {
	public static int numLine = -1;
    public static Map<String, Integer> symTable = new HashMap<>();
    public static int nextRegister = 16;
    static {
        symTable.put("R0", 0);
        symTable.put("R1", 1);
        symTable.put("R2", 2);
        symTable.put("R3", 3);
        symTable.put("R4", 4);
        symTable.put("R5", 5);
        symTable.put("R6", 6);
        symTable.put("R7", 7);
        symTable.put("R8", 8);
        symTable.put("R9", 9);
        symTable.put("R10", 10);
        symTable.put("R11", 11);
        symTable.put("R12", 12);
        symTable.put("R13", 13);
        symTable.put("R14", 14);
        symTable.put("R15", 15);
        symTable.put("SCREEN", 16384);
        symTable.put("KBD", 24576);

        symTable.put("SP", 0);
        symTable.put("LCL", 1);
        symTable.put("ARG", 2);
        symTable.put("THIS", 3);
        symTable.put("THAT", 4);
    }
    public String assemblyLine;
    public identifyCommand type;

    public String symbol;
    public String dest, comp, jump;

    public parser(String line) {
        assemblyLine = removeCommentBlank(line);
        type = identifyType();

        if (type == identifyCommand.A_COMMAND || type == identifyCommand.C_COMMAND) {
            numLine++;
        } else if (type == identifyCommand.L_COMMAND) {
            symTable.put(symbol, numLine + 1);
        }
    }

    public static void resetNumLine() {
        numLine = -1;
    }

    public identifyCommand getType() {
        return type;
    }

    public String getAssemblyLine() {
        return assemblyLine;
    }

    public String toCode() {
        if (type == identifyCommand.A_COMMAND) {
            return aToCode();
        } else if (type == identifyCommand.C_COMMAND) {
            return cToCode();
        } else {
            return "";
        }
    }

    public String getSymbol() {
        return symbol;
    }

    public static String removeCommentBlank(String s) {
        int offSet = s.indexOf("//");
        if (offSet != -1) {
            s = s.substring(0, offSet);
        }
        s = s.replaceAll("\\s+", "");
        return s;
    }

    public identifyCommand identifyType() {
        if (assemblyLine.equals("")) {
            return null;
        }
        switch (assemblyLine.charAt(0)) {
            case '@':
                symbol = assemblyLine.substring(1);
                return identifyCommand.A_COMMAND;
            case '(':
                symbol = assemblyLine.substring(1, assemblyLine.length() - 1);
                return identifyCommand.L_COMMAND;
            default:
                String[] tokens = assemblyLine.split("[=;]");
                if (tokens.length == 3) {
                    dest = tokens[0];
                    comp = tokens[1];
                    jump = tokens[2];
                } else if (tokens.length == 1) {
                    comp = tokens[0];
                } else {
                    if (assemblyLine.contains(";")) {
                        comp = tokens[0];
                        jump = tokens[1];
                    } else {
                        dest = tokens[0];
                        comp = tokens[1];
                    }
                }
                return identifyCommand.C_COMMAND;
        }
    }

    public String cToCode() {
        return "111" + compToCode() + destToCode() + jumpToCode();
    }

    public String aToCode() {
        Integer value;
        try {
            value = Integer.parseInt(symbol);
        } catch (NumberFormatException e) {
            value = symTable.get(symbol);
            if (value == null) {
                symTable.put(symbol, nextRegister);
                value = nextRegister;
                nextRegister++;
            }
        }
        return String.format("%16s", Integer.toBinaryString(value)).replace(' ', '0');
    }

    public String getDest() {
        return dest;
    }

    public String getComp() {
        return comp;
    }

    public String getJump() {
        return jump;
    }

    public String destToCode() {
        if (dest == null) {
            return "000";
        }
        switch (dest) {
            case "M":   return "001";
            case "D":   return "010";
            case "MD":  return "011";
            case "A":   return "100";
            case "AM":  return "101";
            case "AD":  return "110";
            case "AMD": return "111";
            default:    return null;
        }
    }

    public String compToCode() {
        switch (comp) {
            case "0":   return "0101010";
            case "1":   return "0111111";
            case "-1":  return "0111010";
            case "D":   return "0001100";
            case "A":   return "0110000";
            case "!D":  return "0001101";
            case "!A":  return "0110001";
            case "-D":  return "0001111";
            case "-A":  return "0110011";
            case "D+1": return "0011111";
            case "A+1": return "0110111";
            case "D-1": return "0001110";
            case "A-1": return "0110010";
            case "D+A": return "0000010";
            case "D-A": return "0010011";
            case "A-D": return "0000111";
            case "D&A": return "0000000";
            case "D|A": return "0010101";
            case "M":   return "1110000";
            case "!M":  return "1110001";
            case "-M":  return "1110011";
            case "M+1": return "1110111";
            case "M-1": return "1110010";
            case "D+M": return "1000010";
            case "D-M": return "1010011";
            case "M-D": return "1000111";
            case "D&M": return "1000000";
            case "D|M": return "1010101";
            default:    return null;
        }
    }

    public String jumpToCode() {
        if (jump == null) {
            return "000";
        }
        switch (jump) {
            case "JGT": return "001";
            case "JEQ": return "010";
            case "JGE": return "011";
            case "JLT": return "100";
            case "JNE": return "101";
            case "JLE": return "110";
            case "JMP": return "111";
            default:    return null;
        }
    }
}