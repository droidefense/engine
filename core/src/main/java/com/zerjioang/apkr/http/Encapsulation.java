package com.zerjioang.apkr.http;

import java.util.Base64;

/**
 * Created by sergio on 20/3/16.
 */
public class Encapsulation {

    private static Encapsulation instance = new Encapsulation();

    public static Encapsulation getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        String enc = Encapsulation.getInstance().encapsulate("this is a test");
        System.out.println(enc);
        String dec = Encapsulation.getInstance().removeEncapsulation(enc);
        System.out.println(dec);
    }

    public String encapsulate(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    public String removeEncapsulation(String data) {
        //TODO implement
        //return new String(Base64.getDecoder().decode(data));
        return data;
    }
}
