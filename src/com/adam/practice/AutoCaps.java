package com.adam.practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Take in a string containing sentences, capitalize the sentences in the string
//Example input: hello. i am a string! can you help capitalize me? i need all the help I can get..
//Example output: Hello. I am a string! Can you help capitalize me? I need all the help I can get..
public class AutoCaps {

    interface AutoCorrect{
        void autoCorrect();
    }

    //Regex with capture group named start that captures the start of a sentence that is not capitalized
    //Basically captures all strings that start at a newline followed by next character
    //Or start with a end of sentence punctuation, followed by spaces, and a new character
    //Captured group will be capitalized
    private static final String delimiters ="(?<start>(^[a-z]|[.!?]\\s[a-z]))";

    public static void main(String[] args) {
        //Try with to ensure unmanaged resource is disposed properly - Could also do this in finally block
        try(BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))){
            //Streams in System.in
            String input = in.readLine();
            in.close();

            AutoCorrect autoCorrectedInput = getAutoCorrectedCapitals(input);
            autoCorrectedInput.autoCorrect();
            System.out.println(autoCorrectedInput);
        }catch(IOException |
                IllegalArgumentException |
                IndexOutOfBoundsException |
                IllegalStateException ex){
            //Would log exceptions here with proper log message
            ex.printStackTrace();
        }
    }

    private static AutoCorrect getAutoCorrectedCapitals(String input){
        return new AutoCorrect() {
            private String statement = input;
            @Override
            public void autoCorrect() {
                //Regex java pattern matcher to match on captured group in regex expression
                Pattern compile = Pattern.compile(delimiters);
                Matcher matcher = compile.matcher(input);

                //Find each match, replace and add to string buffer
                StringBuffer sb = new StringBuffer();
                while(matcher.find()){
                    matcher.appendReplacement(sb, matcher.group("start").toUpperCase());
                }
                //Add end of line that was unmatched
                matcher.appendTail(sb);

                statement = sb.toString();
            }

            @Override
            public String toString(){
                return statement;
            }
        };
    }
}
