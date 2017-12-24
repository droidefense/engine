package droidefense.sdk.ssdeep;

/**
 * Created https://github.com/phishme/jssdeep/blob/master/src/java/com/phishme/ssdeep/SSDeepHash.java
 */

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * This class represents an SSDeepHash value.  It is immutable and threadsafe, instances of this class can be shared
 * among threads and used for comparison.
 */
public class SSDeepHash {
    static private final Pattern nullPattern = Pattern.compile("\0");
    static private final int EDIT_DISTN_INSERT_COST = 1;
    static private final int EDIT_DISTN_REMOVE_COST = 1;
    static private final int EDIT_DISTN_REPLACE_COST = 2;

    /**
     * the blocksize used by the program,
     */
    final private int blocksize;

    /**
     * the hash for this blocksize
     */
    final private String hash;
    final private long[] hashChunks;
    final private long[] hash2Chunks;
    /**
     * the filename.
     */
    final private String filename;
    /**
     * the hash for twice the blocksize,
     */
    private String hash2;

    /**
     * Construct a new SSDeepHash based on the string representation of the hash.
     *
     * @param ssDeepHash The string representation of the hash.
     * @throws NullPointerException     If the provided parameter is null.
     * @throws IllegalArgumentException If the provided hash is invalid.
     */
    SSDeepHash(String ssDeepHash) {
        ssDeepHash = nullPattern.matcher(ssDeepHash).replaceAll("");
        int firstColonIndex = ssDeepHash.indexOf(':');
        int secondColonIndex = ssDeepHash.indexOf(':', firstColonIndex + 1);
        int firstCommaIndex = ssDeepHash.indexOf(',', secondColonIndex + 1);
        if (firstColonIndex <= 0 || secondColonIndex <= 0 || firstColonIndex == ssDeepHash.length() - 1 || secondColonIndex == ssDeepHash.length() - 1 || firstColonIndex == secondColonIndex) {
            throw new IllegalArgumentException("Invalid SSDeep hash provided.");
        }
        this.blocksize = Integer.parseInt(ssDeepHash.substring(0, firstColonIndex));
        if (this.blocksize <= 0) {
            throw new IllegalArgumentException("The blockSize must be greater than 0.");
        }
        this.hash = ssDeepHash.substring(firstColonIndex + 1, secondColonIndex);
        this.hashChunks = generateChunks(hash);

        this.hash2 = ssDeepHash.substring(secondColonIndex + 1, (firstCommaIndex < 0) ? ssDeepHash.length() : firstCommaIndex);
        this.hash2Chunks = generateChunks(hash2);

        if (firstCommaIndex >= 0 && firstCommaIndex < ssDeepHash.length() - 1) {
            String filename = ssDeepHash.substring(firstCommaIndex + 1).trim();
            while (filename.endsWith("\"")) {
                filename = filename.substring(0, filename.length() - 1);
            }
            while (filename.startsWith("\"")) {
                filename = filename.substring(1);
            }
            filename = filename.trim();
            this.filename = filename.length() > 0 ? filename : null;
        } else {
            this.filename = null;
        }
    }

    /**
     * Construct a new hash based on the provided blockSize, hash, hash2.
     *
     * @param blockSize The blocksize of this hash.
     * @param hash      The hash of this hash.
     * @param hash2     The double hash of this hash.
     */
    SSDeepHash(final int blockSize, final String hash, final String hash2) {
        this(blockSize, hash, hash2, null);
    }

    /**
     * Construct a new hash based on the provided blockSize, hash, hash2 and filename.
     *
     * @param blockSize The blocksize of this hash.
     * @param hash      The hash of this hash.
     * @param hash2     The double hash of this hash.
     * @param filename  The file which was hashed or null if this is unknown.
     */
    SSDeepHash(final int blockSize, final String hash, final String hash2, final String filename) {
        this("" + blockSize + ":" + hash + ":" + hash2 + ((filename != null && filename.length() > 0) ? ",\"" + filename + "\"" : ""));
    }

