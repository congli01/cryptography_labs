package com.company;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class test {
    public static void main(String[] args) throws IOException {
        String path = "src/lab2-Plaintext.txt";
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String plainText = bufferedReader.readLine();
        System.out.println(plainText);
        byte[] textByte = plainText.getBytes("UTF-8");
        System.out.println(textByte[7]);
    }
}
