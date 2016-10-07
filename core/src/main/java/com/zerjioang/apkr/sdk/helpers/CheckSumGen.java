package com.zerjioang.apkr.sdk.helpers;


import apkr.external.module.ssdeep.core.SsdeepHashGen;
import apkr.external.module.ssdeep.exception.SSDeepException;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.CRC32;

public class CheckSumGen implements Serializable {

    private static final String SHA_1 = "SHA-1";
    private static final String MD5 = "MD5";
    private static final String SHA_256 = "SHA-256";
    private static final String SHA_512 = "SHA-512";
    private static final String DEFAULT_RET = "error-on-computing";
    private static final long DEFAULT_RET_CRC32 = -1;
    private static CheckSumGen instance = new CheckSumGen();

    public static CheckSumGen getInstance() {
        return instance;
    }

    public String calculateSSDeep(File f) throws SSDeepException {
        if (f == null) {
            Log.write(LoggerType.ERROR, "Could not create calculateSSDeep() Hash because of a null file reference");
            throw new SSDeepException("Apkr could not calculateSSDeep() SSDeep fuzzing hash for file\n\t" + "\nPossible reason: null file reference.");
        }
        SsdeepHashGen test = new SsdeepHashGen();
        try {
            return test.fuzzy_hash_file(f);
        } catch (IOException e) {
            Log.write(LoggerType.ERROR, "Apkr could not calculateSSDee()p SSDeep fuzzing hash for file\n\t" + f.getAbsolutePath() + "\nPossible reason: " + e.getLocalizedMessage());
            throw new SSDeepException("Apkr could not calculateSSDee()p SSDeep fuzzing hash for file\n\t" + f.getAbsolutePath() + "\nPossible reason: " + e.getLocalizedMessage());
        }
    }

    private String calculate(File f, String alg) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(alg);
        FileInputStream fis = new FileInputStream(f);
        byte[] dataBytes = new byte[1024];

        int nread;

        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }

        byte[] mdbytes = md.digest();

        //convert the byte to hex format
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //Log.write(LoggerType.DEBUG, alg.toUpperCase() + " digest(in hex format):: " + sb.toString());
        return sb.toString().toUpperCase();
    }

    private String calculate(byte[] data, String alg) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(alg);

        md.update(data, 0, data.length);

        byte[] mdbytes = md.digest();

        //convert the byte to hex format
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //Log.write(LoggerType.DEBUG, alg.toUpperCase() + " digest(in hex format):: " + sb.toString());
        return sb.toString().toUpperCase();
    }

    public long calculateCRC32(File f) throws NullPointerException {
        if (f == null) {
            Log.write(LoggerType.ERROR, "Could not create calculateCRC32() Hash because of a null file reference");
            throw new NullPointerException("Apkr could not calculateCRC32() hash for file\n\t" + "\nPossible reason: null file reference.");
        }
        try {
            InputStream inputStreamn = new FileInputStream(f);
            CRC32 crc = new CRC32();
            int cnt;
            while ((cnt = inputStreamn.read()) != -1) {
                crc.update(cnt);
            }
            return crc.getValue();
        } catch (IOException e) {
            Log.write(LoggerType.ERROR, "Could not create CRC32 Hash because", e.getLocalizedMessage(), Arrays.toString(e.getStackTrace()));
        }
        return DEFAULT_RET_CRC32;
    }

    public String calculateSHA1(File f) throws NullPointerException {
        if (f == null) {
            Log.write(LoggerType.ERROR, "Could not create calculateSHA1() Hash because of a null file reference");
            throw new NullPointerException("Apkr could not calculateSHA1() hash for file\n\t" + "\nPossible reason: null file reference.");
        }
        try {
            return calculate(f, SHA_1);
        } catch (NoSuchAlgorithmException | IOException e) {
            Log.write(LoggerType.ERROR, "Could not create SHA1 Hash because", e.getLocalizedMessage(), Arrays.toString(e.getStackTrace()));
        }
        return DEFAULT_RET;
    }

    public String calculateMD5(File f) throws NullPointerException {
        if (f == null) {
            Log.write(LoggerType.ERROR, "Could not create calculateSHAMD5() Hash because of a null file reference");
            throw new NullPointerException("Apkr could not calculateMD5() hash for file\n\t" + "\nPossible reason: null file reference.");
        }
        try {
            return calculate(f, MD5);
        } catch (NoSuchAlgorithmException | IOException e) {
            Log.write(LoggerType.ERROR, "Could not create MD5 Hash because", e.getLocalizedMessage(), Arrays.toString(e.getStackTrace()));
        }
        return DEFAULT_RET;
    }

    public String calculateSHA256(File f) throws NullPointerException {
        if (f == null) {
            Log.write(LoggerType.ERROR, "Could not create calculateSHA256() Hash because of a null file reference");
            throw new NullPointerException("Apkr could not calculateSHA256() hash for file\n\t" + "\nPossible reason: null file reference.");
        }
        try {
            return calculate(f, SHA_256);
        } catch (NoSuchAlgorithmException | IOException e) {
            Log.write(LoggerType.ERROR, "Could not create SHA256 Hash because", e.getLocalizedMessage(), Arrays.toString(e.getStackTrace()));
        }
        return DEFAULT_RET;
    }

    public String calculateSHA256(byte[] data) throws NullPointerException {
        if (data == null) {
            Log.write(LoggerType.ERROR, "Could not create calculateSHA256() Hash because of a null file reference");
            throw new NullPointerException("Apkr could not calculateSHA256() hash for file\n\t" + "\nPossible reason: null file reference.");
        }
        try {
            return calculate(data, SHA_256);
        } catch (NoSuchAlgorithmException | IOException e) {
            Log.write(LoggerType.ERROR, "Could not create SHA256 Hash because", e.getLocalizedMessage(), Arrays.toString(e.getStackTrace()));
        }
        return DEFAULT_RET;
    }

    public String calculateSHA512(File f) throws NullPointerException {
        if (f == null) {
            Log.write(LoggerType.ERROR, "Could not create calculateSHA512() Hash because of a null file reference");
            throw new NullPointerException("Apkr could not calculateSHA512() hash for file\n\t" + "\nPossible reason: null file reference.");
        }
        try {
            return calculate(f, SHA_512);
        } catch (NoSuchAlgorithmException | IOException e) {
            Log.write(LoggerType.ERROR, "Could not create SHA512 Hash because", e.getLocalizedMessage(), Arrays.toString(e.getStackTrace()));
        }
        return DEFAULT_RET;
    }
}
