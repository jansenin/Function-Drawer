package com.mygdx.game.Calculator;

import com.mygdx.game.Main;

import java.util.StringTokenizer;

public class Calculator {
    public class Task {
        private StringBuilder stringTask;
        private int iLevel;
        private static final boolean log = false;

        public Task(StringBuilder stringTask , int iLevel) {
            this.stringTask = new StringBuilder(stringTask);
            this.iLevel = iLevel;
        }

        private StringBuilder calculatedBraces(StringBuilder taskCopy , double x) {
            int openBracePosition = taskCopy.indexOf("(");
            int closeBracePosition;
            while (openBracePosition != -1) {
                String brace = Utils.getRawBraces(taskCopy.substring(openBracePosition, taskCopy.length()));
                closeBracePosition = openBracePosition + brace.length() - 1;

                String addToStart = "";
                String addToEnd = "";
                if (openBracePosition - 1 > 0 && Utils.isNumber(taskCopy.toString() , openBracePosition - 1 , openBracePosition)) {
                    addToStart = "*";
                }if (closeBracePosition + 1 < taskCopy.length() && Utils.isNumber(taskCopy.toString() , closeBracePosition + 1 , closeBracePosition + 2)) {
                    addToEnd = "*";
                }

                taskCopy.delete(openBracePosition,closeBracePosition + 1);

                taskCopy.insert(openBracePosition,addToStart +  new Task(new StringBuilder(brace.substring(1, brace.length() - 1)) , iLevel + 1).calculate(x) + addToEnd);
                openBracePosition = taskCopy.indexOf("(");
            }
            String stringTaskCopy = taskCopy.toString();
            if (stringTaskCopy.startsWith("(") && stringTaskCopy.endsWith(")")) {
                stringTaskCopy = stringTaskCopy.substring(1,stringTaskCopy.length() - 1);
            }
            return new StringBuilder(stringTaskCopy);
        }

        private StringBuilder calculateFunctions(StringBuilder taskCopy , double x) {
            for (int i = 0;i < taskCopy.length();i++) {
                String nextFunction = Utils.getNextFunction(new StringBuilder(taskCopy.substring(i)));
                for (String function : Utils.functions) {
                    if (nextFunction.equals(function)) {
                        boolean negative = false;
                        if (taskCopy.charAt(i + nextFunction.length()) == '-') {
                            negative = true;
                            taskCopy = taskCopy.delete(i + nextFunction.length() , i + nextFunction.length() + 1);
                        }
                        String nextNumberString = Utils.getDoubleRawNumber(taskCopy,i + function.length());
                        if (nextNumberString.endsWith("E")) {
                            if (Utils.getNextOperation(taskCopy , nextNumberString.length() + nextFunction.length()).equals("-")) {
                                Utils.deleteNextOperation(taskCopy , nextNumberString.length() + nextFunction.length());
                                nextNumberString += "-";
                            }
                            nextNumberString += Utils.nextRawNumber(taskCopy , function.length() + nextNumberString.length() - (nextNumberString.endsWith("-") ? 1 : 0));
                        }
                        double nextNumber = Double.parseDouble(nextNumberString);
                        if (negative) nextNumber = -nextNumber;
                        taskCopy = taskCopy.delete(i,i+nextFunction.length() + nextNumberString.length());
                        nextNumber = Utils.functionComputers.get(nextFunction).compute(nextNumber);
                        taskCopy.insert(i,nextNumber);
                    }
                }
            }
            return taskCopy;
        }

