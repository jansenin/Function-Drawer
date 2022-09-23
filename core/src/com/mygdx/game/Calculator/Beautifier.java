package com.mygdx.game.Calculator;

import com.mygdx.game.Main;

public class Beautifier {
    private String getBraces(String s) {
        int i = 0;
        int braces = 0;
        if (!(s.contains("(") && s.contains(")"))) return "";
        do {
            if (s.charAt(i) == '(') {
                braces++;
            } else if (s.charAt(i) == ')'){
                braces--;
            }
            i++;
        } while (braces > 0);
        String ss = s.charAt(0) + beautify(s.substring(1,i - 1)) + s.charAt(i - 1);
        return ss;
    }

    private String nextNumber(StringBuilder s) {
        if (s.charAt(0) == 'x') return "x";
        int i = 0;
        for (String function : Utils.functions) {
            if (s.length() > function.length() && s.substring(0,function.length()).equals(function)) {
                return s.substring(0,function.length()) + getBraces(s.substring(function.length(),s.length()));
            }
        }
        if (s.charAt(0) == '(') {
            if (!s.toString().contains(")")) {
                throw new RuntimeException("Invalid Formula");
            }
            String ss = getBraces(s.toString());
            return ss;
        }
        while (Utils.isNumber(s.toString() , 0 , ++i)) { }

        String ss = s.toString().substring(0 , i - 1);
        return ss;
    }

    private int nextNumberLength(StringBuilder s) {
        String ss = nextNumber(s);
        int ii = ss.length();
        return ii;
    }

    public String beautify(String s) {
        try {
            if (s.length() == 0) return s;
            String sCopy = new String(s);
            s = s.replace("(-", "(0-").replace(" ", "").toLowerCase();
            if (s.charAt(0) == '-') {
                s = "0-" + s.substring(1, s.length());
            }

            StringBuilder string = new StringBuilder(s);
            StringBuilder result = new StringBuilder();

            int i = 0;
            boolean number = true;
            while (i < string.length()) {
                if (number) {
                    int nextNumberLength = Utils.nextRawNumber(string).length();
                    if (nextNumberLength == 0) throw new RuntimeException("Invalid Formula");
                    result.append(nextNumber(string));
                    number = false;
                    Utils.deleteNextRawNumber(string);
                } else {
                    if (nextNumberLength(string) == 0) {
                        result.append(Utils.getNextOperation(string));
                        Utils.deleteNextOperation(string);
                    } else {
                        result.append("*");
                    }
                    number = true;
                }
            }
            return result.toString();
        } catch (Exception e) {
            if (Main.FILE_LOGGING) {
                e.printStackTrace(Main.logWriter);
                Main.logWriter.print(s);
            }
            if (Main.LOGGING) {
                e.printStackTrace();
                System.out.println(s);
            }
            throw e;
        }
    }
}
