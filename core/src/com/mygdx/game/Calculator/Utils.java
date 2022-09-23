package com.mygdx.game.Calculator;

import com.mygdx.game.Main;

import java.util.HashMap;
import java.util.Map;

class Utils {
    public interface FunctionComputer {
        double compute(double x);
    }

    public interface PrimitiveComputer {
        double compute(double a , double b);
    }

    public static String[] operations = {
            "+",
            "-",
            "*",
            "/",
    };

    public static String[] functions = {
            "sin",
            "cos",
            "tg",
            "tan", // = tg
            "ctg",
            "cot", // = ctg
            "√",
            "sqrt", // = √
            "sqr",
            "abs",
            "round",
            "sign",
    };

    public static Map<String,PrimitiveComputer> primitiveComputers = new HashMap<String,PrimitiveComputer>() {
        {
            put("+", new PrimitiveComputer() {
                @Override
                public double compute(double a, double b) {
                    return a + b;
                }
            });
            put("-", new PrimitiveComputer() {
                @Override
                public double compute(double a, double b) {
                    return a - b;
                }
            });
            put("*", new PrimitiveComputer() {
                @Override
                public double compute(double a, double b) {
                    return a * b;
                }
            });
            put("/", new PrimitiveComputer() {
                @Override
                public double compute(double a, double b) {
                    return a / b;
                }
            });
            put("^", new PrimitiveComputer() {
                @Override
                public double compute(double a, double b) {
                    return Math.pow(a , b);
                }
            });
        }
    };

    public static Map<String,FunctionComputer> functionComputers = new HashMap<String,FunctionComputer>() {
        {
            put("sin", new FunctionComputer() {
                @Override
                public double compute(double x) {
                    return Math.sin(x);
                }
            });
            put("cos", new FunctionComputer() {
                @Override
                public double compute(double x) {
                    return Math.cos(x);
                }
            });
            put("tg" , new FunctionComputer() {
                @Override
                public double compute(double x) {
                    return Math.tan(x);
                }
            });
            put("tan", new FunctionComputer() {
                @Override
                public double compute(double x) {
                    return Math.tan(x);
                }
            });
            put("cot", new FunctionComputer() {
                @Override
                public double compute(double x) {
                    return 1 / Math.tan(x);
                }
            });
            put("ctg", new FunctionComputer() {
                @Override
                public double compute(double x) {
                    return 1 / Math.tan(x);
                }
            });
            put("√"  , new FunctionComputer() {
                @Override
                public double compute(double x) {
                    return Math.sqrt(x);
                }
            });
            put("sqrt"  , new FunctionComputer() {
                @Override
                public double compute(double x) {
                    return Math.sqrt(x);
                }
            });
            put("sqr"  , new FunctionComputer() {
                @Override
                public double compute(double x) {
                    return Math.pow(x , 2);
                }
            });
            put("abs"  , new FunctionComputer() {
                @Override
                public double compute(double x) {
                    return Math.abs(x);
                }
            });
            put("round"  , new FunctionComputer() {
                @Override
                public double compute(double x) {
                    return Math.round(x);
                }
            });
            put("sign"  , new FunctionComputer() {
                @Override
                public double compute(double x) {
                    return (x == 0) ? 0 : (x > 0 ? 1 : -1);
                }
            });
        }
    };

