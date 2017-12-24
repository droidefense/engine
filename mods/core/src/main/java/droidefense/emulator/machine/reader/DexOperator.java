package droidefense.emulator.machine.reader;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

/**
 * Created by sergio on 26/2/16.
 */
public class DexOperator implements Serializable {

    //Default DEX endianness is little endian

    public static final int ENDIAN_CONSTANT = 0x12345678;
    public static final int REVERSE_ENDIAN_CONSTANT = 0x78563412;

    public static final byte LITTLE_ENDIAN = 0;
    public static final byte BIG_ENDIAN = 1;

    private static final int INT_CONVERSION_ERROR = -1;

    //positiveMatch msg
    private static final String SUCCESS_MSG = " appears to be valid!";

    private final byte[] data;
    private String sha1;
    private long adler;

    public DexOperator(byte[] data) {
        this.data = data;
    }

    //VALUE CHECKERS METHODS

    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result +=
                    Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public void checkData(final String name, byte[] readed, byte[] expected) throws IllegalArgumentException {
        System.out.println("Checking " + name + "...");
        boolean result = Arrays.equals(readed, expected);
        if (!result)
            throw new IllegalArgumentException("illegal " + name + " detected");
        System.out.println(name + SUCCESS_MSG);
    }

    public void checkData(String name, int result, int expected) throws IllegalArgumentException {
        System.out.println("Checking " + name + "...");
        boolean matched = result == expected;
        if (!matched)
            throw new IllegalArgumentException("illegal " + name + " detected");
        System.out.println(name + SUCCESS_MSG);
    }

    //HELPER METHODS

    public void checkData(String name, boolean result) throws IllegalArgumentException {
        System.out.println("Checking " + name + "...");
        if (!result)
            throw new IllegalArgumentException("illegal " + name + " detected");
        System.out.println(name + SUCCESS_MSG);
    }

    public int toInt(byte[] data) {
        //use little endian by default according to dalvik documentation
        return toInt(data, LITTLE_ENDIAN);
    }

    //MORE LOW LEVEL UTILS

    public int toInt(byte[] bytes, byte endianess) {
        switch (endianess) {
            case BIG_ENDIAN: {
                int ret = 0;
                for (int i = 0; i < 4 && i < bytes.length; i++) {
                    ret <<= 8;
                    ret |= (int) bytes[i] & 0xFF;
                }
                return ret;
            }
            case LITTLE_ENDIAN: {
                int ret = 0;
                for (int i = bytes.length; i > 0; i--) {
                    ret <<= 8;
                    ret |= (int) bytes[i - 1] & 0xFF;
                }
                return ret;
            }
        }
        return INT_CONVERSION_ERROR;
    }

    public int readUInt(int offset) {
        return readUByte(offset) | (readUByte(offset) << 8) | (readUByte(offset) << 16) | (readUByte(offset) << 24);
    }

    public int readUShort(int offset) {
        return readUByte(offset) | (readUByte(offset) << 8);
    }

    public int readByte(int offset) {
        return data[offset++];
    }

    public int readUByte(int offset) {
        return data[offset++] & 0xFF;
    }

    public byte[] readRange(int offset, int size) {
        byte[] ret = new byte[size];
        for (int i = 0; i < size; i++)
            ret[i] = data[offset + i];
        return ret;
    }

    public byte[] calculateDexChecksum() {
        Checksum checksum = new Adler32();
        checksum.update(data, 0x0C, data.length - 0x0C);

        this.adler = checksum.getValue();
        int sum = (int) adler;
        byte[] bytes = new byte[4];
        bytes[0] = (byte) sum;
        bytes[1] = (byte) (sum >> 8);
        bytes[2] = (byte) (sum >> 16);
        bytes[3] = (byte) (sum >> 24);

        return bytes;
    }

    public long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        if (bytes.length < Long.BYTES) {
            byte[] data = new byte[Long.BYTES];
            for (int i = 0; i < data.length; i++)
                data[i] = 0;

            for (int i = bytes.length; i < data.length; i++)
                data[i] = bytes[i - bytes.length];
            bytes = data;
        }
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public byte[] calculateSignature() {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(data, 32, data.length - 32);

            byte[] rsult = md.digest();
            /*byte[] bytes = new byte[4];
            bytes[0] = (byte)sum;
            bytes[1] = (byte)(sum >> 8);
            bytes[2] = (byte)(sum >> 16);
            bytes[3] = (byte)(sum >> 24);*/

            this.sha1 = byteArrayToHexString(rsult);
            return rsult;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkEndianTag(byte[] endian_tag) {
        int endianInt = toInt(endian_tag, LITTLE_ENDIAN);
        return endianInt == ENDIAN_CONSTANT || endianInt == REVERSE_ENDIAN_CONSTANT;
    }

    public String getEndianness(byte[] endian_tag) {
        int endianInt = toInt(endian_tag, LITTLE_ENDIAN);
        if (endianInt == ENDIAN_CONSTANT) {
            return "standard endian (0x12345678)";
        } else if (endianInt == REVERSE_ENDIAN_CONSTANT) {
            return "reverse endian (0x78563412)";
        } else {
            return "unkwnown endianness";
        }

    }

    public byte[] readRangeUntil(int currentOffset, byte stopValue) {
        ArrayList<Byte> list = new ArrayList<>();
        byte current;
        do {
            current = this.data[currentOffset];
            list.add(current);
            currentOffset++;
        } while (current != stopValue);
        return toByteArray(list);
    }

    private byte[] toByteArray(List<Byte> in) {
        final int n = in.size();
        byte ret[] = new byte[n];
        for (int i = 0; i < n; i++) {
            ret[i] = in.get(i);
        }
        return ret;
    }

    public String getSha1() {
        return sha1.toUpperCase();
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public long getAdler() {
        return adler;
    }

    public void setAdler(long adler) {
        this.adler = adler;
    }

    public int getFileSize() {
        return this.data.length;
    }
}
