package com.adam.practice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

//Question: Given coins of value 1, 3, and 6 and a sum, what is the minimum number of coins needed to reach the sum?
public class CoinCounter {

    //Nested interface to handle coin counting
    interface ValueCounter {
        void calculateCount();
    }

    private static final String regexForNonNumberChars = "[^0-9]+";
    private static final Consumer<ValueCounter> calculateCount = ValueCounter::calculateCount;
    private static final Consumer<ValueCounter> printToScreen = System.out::println;

    public static void main(String[] args) {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))){
            //Stream of input lines that maps to a stream of anonymous classes implementing CoinCounter
            in.lines()
                    .filter(r -> r.split(regexForNonNumberChars).length == 1)
                    .map(CoinCounter::getCoinCounterForRequest)
                    .forEachOrdered(r -> calculateCount.andThen(printToScreen).accept(r));

        }catch(IOException ex){
            //Print exceptions and exit
            ex.printStackTrace();
        }
    }

    private static ValueCounter getCoinCounterForRequest(String input){
        return new ValueCounter() {
            private final List<Integer> values = Arrays.asList(6, 3, 1);
            private int currentValue = Integer.parseInt(input);
            private int count;

            @Override
            public void calculateCount() {
                //Call to reduce to take coin values and accumulate based on calculate method
                values
                        .forEach(r -> {
                            this.count += currentValue / r;
                            currentValue = currentValue % r;
                        });
            }

            @Override
            public String toString(){
                return Integer.toString(count);
            }
        };
    }

}
