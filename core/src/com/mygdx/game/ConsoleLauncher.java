package com.mygdx.game;

import com.mygdx.game.Calculator.Calculator;

import java.util.Scanner;

public class ConsoleLauncher {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = "";
        Calculator calculator = new Calculator();
        while (!s.equals("exit")) {
            s = scanner.nextLine();
            calculator.setTask(s);
            try {
                System.out.println(calculator.calculate(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
