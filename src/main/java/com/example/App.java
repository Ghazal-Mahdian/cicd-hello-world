package com.example;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello World from CI/CD Pipeline!");
        while (true) {
            Thread.sleep(10000); 
        }
    }

    public static String getMessage() {
        return "Hello World from CI/CD Pipeline!";
    }
}