    public static boolean isNumber(String s, int start, int end) {
//        boolean point = false;
//        char[] chars = s.toCharArray();
//
//        for (int i = start;i < end;i++) {
//            if (chars[i] == '.' && point) {
//                return false;
//            }
//
//            if (chars[i] == '.') {
//                point = true;
//            } else if (chars[i] == 'E') {
//                point = false;
//                i++;
//                if (chars[i] == '-') {
//                    i++;
//                }
//            } else if (!isNumber(chars[i])) {
//                return false;
//            }
//        }
//        return true;
//
//
        try {
            Double.valueOf(s.substring(start , end));
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean isNumber2(String s , int start , int end) {
        s = s.replace("." , "");
        if (s.contains("-") || s.contains("+")) return false;
        try {
            Double.valueOf(s.substring(start , end));
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static String getNextFunction (StringBuilder s) {
        for (String function : functions) {
            if (s.length() >= function.length()) {
                if (function.equals(s.substring(0, function.length()))) return function;
            }
        }
        return "";
    }

    public static String getRawBraces (String s) {
        int i = 0;
        int braces = 0;
        if (!(s.contains("(") && s.contains(")"))) {
            return "";
        }
        do {
            if (s.charAt(i) == '(') {
                braces++;
            } else if (s.charAt(i) == ')'){
                braces--;
            }
            i++;
        } while (braces > 0);
        return s.substring(0,i);
    }

    public static String getDoubleRawNumber(StringBuilder s, int start) {
        return getDoubleRawNumber(new StringBuilder(s.substring(start,s.length())));
    }

    public static String getDoubleRawNumber(StringBuilder s) {
        //Infinity
        //-Infinity
        if (s.length() >= 8) {
            if (s.substring(0, 8).equals("Infinity")) {
                return "Infinity";
            }
        }
        if (s.length() >= 9) {
            if (s.substring(0, 9).equals("-Infinity")) {
                return "-Infinity";
            }
        }
        if (s.charAt(0) == 'x') return "x";


        int i = 0;
        for (String function : functions) {
            if (s.length() > function.length() && s.substring(0, function.length()).equals(function)) {
                return s.substring(0, function.length()) + getRawBraces(s.substring(function.length(), s.length()));
            }
        }
        if (s.charAt(0) == '(') {
            String ss = getRawBraces(s.toString());
            return ss;
        }
        while (isNumber(s.toString(), 0, ++i)
                || (
                i < s.length() &&
                        (s.charAt(i - 1) == '.'
                                || s.charAt(i - 1) == 'E'))
                ) {
        }
        String ss = s.toString().substring(0, i - 1);
        return ss;
    }

    public static String nextRawNumber(StringBuilder s) {
        if (s.charAt(0) == 'x') return "x";
        int i = 0;
        for (String function : functions) {
            if (s.length() > function.length() && s.substring(0,function.length()).equals(function)) {
                return s.substring(0,function.length()) + getRawBraces(s.substring(function.length(),s.length()));
            }
        }
        if (s.charAt(0) == '(') {
            String ss = getRawBraces(s.toString());
            return ss;
        }
        while (isNumber(s.toString() , 0 , ++i)) { }

        String ss = s.toString().substring(0 , i - 1);
        return ss;
    }

    //offset - inclusive
    public static String nextRawNumber(StringBuilder s, int start) {
        return nextRawNumber(new StringBuilder(s.substring(start,s.length())));
    }

    public static int nextRawNumberLength(StringBuilder s) {
        return nextRawNumber(s).length();
    }

    public static String getNextOperation(StringBuilder s) {
        for (String operation : operations) {

            String substring = s.substring(0,operation.length());
            if (substring.equals(operation)) {
                return substring;
            }
        }
        return "";
    }

    public static String getNextOperation(StringBuilder s , int start) {
        for (String operation : operations) {

            String substring = s.substring(start,operation.length() + start);
            if (substring.equals(operation)) {
                return substring;
            }
        }
        return "";
    }

    public static int getNextOperationLength(StringBuilder s) {
        return getNextOperation(s).length();
    }

    public static int getNextOperationLength(StringBuilder s , int start) {
        return getNextOperation(s , start).length();
    }

    public static void deleteNextRawNumber(StringBuilder s) {
        s.delete(0,nextRawNumberLength(s));
    }

    public static void deleteNextRawNumber(StringBuilder s , int start) {
        s.delete(start,nextRawNumberLength(s));
    }

    public static void deleteNextOperation(StringBuilder s) {
        s.delete(0,getNextOperationLength(s));
    }

    public static void deleteNextOperation(StringBuilder s , int start) {
        s.delete(start,getNextOperationLength(s , start) + start);
    }
}