        private StringBuilder calculatePowers(StringBuilder taskCopy , double x) {
            int powerPosition = taskCopy.lastIndexOf("^");
            int firstNumberStartPosition;
            int firstNumberEndPosition;
            int secondNumberStartPosition;
            int secondNumberEndPosition;
            double result;
            String firstNumber;
            String secondNumber;

            while (powerPosition != -1) {
                firstNumberEndPosition = powerPosition - 1;
                secondNumberStartPosition = powerPosition + 1;
                firstNumberStartPosition = firstNumberEndPosition;
                secondNumberEndPosition = secondNumberStartPosition;

                boolean b1 = false , b2 = false;

                while (Utils.isNumber2(taskCopy.toString() , firstNumberStartPosition , firstNumberEndPosition + 1)) {
                    firstNumberStartPosition--;
                    b1 = true;
                }
                while (Utils.isNumber(taskCopy.toString() , secondNumberStartPosition , secondNumberEndPosition + 1)) {
                    secondNumberEndPosition++;
                    b2 = true;
                }
                if (b1) firstNumberStartPosition++;
                if (b2) secondNumberEndPosition--;

                firstNumber = taskCopy.substring(firstNumberStartPosition , firstNumberEndPosition + 1);
                secondNumber = taskCopy.substring(secondNumberStartPosition , secondNumberEndPosition + 1);
                System.out.println();
                System.out.println(taskCopy);
                System.out.println(firstNumber + "  " + firstNumberStartPosition + "  " + firstNumberEndPosition);
                System.out.println(secondNumber + "  " + secondNumberStartPosition + "  " + secondNumberEndPosition);
                System.out.println();
                taskCopy = taskCopy.delete(firstNumberStartPosition , secondNumberEndPosition + 1);
                result = Utils.primitiveComputers.get("^").compute(Double.parseDouble(firstNumber) , Double.parseDouble(secondNumber));
                taskCopy.insert(firstNumberStartPosition , result);

                powerPosition = taskCopy.indexOf("^");
            }
            return taskCopy;
        }

        private StringBuilder calculatePrimitives(StringBuilder taskCopy , double x) {
            StringBuilder result;
            result = calculateMultiplicationAndDivision(taskCopy , x);
            result = calculatePlusAndMinus(result , x);
            return result;
        }

        private StringBuilder calculatePlusAndMinus(StringBuilder taskCopy , double x) {
            if (taskCopy.length() == 0) return new StringBuilder();
            if (taskCopy.charAt(0) == '-') {
                taskCopy.insert(0,0);
            }
            StringTokenizer tokenizer = new StringTokenizer(taskCopy.toString() , "+-" , true);
            String nextToken = tokenizer.nextToken();
            String nextToken2;
            if (nextToken.endsWith("E")) {
                nextToken += tokenizer.nextToken() + tokenizer.nextToken();
            }
            double tempResult = Double.parseDouble(nextToken);
            while (tokenizer.hasMoreTokens()) {
                nextToken = tokenizer.nextToken();
                nextToken2 = tokenizer.nextToken();
                if (nextToken2.endsWith("E")) {
                    nextToken2 += tokenizer.nextToken() + tokenizer.nextToken();
                }
                tempResult = Utils.primitiveComputers.get(nextToken).compute(tempResult , Double.parseDouble(nextToken2));
            }
            return new StringBuilder(Double.toString(tempResult));
        }

        private StringBuilder calculateMultiplicationAndDivision(StringBuilder taskCopy , double x) {
            if (taskCopy.length() == 0) return new StringBuilder();
            if (taskCopy.charAt(0) == '-') {
                taskCopy.insert(0, '0');
            }
            taskCopy = new StringBuilder(taskCopy.toString().replace("--", "+"));
            taskCopy = new StringBuilder(taskCopy.toString().replace("+-", "-"));
            StringBuilder result = new StringBuilder();
            StringTokenizer tokenizer = new StringTokenizer(taskCopy.toString(), "+-*/", true);
            String nextToken;
            double tempResult = 0;
            while (tokenizer.hasMoreTokens()) {
                nextToken = tokenizer.nextToken();
                if (nextToken.equals("-") || nextToken.equals("+")) {
                    result.append(tempResult);
                    result.append(nextToken);
                } else if (Utils.isNumber(nextToken, 0, nextToken.length()) || nextToken.endsWith("E")) {
                    if (nextToken.endsWith("E")) {
                        nextToken += tokenizer.nextToken();
                        if (nextToken.endsWith("-")) {
                            nextToken += tokenizer.nextToken();
                        }
                    }
                    tempResult = Double.parseDouble(nextToken);
                } else {
                    if (tokenizer.hasMoreTokens()) {
                        boolean negative = false;
                        String nextTokenL = tokenizer.nextToken();
                        if (nextTokenL.equals("-")) {
                            negative = true;
                            nextTokenL = tokenizer.nextToken();
                        }
                        if (Utils.isNumber(nextTokenL, 0, nextTokenL.length()) || nextTokenL.endsWith("E")) {
                            if (nextTokenL.endsWith("E")) {
                                nextTokenL += tokenizer.nextToken();
                                if (nextTokenL.endsWith("-")) {
                                    nextTokenL += tokenizer.nextToken();
                                }
                            }
                            String tmp = nextTokenL;
                            nextTokenL = nextToken;
                            nextToken = tmp;
                        }
                        try {
                            tempResult = Utils.primitiveComputers.get(nextTokenL).compute(tempResult, Double.parseDouble(nextToken) * (negative ? -1 : 1));
                        } catch (Exception e) {
                            Main.printException(e);
                        }
                    } else {
                        tempResult = Double.parseDouble(nextToken);
                    }
                }
            }

            result.append(tempResult);
            result = new StringBuilder(result.toString().replace("--", "+"));
            result = new StringBuilder(result.toString().replace("+-", "-"));
            return result;
        }

