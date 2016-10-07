package com.zerjioang.apkr.sdk.model.holder;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by sergio on 7/6/16.
 */
public class StringAnalysis implements Serializable {

    private HashMap<String, String> classified;

    private int initialLength;
    private boolean ofuscated;
    private int irrelevant;
    private int classname;
    private int javaName;
    private int innerclass;
    private int accessor;
    private int param;
    private int hexstring;
    private int word;
    private int underword;
    private int separatorWord;
    private int ipv4;
    private int dns;
    private int ipv6;
    private int email;
    private int url;
    private int unknown;
    private int numeric;
    private int ofuscatedString;
    private int sentences;
    private int methodName;

    public StringAnalysis() {
    }

    public int getInitialLength() {
        return initialLength;
    }

    public void setInitialLength(int initialLength) {
        this.initialLength = initialLength;
    }

    public HashMap<String, String> getClassified() {
        return classified;
    }

    public void setClassified(HashMap<String, String> classified) {
        this.classified = classified;
    }

    public boolean isOfuscated() {
        return ofuscated;
    }

    public void setOfuscated(boolean ofuscated) {
        this.ofuscated = ofuscated;
    }

    public int getIrrelevant() {
        return irrelevant;
    }

    public void setIrrelevant(int irrelevant) {
        this.irrelevant = irrelevant;
    }

    public int getClassname() {
        return classname;
    }

    public void setClassname(int classname) {
        this.classname = classname;
    }

    public int getJavaName() {
        return javaName;
    }

    public void setJavaName(int javaName) {
        this.javaName = javaName;
    }

    public int getInnerclass() {
        return innerclass;
    }

    public void setInnerclass(int innerclass) {
        this.innerclass = innerclass;
    }

    public int getAccessor() {
        return accessor;
    }

    public void setAccessor(int accessor) {
        this.accessor = accessor;
    }

    public int getParam() {
        return param;
    }

    public void setParam(int param) {
        this.param = param;
    }

    public int getHexstring() {
        return hexstring;
    }

    public void setHexstring(int hexstring) {
        this.hexstring = hexstring;
    }

    public int getWord() {
        return word;
    }

    public void setWord(int word) {
        this.word = word;
    }

    public int getUnderword() {
        return underword;
    }

    public void setUnderword(int underword) {
        this.underword = underword;
    }

    public int getSeparatorWord() {
        return separatorWord;
    }

    public void setSeparatorWord(int separatorWord) {
        this.separatorWord = separatorWord;
    }

    public int getIpv4() {
        return ipv4;
    }

    public void setIpv4(int ipv4) {
        this.ipv4 = ipv4;
    }

    public int getDns() {
        return dns;
    }

    public void setDns(int dns) {
        this.dns = dns;
    }

    public int getIpv6() {
        return ipv6;
    }

    public void setIpv6(int ipv6) {
        this.ipv6 = ipv6;
    }

    public int getEmail() {
        return email;
    }

    public void setEmail(int email) {
        this.email = email;
    }

    public int getUrl() {
        return url;
    }

    public void setUrl(int url) {
        this.url = url;
    }

    public int getUnknown() {
        return unknown;
    }

    public void setUnknown(int unknown) {
        this.unknown = unknown;
    }

    public int getNumeric() {
        return numeric;
    }

    public void setNumeric(int numeric) {
        this.numeric = numeric;
    }

    public int getOfuscatedString() {
        return ofuscatedString;
    }

    public void setOfuscatedString(int ofuscatedString) {
        this.ofuscatedString = ofuscatedString;
    }

    public int getSentences() {
        return sentences;
    }

    public void setSentences(int sentences) {
        this.sentences = sentences;
    }

    public int getMethodName() {
        return methodName;
    }

    public void setMethodName(int methodName) {
        this.methodName = methodName;
    }
}