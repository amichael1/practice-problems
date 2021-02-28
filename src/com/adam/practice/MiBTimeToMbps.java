package com.adam.practice;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class MiBTimeToMbps {

    interface Converter{
        void convert();
    }

    private static final String regexForNonNumberChars = "[^0-9]+";
    private static final int numberOfInputsPerLine = 2;
    private static final Consumer<Converter> converterConsumer = Converter::convert;
    private static final Consumer<Converter> printConversion = System.out::println;

    public static void main(String[] args) {
        //Try with resource - Read about this, never ever ever use a unmanaged resource without closing connection
        //System.in will read the console inputs as the application is running
	    try(BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))){
            //Take input and convert to input array per line (2 inputs per line in)
            //Filter out any invalid lines -- this is bad practice, should log instead
            //Map those inputs and change current stream into a stream of converters
            //For each converter, call the consumers in order (.accept tells them what each consumers method is passing as param)
	        in.lines()
                    .map(r -> r.split(regexForNonNumberChars))
                    .filter(r -> r.length == numberOfInputsPerLine)
                    .map(MiBTimeToMbps::getConverters)
                    .forEach(r -> converterConsumer.andThen(printConversion).accept(r));
        }catch(IOException ex){
            //Print exceptions and exit
            ex.printStackTrace();
	    }
    }

    //Creates anonymous object of type Converter Interface to handle conversion
    private static Converter getConverters(String[] inputs){
        return new Converter() {
            private final double BITS_IN_A_MEBIBYTE = Math.pow(1024, 2);
            private final double BITS_IN_A_MEGABYTE = Math.pow(1000, 2) * 8;
            private final double mebiBytesInput = Double.parseDouble(inputs[0]);
            private final double timeToFinishInput = Double.parseDouble(inputs[1]);
            private long timeToTransfer;

            @Override
            public void convert() {
                //try-catch so that I can log the exception and then rethrow it since I am going to let that exception term the app
                try {
                    double bitsFromMebiBytes = mebiBytesInput / BITS_IN_A_MEBIBYTE;
                    double MegaBitsFromBytes = bitsFromMebiBytes * BITS_IN_A_MEGABYTE;
                    timeToTransfer = (long) Math.ceil(MegaBitsFromBytes / timeToFinishInput);
                }catch(Exception ex){
                    ex.printStackTrace();
                    throw ex;
                }
            }

            @Override
            public String toString(){
                return String.valueOf(timeToTransfer);
            }
        };
    }
}
