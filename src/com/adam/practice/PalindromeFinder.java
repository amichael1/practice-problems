package com.adam.practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

//Question: Given an integer, add it with the reverse of the value, and check if it’s a palindrome. If it isn’t a palindrome, repeat the process until the sum is a palindrome
public class PalindromeFinder {

    //PalindromeHandlers interface
    //Handle Palindrome checks
    interface PalindromeHandler {
        void handlePalindrome();

        //All palindromes will need ability to check if it is a palindrome
        private static boolean isPalindrome(String inputString){
            //Collection from strings array of chars
            List<Character> chars = inputString.chars()
                    .sequential()
                    .mapToObj(c -> (char)c)
                    .collect(Collectors.toList());

            //Reverse Chars
            Collections.reverse(chars);
            //Create string from array to string removing non-digit characters that Array.ToString makes
            //Ex:"[" ","
            String reverseString = chars.toString().replaceAll(regexForNonNumberChars, "");

            //Return if palindrome, i.e. the reverse is the same as original
            return reverseString.equals(inputString);
        }
    }

    private static final String regexForNonNumberChars = "[^0-9]+";
    private static final Consumer<PalindromeHandler> handlePalindrome = PalindromeHandler::handlePalindrome;
    private static final Consumer<PalindromeHandler> printPalindrome = System.out::println;

    public static void main(String[] args) {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))){
            in.lines()
                    .filter(r -> r.split(regexForNonNumberChars).length == 1)
                    .map(PalindromeFinder::createHandlerForRequest)
                    .forEachOrdered(r -> handlePalindrome.andThen(printPalindrome).accept(r));
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    //Method creates anonymous objects of type PalindromeHandlers
    private static PalindromeHandler createHandlerForRequest(String r) {
        return new PalindromeHandler() {
            private String palindromeToTest = r;
            private int attemptCounter;

            @Override
            public void handlePalindrome(){
                long inputInt = Long.parseLong(palindromeToTest);
                long newInt = inputInt;

                do {
                    newInt += inputInt;
                    palindromeToTest = Long.toString(newInt);
                    attemptCounter++;
                }
                while (!PalindromeHandler.isPalindrome(palindromeToTest));
            }

            @Override
            public String toString() {
                return attemptCounter + " " + palindromeToTest;
            }
        };
    }

}