        private String getTabs() {
            String result = "";
            for (int i = 0; i < iLevel; i++) {
                result += "   ";
            }
            return result;
        }

        public String calculate(double x) {
            if (log && Main.LOGGING) System.out.println(getTabs() + "{");
            if (log && Main.FILE_LOGGING) Main.logWriter.print(getTabs() + "{");

            StringBuilder taskCopy = new StringBuilder(stringTask.toString().replace("x" , "(" + Double.toString(x) + ")"));
            if (taskCopy.toString().endsWith("+") || taskCopy.toString().endsWith("-")) throw new RuntimeException();

            if (log && Main.LOGGING) System.out.println(getTabs() + "start : " + taskCopy);
            if (log && Main.FILE_LOGGING) Main.logWriter.print(getTabs() + "start : " + taskCopy);

            taskCopy = calculatedBraces(taskCopy , x);

            if (log && Main.LOGGING) System.out.println(getTabs() + "after braces calculating : " + taskCopy);
            if (log && Main.FILE_LOGGING) Main.logWriter.print(getTabs() + "after braces calculating : " + taskCopy);

            taskCopy = calculateFunctions(taskCopy , x);

            if (log && Main.LOGGING) System.out.println(getTabs() + "after functions calculating : " + taskCopy);
            if (log && Main.FILE_LOGGING) Main.logWriter.print(getTabs() + "after functions calculating : " + taskCopy);
            //taskCopy = calculatePowers(taskCopy , x);

            taskCopy = calculatePrimitives(taskCopy , x);

            if (log && Main.LOGGING) {
                System.out.println(getTabs() + "after primitives calculating : " + taskCopy);
                System.out.println(getTabs() + "}");
            }
            if (log && Main.FILE_LOGGING) {
                Main.logWriter.print(getTabs() + "after primitives calculating : " + taskCopy);
                Main.logWriter.print(getTabs() + "}");
            }

            return taskCopy.toString();
        }
    }

    private StringBuilder stringTask;

    private Beautifier beautifier;

    private Task task;

    private String rawStringTask = "";

    public StringBuilder getStringTask() {
        return stringTask;
    }

    public String getRawStringTask() {
        return rawStringTask;
    }

    public Calculator() {
        this.stringTask = new StringBuilder("0");
        this.task = new Task(stringTask , 0);
        this.beautifier = new Beautifier();
    }

    public void setTask(String task) {
        this.rawStringTask = new String(task);
        this.stringTask = new StringBuilder(beautifier.beautify(task));
        try {
            this.task.stringTask = new StringBuilder(stringTask);
        } catch (Exception e) {
            Main.printException(e);
            throw new RuntimeException("Invalid formula");
        }
    }

    public double calculate(double x) {
        try {
            return Double.parseDouble(task.calculate(x));
        } catch (RuntimeException e) {
            if (Main.FILE_LOGGING) {
                e.printStackTrace(Main.logWriter);
                Main.logWriter.write(task.stringTask.toString());
            }
            if (Main.LOGGING) {
                e.printStackTrace();
                System.out.println(task.stringTask.toString());
            }
            Exception exception = new RuntimeException("Invalid formula, x = " + x);
            Main.printException(exception);
        }
        return Double.NaN;
    }
}