    /**
     * The blocksize of this hash.
     *
     * @return the blocksize of this hash.
     */
    public int getBlocksize() {
        return blocksize;
    }

    /**
     * Return the hash portion of this hash represented as a byte array.
     *
     * @return The hash portion represented as a byte array.
     */
    public byte[] getHash() {
        return hash.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Return the hash as a string representation.
     *
     * @return The hash portion represented as a string.
     */
    public String getHashString() {
        return hash;
    }

    /**
     * Retrieve the unique hash chunks in the rolling window of this hash represented as long values.  This array is
     * guaranteed not to contain any duplicates, it is essentially a Set and is also guaranteed to be sorted in ascending
     * order and is suitable for use in Arrays.binarySearch().
     *
     * @return Retrieve the unique hash chunks in the rolling window of this hash represented as long values and sorted
     * in ascending order.
     */
    public long[] getHashChunks() {
        return hashChunks.clone();
    }

    /**
     * Return the hash2 portion of this hash represented as a byte array.
     *
     * @return The hash2 portion represented as a byte array.
     */
    public byte[] getHash2() {
        return hash2.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Return the hash2 as a string representation.
     *
     * @return The hash2 portion represented as a string.
     */
    public String getHash2String() {
        return hash2;
    }

    /**
     * Retrieve the unique hash2 chunks in the rolling window of this hash represented as long values.  This array is
     * guaranteed not to contain any duplicates, it is essentially a Set and is also guaranteed to be sorted in ascending
     * order and is suitable for use in Arrays.binarySearch().
     *
     * @return Retrieve the unique hash2 chunks in the rolling window of this hash represented as long values and sorted
     * in ascending order.
     */
    public long[] getHash2Chunks() {
        return hash2Chunks.clone();
    }

    /**
     * The filename of the hashed file, or null if not available.
     *
     * @return the filename of the hashed file or null if this information is not available.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Return the SSDeep hash represented by this instance.
     *
     * @return The SSDeep hash represented by this instance.
     */
    @Override
    public String toString() {
        return this.blocksize + ":" + getHashString() + ":" + getHash2String() + ((filename != null && filename.length() > 0) ? ",\"" + filename + "\"" : "");
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }

        if (!(that instanceof SSDeepHash)) {
            return false;
        }
        SSDeepHash other = (SSDeepHash) that;
        return toString().equals(other.toString());
    }

    /**
     * Return a number between 0 and 100 inclusive where 0 is a terrible match and 100 is a great match.
     *
     * @param that The other SSDeep hash to compare against.
     * @return A number betwen 0 and 100 representing the fuzzy closeness of the two items.  100 is a very good match.
     */
    public int compare(SSDeepHash that) {
        return fuzzyCompare(this, that);
    }

    /**
     * Retrieve a Set of Long values representing the 6 byte sequences of the hash within the rolling window.  If there
     * is no overlap in the chunk sets from two different hashes their comparison will be 0, this can be used in certain
     * circumstances to optimize droidefense.ssdeep comparisons on large data sets.
     *
     * @param chunkHash The string representation of the chunk hash.
     * @return The Set of unique Long values represented within the rolling window of the hash value.
     */
    private long[] generateChunks(final String chunkHash) {
        final SortedSet<Long> chunks = new TreeSet<>();
        final String chunkHashString = eliminateSequences(chunkHash);

        for (int start = 0, end = SSDeep.ROLLING_WINDOW; end <= chunkHashString.length(); ++start, ++end) {
            int i = 0;
            long result = 0;
            for (final byte b : chunkHashString.substring(start, end).getBytes(StandardCharsets.UTF_8)) {
                result |= ((long) (b & 0xFF)) << (i * 8);
                ++i;
            }
            chunks.add(result);
        }

        int i = 0;
        long[] result = new long[chunks.size()];
        for (Long chunk : chunks) {
            result[i++] = chunk;
        }
        return result;
    }

    /**
     * given two spamsum strings return a value indicating the degree to which they match.
     *
     * @param fh1 The first droidefense.ssdeep hash to compare.
     * @param fh2 The second droidefense.ssdeep hash to compare.
     * @return The score indicating the degree to which the hashes match.
     */
    private int fuzzyCompare(final SSDeepHash fh1, final SSDeepHash fh2) {
        int score;

        // each signature has a string for two block sizes. We now
        // choose how to combine the two block sizes. We checked above
        // that they have at least one block size in common
        if (fh1.getBlocksize() == fh2.getBlocksize()) {
            if (fh1.getHashString().equals(fh2.getHashString()) && fh1.getHash2String().equals(fh2.getHash2String())) {
                score = 100;
            } else {
                final int a = hasOverlap(fh1.hashChunks, fh2.hashChunks) ? scoreStrings(fh1.getHashString(), fh2.getHashString(), fh1.getBlocksize()) : 0;
                if (a < 100) {
                    final int b = hasOverlap(fh1.hash2Chunks, fh2.hash2Chunks) ? scoreStrings(fh1.getHash2String(), fh2.getHash2String(), fh1.getBlocksize() * 2) : 0;
                    score = Math.max(a, b);
                } else {
                    score = 100;
                }
            }
        } else if (fh1.getBlocksize() == fh2.getBlocksize() * 2) {
            score = hasOverlap(fh1.hashChunks, fh2.hash2Chunks) ? scoreStrings(fh1.getHashString(), fh2.getHash2String(), fh1.getBlocksize()) : 0;
        } else if (fh2.getBlocksize() == fh1.getBlocksize() * 2) {
            score = hasOverlap(fh1.hash2Chunks, fh2.hashChunks) ? scoreStrings(fh1.getHash2String(), fh2.getHashString(), fh2.getBlocksize()) : 0;
        } else {
            // if the blocksizes don't match then we are comparing
            // apples to oranges. This isn't an 'error' per se. We could
            // have two valid signatures, but they can't be compared.
            score = 0;
        }

        return score;
    }

    private boolean hasOverlap(final long[] chunksA, final long[] chunksB) {
        long[] a, b;
        if (chunksA.length >= chunksB.length) {
            a = chunksA;
            b = chunksB;
        } else {
            a = chunksB;
            b = chunksA;
        }

        for (long value : b) {
            if (Arrays.binarySearch(a, value) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * eliminate sequences of longer than 3 identical characters. These
     * sequences contain very little information so they tend to just bias
     * the result unfairly
     *
     * @param string The string to have duplicate sequences removed from.
     * @return A string with any character runs greater than 3 truncated to be 3 characters long.
     */
    private String eliminateSequences(final String string) {
        final char[] str = string.toCharArray();
        final StringBuilder ret = new StringBuilder();
        boolean isModified = false;

        // Do not include repeats:
        if (str.length >= 1) {
            ret.append(str[0]);
            if (str.length >= 2) {
                ret.append(str[1]);
                if (str.length >= 3) {
                    ret.append(str[2]);
                    for (int i = 3; i < str.length; i++) {
                        if (str[i] != str[i - 1] || str[i] != str[i - 2] || str[i] != str[i - 3]) {
                            ret.append(str[i]);
                        } else {
                            isModified = true;
                        }
                    }
                }
            }
        }

        return isModified ? ret.toString() : string;
    }

    /**
     * this is the low level string scoring algorithm. It takes two strings
     * and scores them on a scale of 0-100 where 0 is a terrible match and
     * 100 is a great match. The block_size is used to cope with very small
     * messages.
     *
     * @param s1Raw     The first string to compare.
     * @param s2Raw     The second string to compare.
     * @param blockSize The blocksize.
     * @return A number between 0 and 100 inclusive where 0 is a terrible match and 100 is a great match.
     */
    private int scoreStrings(final String s1Raw, final String s2Raw, final int blockSize) {
        int score = 0;
        // there is very little information content is sequences of
        // the same character like 'LLLLL'. Eliminate any sequences
        // longer than 3. This is especially important when combined
        // with the has_common_substring() test below.
        final String s1 = eliminateSequences(s1Raw);
        final String s2 = eliminateSequences(s2Raw);
        final int len1 = s1.length();
        final int len2 = s2.length();

        // ensure that both strings represent real spamsum signatures.
        if (len1 <= SSDeep.SPAMSUM_LENGTH && len2 <= SSDeep.SPAMSUM_LENGTH) {

            // compute the edit distance between the two strings. The edit distance gives
            // us a pretty good idea of how closely related the two strings are
            score = calculateLevenshteinDistance(s1, s2);
            // scale the edit distance by the lengths of the two
            // strings. This changes the score to be a measure of the
            // proportion of the message that has changed rather than an
            // absolute quantity. It also copes with the variability of
            // the string lengths.
            score = (score * SSDeep.SPAMSUM_LENGTH) / (len1 + len2);
            // at this stage the score occurs roughly on a 0-64 scale,
            // with 0 being a good match and 64 being a complete
            // mismatch.

            // rescale to a 0-100 scale (friendlier to humans)
            score = (100 * score) / SSDeep.SPAMSUM_LENGTH;

            // now re-scale on a 0-100 scale with 0 being a poor match and 100 being a excellent match.
            score = 100 - score;

            // when the blocksize is small we don't want to exaggerate the match size.
            if (blockSize < (99 + SSDeep.ROLLING_WINDOW) / SSDeep.ROLLING_WINDOW * SSDeep.MIN_BLOCKSIZE) {
                final int maxScore = blockSize / SSDeep.MIN_BLOCKSIZE * Math.min(len1, len2);
                if (score > maxScore) {
                    score = maxScore;
                }
            }

            // Ensure that we are capped between 0 and 100.
            score = Math.max(0, Math.min(100, score));
        }
        return score;
    }

    /**
     * Modified levenshtein distance calculation
     * <p>
     * This program can be used, redistributed or modified under any of Boost Software License 1.0, GPL v2 or GPL v3
     * See the file COPYING for details.
     * <p>
     * This method was ported from edit_dist.c distributed with the droidefense.ssdeep source code from version 2.13, this is
     * a slightly modified Levenshtein distince calculation which is used by the droidefense.ssdeep application when calculating
     * the score between strings.
     * <p>
     * Copyright (C) 2014 kikairoya &lt;kikairoya@gmail.com&gt;
     * Copyright (C) 2014 Jesse Kornblum &lt;research@jessekornblum.com&gt;
     * Copyright (C) 2016 Russell Francis &lt;russell.francis@phishme.com&gt;
     */
    private int calculateLevenshteinDistance(final String a, final String b) {
        int firstIndex = 0, secondIndex = 1, tempIndex;
        final int[][] t = new int[2][Math.max(a.length(), b.length()) + 1];
        final char[] aa = a.toCharArray();
        final char[] ba = b.toCharArray();
        for (int i = 0; i <= b.length(); ++i) {
            t[0][i] = i * EDIT_DISTN_REMOVE_COST;
        }
        for (int i = 0; i < a.length(); ++i) {
            t[secondIndex][0] = (i + 1) * EDIT_DISTN_INSERT_COST;
            for (int j = 0; j < b.length(); ++j) {
                final int costA = t[firstIndex][j + 1] + EDIT_DISTN_INSERT_COST;
                final int costD = t[secondIndex][j] + EDIT_DISTN_REMOVE_COST;
                final int costR = t[firstIndex][j] + (aa[i] == ba[j] ? 0 : EDIT_DISTN_REPLACE_COST);
                t[secondIndex][j + 1] = Math.min(Math.min(costA, costD), costR);
            }
            tempIndex = firstIndex;
            firstIndex = secondIndex;
            secondIndex = tempIndex;
        }
        return t[firstIndex][b.length()];
    }
}
