package droidefense.ssdeep.core;

import java.io.Serializable;

/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2012-06-29T18:49:54Z
 * LicenseName: GPL-2.0+
 * FileCopyrightText: <text> Copyright 2012 Nuno Brito, TripleCheck </text>
 * FileCopyrightText: <text> Copyright Jesse Kornblum </text>
 * FileComment: <text> 
    The code inside this file has been adapted from the SSDEEP code 
    authored by Jesse Kornblum at http://jessekornblum.com/

    Other portions of the code may derive from spamsum/TRN.
    </text> 
 */
public class SpamSumSignature implements Serializable {
    /**
     * **************************************************
     * FIELDS
     * ***************************************************
     */
    private /*uint*/ long blockSize;
    private byte[] hash1;
    private byte[] hash2;

    /*****************************************************
     * UTILS
     *****************************************************/
    /**
     * <p>
     * Initializes a new instance of the {@code SpamSumSignature} cls.
     * </p>
     *
     * @param signature The signature.
     */
    public SpamSumSignature(String signature) {
        if (null == signature)
            throw new IllegalArgumentException("Signature string cannot be null or empty." + "\r\nParameter name: " + "signature");

        int idx1 = signature.indexOf(':');
        int idx2 = signature.indexOf(':', idx1 + 1);

        if (idx1 < 0)
            throw new IllegalArgumentException("Signature is not valid." + "\r\nParameter name: " + "signature");

        if (idx2 < 0)
            throw new IllegalArgumentException("Signature is not valid." + "\r\nParameter name: " + "signature");

        blockSize = Integer.parseInt(signature.substring((0), (0) + (idx1)));
        hash1 = GetBytes(signature.substring(idx1 + 1, idx1 + 1 + idx2 - idx1 - 1));
        hash2 = GetBytes(signature.substring(idx2 + 1));
    }

    public SpamSumSignature(long blockSize, byte[] hash1, byte[] hash2) {
        this.blockSize = blockSize;
        this.hash1 = hash1;
        this.hash2 = hash2;
    }
    /*****************************************************
     * CONSTRUCTOR
     *****************************************************/

    /**
     * <p>
     * Change a string into an array of bytes
     * </p>
     */
    public static byte[] GetBytes(String str) {
        byte[] r = new byte[str.length()];
        for (int i = 0; i < r.length; i++)
            r[i] = (byte) str.charAt(i);
        return r;
    }

    /**
     * <p>
     * Change a string into an array of bytes
     * </p>
     */
    public static String GetString(byte[] hsh) {
        String r = "";
        for (int i = 0; i < hsh.length; i++)
            r += (char) hsh[i];
        return r;
    }

    /**
     * **************************************************
     * METHODS
     * ***************************************************
     */

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SpamSumSignature))
            return false;

        return this.equals((SpamSumSignature) obj);
    }

    public boolean equals(SpamSumSignature other) {
        if (this.blockSize != other.blockSize)
            return false;

        if (this.hash1.length != other.hash1.length)
            return false;

        if (this.hash2.length != other.hash2.length)
            return false;

        for (int idx = 0; idx < hash1.length; idx++) {
            if (this.hash1[idx] != other.hash1[idx])
                return false;
        }

        for (int idx = 0; idx < hash2.length; idx++) {
            if (this.hash2[idx] != other.hash2[idx])
                return false;
        }

        return true;
    }

    @Override
    public String toString() {
        String hashText1 = GetString(hash1);
        String hashText2 = GetString(hash2);
        return blockSize + ":" + hashText1 + ":" + hashText2;
    }

    /*****************************************************
     * PROPERTIES
     *****************************************************/

    /**
     * <p>
     * Gets the size of the block.
     * </p>Value: The size of the block.
     */
    public /*uint*/long getBlockSize() {
        return blockSize;
    }

    /**
     * <p>
     * Gets the first hash part.
     * </p>Value: The first hash part.
     */
    public byte[] getHashPart1() {
        return hash1;
    }

    /**
     * <p>
     * Gets the second hash part.
     * </p>Value: The second hash part.
     */
    public byte[] getHashPart2() {
        return hash2;
    }
}

