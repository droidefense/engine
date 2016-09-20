package ssdeep.core;
/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2014-05-14T19:16:46Z
 * LicenseName: GPL-2.0+
 * FileCopyrightText: <text> Copyright (c) 2014, Nuno Brito </text>
 * FileCopyrightText: <text> Copyright Jesse Kornblum </text>
 * FileComment: <text> 
    The code inside this file has been adapted from the SSDEEP code 
    authored by Jesse Kornblum at http://jessekornblum.com/

    Other portions of the code may derive from spamsum/TRN.
    </text> 
 */

import java.io.*;

public class SsdeepHashGen implements Serializable {
    private static byte[] b64;
    public final int SPAMSUM_LENGTH = 64;
    public final int FUZZY_MAX_RESULT = (SPAMSUM_LENGTH + (SPAMSUM_LENGTH / 2 + 20));
    public final int FALSE = 0;
    public final int TRUE = 1;
    public final int MIN_BLOCKSIZE = 3;
    public final int ROLLING_WINDOW = 7;
    public final int BUFFER_SIZE = 8192;
    public final int HASH_PRIME = 0x01000193;
    public final int HASH_INIT = 0x28021967;
    public final String b64String = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private RollingState roll_state;

    /**
     * Initializes the cls.
     */
    public SsdeepHashGen() {
        b64 = SpamSumSignature.GetBytes(b64String);
    }

    private static int Copy(byte[] source, int sourceIdx, byte[] destination, int destinationIdx, int maxLength) {
        for (int idx = 0; idx < maxLength; idx++) {
            if (sourceIdx + idx >= source.length) {
                return idx;
            }

            if (source[sourceIdx + idx] == 0) {
                return idx;
            }

            destination[destinationIdx + idx] = source[sourceIdx + idx];
        }

        return maxLength;
    }

    /**
     * **************************************************
     * CONSTRUCTOR
     * ***************************************************
     */

    public char[] snprintf(int size, String format, Object... args) {
        StringWriter writer = new StringWriter(size);
        PrintWriter out = new PrintWriter(writer);
        out.printf(format, args);
        out.close();
        String temp = writer.toString();
        char[] result = new char[temp.length()];
        for (int i = 0; i < result.length; i++)
            result[i] = temp.charAt(i);
        return result;
    }

    /**
     * a rolling hash, based on the Adler checksum. By using a rolling hash
     * we can perform auto resynchronisation after inserts/deletes
     * <p>
     * internally, h1 is the sum of the bytes in the window and h2
     * is the sum of the bytes times the index
     * <p>
     * h3 is a shift/xor based rolling hash, and is mostly needed to ensure that
     * we can cope with large blocksize values
     *
     * @param c The c.
     * @return Hash value
     */

    private long roll_hash(int c) {
        roll_state.h2 = (roll_state.h2 - roll_state.h1) & 0xffffffffL;
        roll_state.h2 = (roll_state.h2 + ((ROLLING_WINDOW * c) & 0xffffffffL)) & 0xffffffffL;

        roll_state.h1 = (roll_state.h1 + c) & 0xffffffffL;
        roll_state.h1 = (roll_state.h1 - roll_state.window[(int) (roll_state.n % ROLLING_WINDOW)]) & 0xffffffffL;

        roll_state.window[(int) (roll_state.n % ROLLING_WINDOW)] = c;
        roll_state.n++;

        // The original spamsum AND'ed this value with 0xFFFFFFFF which
        // in theory should have no effect. This AND has been removed
        // for performance (jk)
        roll_state.h3 = (roll_state.h3 << 5) & 0xffffffffL;
        roll_state.h3 = (roll_state.h3 ^ ((c) & 0xFF)) & 0xffffffffL;

        return (roll_state.h1 + roll_state.h2 + roll_state.h3) & 0xffffffffL;
    }

    /*****************************************************
     * HASH METHODS
     *****************************************************/

    /**
     * <p>
     * Reset the state of the rolling hash and return the initial rolling hash value
     * </p>
     *
     * @return Hash value
     */
    private long roll_reset() {
        roll_state = new RollingState();
        return 0;
    }

    /**
     * <p>
     * a simple non-rolling hash, based on the FNV hash
     * </p>
     *
     * @param c The c.
     * @param h The h.
     * @return Hash value
     */
    private long sum_hash(int c, long h) {
        h = (h * HASH_PRIME) & 0xFFFFFFFFL;
        h = (h ^ c) & 0xFFFFFFFFL;
        return h;
    }

    /**
     * Initializes the specified <paramref name="ctx">SpamSumContext</paramref>
     *
     * @param ctx    The SpamSum context.
     * @param stream The stream.
     * @return
     */
    private void ss_init(ss_context ctx, File handle) {
        if (null == ctx) {
            throw new IllegalArgumentException("ctx");
        }
        // ctx.ret = new byte[FUZZY_MAX_RESULT];

        if (handle != null) {
            ctx.total_chars = handle.length();
        }

        ctx.block_size = MIN_BLOCKSIZE;

        while (ctx.block_size * SPAMSUM_LENGTH < ctx.total_chars) {
            ctx.block_size = ctx.block_size * 2;
        }
    }
    /*
    private long sum_hash(byte c, long h)
    {
      h = (h * HASH_PRIME) & 0xffffffffL;
      h = (h ^ c) & 0xffffffffL;
      return h;
    }
    */

    /*
    private int ss_init(ss_context ctx, File handle)
    {
        if (null == ctx)
            return TRUE;

        if (handle != null)
            ctx.total_chars = handle.length();

        ctx.block_size = MIN_BLOCKSIZE;
        while (ctx.block_size * SPAMSUM_LENGTH < ctx.total_chars) {
            ctx.block_size = ctx.block_size * 2;
        }

        return FALSE;
    }
    */
    private void ss_engine(ss_context ctx, byte[] buffer, int buffer_size) {
        int i;

        if (null == ctx || null == buffer) {
            return;
        }

        for (i = 0; i < buffer_size; i++) {
            /*
               at each character we update the rolling hash and
               the normal hash. When the rolling hash hits the
               reset value then we emit the normal hash as a
               element of the signature and reset both hashes
            */
            if (buffer[i] >= 0) {
                ctx.h = roll_hash(buffer[i]);
                ctx.h2 = sum_hash(buffer[i], ctx.h2);
                ctx.h3 = sum_hash(buffer[i], ctx.h3);
            } else {
                ctx.h = roll_hash(buffer[i] + 256);
                ctx.h2 = sum_hash(buffer[i] + 256, ctx.h2);
                ctx.h3 = sum_hash(buffer[i] + 256, ctx.h3);
            }

            if (ctx.h % ctx.block_size == (ctx.block_size - 1)) {
                /* we have hit a reset point. We now emit a
               hash which is based on all chacaters in the
               piece of the message between the last reset
               point and this one */
                ctx.p[ctx.j] = b64[(int) (ctx.h2 % 64)];
                if (ctx.j < SPAMSUM_LENGTH - 1) {
                    /* we can have a problem with the tail
                       overflowing. The easiest way to
                       cope with this is to only reset the
                       second hash if we have room for
                       more characters in our
                       signature. This has the effect of
                       combining the last few pieces of
                       the message into a single piece */

                    ctx.h2 = HASH_INIT;
                    (ctx.j)++;
                }
            }

            /* this produces a second signature with a block size
               of block_size*2. By producing dual signatures in
               this way the effect of small changes in the message
               size near a block size boundary is greatly reduced. */
            if (ctx.h % (ctx.block_size * 2) == ((ctx.block_size * 2) - 1)) {
                ctx.ret2[ctx.k] = b64[(int) (ctx.h3 % 64)];
                if (ctx.k < SPAMSUM_LENGTH / 2 - 1) {
                    ctx.h3 = HASH_INIT;
                    (ctx.k)++;
                }
            }
        }
    }

    private int ss_update(ss_context ctx, RandomAccessFile stream) throws IOException {
        int bytes_read;
        byte[] buffer;

        if (null == ctx || null == stream) {
            return 1;
        }

        buffer = new byte[BUFFER_SIZE];

        //snprintf(ctx->ret, 12, "%u:", ctx->block_size);
        //ctx.ret = Encoding.ASCII.GetBytes(string.Format("{0}:", ctx.block_size));

        //ctx.p = ctx.ret + strlen(ctx.ret);

        //memset(ctx->p, 0, SPAMSUM_LENGTH+1);
        //memset(ctx->ret2, 0, sizeof(ctx->ret2));

        ctx.p = new byte[SPAMSUM_LENGTH + 1];
        ctx.ret2 = new byte[SPAMSUM_LENGTH / 2 + 1];

        ctx.k = ctx.j = 0;
        ctx.h3 = ctx.h2 = HASH_INIT;
        ctx.h = roll_reset();

        while ((bytes_read = stream.read(buffer, 0, buffer.length)) > 0) {
            ss_engine(ctx, buffer, bytes_read);
        }

        if (ctx.h != 0) {
            ctx.p[ctx.j] = b64[(int) (ctx.h2 % 64)];
            ctx.ret2[ctx.k] = b64[(int) (ctx.h3 % 64)];
            ctx.j++;
            ctx.k++;
        }

        //strcat(ctx.p + ctx.j, ":");
        //strcat(ctx.p + ctx.j, ctx.ret2);

        //ctx.p[ctx.j] = Encoding.ASCII.GetBytes(":")[0];
        //Copy(ctx.ret2, 0, ctx.p, ctx.j + 1, ctx.ret2.Length);

        //byte[] result = new byte[FUZZY_MAX_RESULT];
        //uint resultIdx = (uint)Copy(ctx.ret, 0, result, 0, ctx.ret.Length);
        //Copy(ctx.p, 0, result, resultIdx, ctx.p.Length);

        //ctx.ret = result;

        ctx.signature = new SpamSumSignature(ctx.block_size, GetArray(ctx.p, ctx.j), GetArray(ctx.ret2, ctx.k));

        return 0;
    }

    private byte[] GetArray(byte[] input, int maxLength) {
        if (input.length == maxLength) {
            return input;
        }

        byte[] output = new byte[maxLength];

        Copy(input, 0, output, 0, maxLength);

        return output;
    }

    /**
     * /// Calculates the SpamSum hash for specified <paramref name="stream"/>.
     *
     * @param file
     * @return SpamSum signature
     * @throws IOException
     */
    public String fuzzy_hash_file(File file) throws IOException {
        RandomAccessFile stream = new RandomAccessFile(file, "r");
        if (null == stream) {
            throw new IllegalArgumentException("stream");
        }

        boolean done = false;

        ss_context ctx = new ss_context();

        long filepos = stream.getFilePointer();

        ss_init(ctx, file);

        while (!done) {
            //stream.Seek(0, SeekOrigin.Begin);
            stream.seek(0);

            ss_update(ctx, stream);

            // our blocksize guess may have been way off - repeat if necessary
            if (ctx.block_size > MIN_BLOCKSIZE && ctx.j < SPAMSUM_LENGTH / 2) {
                ctx.block_size = ctx.block_size / 2;
            } else {
                done = true;
            }
        }

        //strncpy(result, ctx.ret, FUZZY_MAX_RESULT);
        //byte[] result = new byte[FUZZY_MAX_RESULT];
        //Copy(ctx.ret, 0, result, 0, FUZZY_MAX_RESULT);

        //stream.Position = filepos;
        stream.seek(filepos);

        // close the stream
        stream.close();

        return ctx.signature.toString();
    }

    public String fuzzy_hash_file(String file) throws IOException {
        File f = new File(file);
        return this.fuzzy_hash_file(f);
    }

    /**
     * we only accept a match if we have at least one common substring in
     * the signature of length ROLLING_WINDOW. This dramatically drops the
     * false positive rate for low score thresholds while having
     * negligable affect on the rate of spam detection.
     * <p>
     * return 1 if the two strings do have a common substring, 0 otherwise
     *
     * @param s1 The s1.
     * @param s2 The s2.
     * @return
     */
    private int has_common_substring(byte[] s1, byte[] s2) {
        int i, j;
        int num_hashes;
        long[] hashes = new long[SPAMSUM_LENGTH];

        /* there are many possible algorithms for common substring
           detection. In this case I am re-using the rolling hash code
           to act as a filter for possible substring matches */

        roll_reset();

        /* first compute the windowed rolling hash at each offset in
           the first string */
        for (i = 0; i < s1.length; i++) {
            hashes[i] = roll_hash(s1[i]);
        }
        num_hashes = i;

        roll_reset();

        /* now for each offset in the second string compute the
           rolling hash and compare it to all of the rolling hashes
           for the first string. If one matches then we have a
           candidate substring match. We then confirm that match with
           a direct string comparison */
        for (i = 0; i < s2.length; i++) {
            long h = roll_hash(s2[i]);
            if (i < ROLLING_WINDOW - 1) {
                continue;
            }
            for (j = ROLLING_WINDOW - 1; j < num_hashes; j++) {
                if (hashes[j] != 0 && hashes[j] == h) {
                    /* we have a potential match - confirm it */
                    //if (strlen(s2 + i - (ROLLING_WINDOW - 1)) >= ROLLING_WINDOW &&
                    //    strncmp(s2 + i - (ROLLING_WINDOW - 1),
                    //            s1 + j - (ROLLING_WINDOW - 1),
                    //            ROLLING_WINDOW) == 0)
                    if ((s2.length - i - (ROLLING_WINDOW - 1)) >= ROLLING_WINDOW && ArrayCompare(s2, (s2.length - i - (ROLLING_WINDOW - 1)), s1, (s1.length - j - (ROLLING_WINDOW - 1)), ROLLING_WINDOW) == 0) {
                        return 1;
                    }
                }
            }
        }

        return 0;
    }

    /**
     * @return -1 : not equals
     * 0 : equals
     * 1 : array1 is not long enough
     * 2 : array2 is not long enough
     */
    private int ArrayCompare(byte[] array1, int idx1, byte[] array2, int idx2, int rollingWindow) {
        boolean result;

        for (int a = 0; a < rollingWindow; a++) {
            if ((a + idx1) > array1.length) {
                return 1;
            }

            if ((a + idx2) > array2.length) {
                return 2;
            }

            result = (array1[a + idx1] == array2[a + idx2]);

            if (!result) {
                return -1;
            }
        }
        return 0;
    }

    /*****************************************************
     * HASH COMPARISSON METHODS
     *****************************************************/

    /**
     * eliminate sequences of longer than 3 identical characters. These
     * sequences contain very little information so they tend to just bias
     * the result unfairly
     *
     * @param str The STR.
     * @return
     */
    private byte[] eliminate_sequences(byte[] str) {
        byte[] ret;
        int i, j, len;

        ret = str.clone();

        len = str.length;

        for (i = j = 3; i < len; i++) {
            if (str[i] != str[i - 1] || str[i] != str[i - 2] || str[i] != str[i - 3]) {
                ret[j++] = str[i];
            }
        }

        ret[j] = 0;

        return ret;
    }

    private byte[] eliminate_sequences2(byte[] str) {
        byte[] ret;
        int i, j, len;

        len = str.length;
        ret = new byte[len];

        for (i = j = 3; i < len; i++) {
            if (str[i] != str[i - 1] || str[i] != str[i - 2] || str[i] != str[i - 3]) {
                ret[j++] = str[i];
            }
        }
        return ret;
    }

    /**
     * this is the low level string scoring algorithm. It takes two strings
     * and scores them on a scale of 0-100 where 0 is a terrible match and
     * 100 is a great match. The block_size is used to cope with very small
     * messages.
     */
    private long score_strings(byte[] s1, byte[] s2, long block_size) {
        long score;
        int len1, len2;
        len1 = s1.length;
        len2 = s2.length;

        if (len1 > SPAMSUM_LENGTH || len2 > SPAMSUM_LENGTH) {
            /* not a real spamsum signature? */
            return 0;
        }

        /* the two strings must have a common substring of length
           ROLLING_WINDOW to be candidates */
        if (has_common_substring(s1, s2) == 0) {
            return 0;
        }

        /* compute the edit distance between the two strings. The edit distance gives
           us a pretty good idea of how closely related the two strings are */
        score = edit_distn(s1, len1, s2, len2);

        /* scale the edit distance by the lengths of the two
           strings. This changes the score to be a measure of the
           proportion of the message that has changed rather than an
           absolute quantity. It also copes with the variability of
           the string lengths. */
        score = (score * SPAMSUM_LENGTH) / (len1 + len2);

        /* at this stage the score occurs roughly on a 0-64 scale,
         * with 0 being a good match and 64 being a complete
         * mismatch */

        /* rescale to a 0-100 scale (friendlier to humans) */
        score = (100 * score) / 64;

        /* it is possible to get a score above 100 here, but it is a
           really terrible match */
        if (score >= 100) {
            return 0;
        }

        /* now re-scale on a 0-100 scale with 0 being a poor match and
           100 being a excellent match. */
        score = 100 - score;

        /* when the blocksize is small we don't want to exaggerate the match size */
        if (score > block_size / MIN_BLOCKSIZE * Math.min(len1, len2)) {
            score = block_size / MIN_BLOCKSIZE * Math.min(len1, len2);
        }
        return score;
    }

    private int edit_distn(byte[] s1, int len1, byte[] s2, int len2) {
        return EditDistance.edit_distn(s1, len1, s2, len2);
    }

    /**
     * given two spamsum signature return a value indicating the degree to which they match.
     *
     * @param signature1 The first signature.
     * @param signature2 The second signature.
     * @return
     */
    public int Compare(SpamSumSignature signature1, SpamSumSignature signature2) {
        long block_size1, block_size2;
        long score;
        byte[] s1, s2;
        byte[] s1_1, s1_2;
        byte[] s2_1, s2_2;

        if (null == signature1 || null == signature2) {
            return -1;
        }
        //string str1, str2;
        //int str1Idx, str2Idx;

        //str1 = Encoding.ASCII.GetString(bytes1);
        //str2 = Encoding.ASCII.GetString(bytes2);

        //// each spamsum is prefixed by its block size
        //if (sscanf(str1, "%u:", &block_size1) != 1 ||
        //    sscanf(str2, "%u:", &block_size2) != 1) {
        //  return -1;
        //}

        //str1Idx = str1.IndexOf(':');
        //str2Idx = str1.IndexOf(':');

        //block_size1 = uint.Parse(str1.Substring(0, str1Idx));
        //block_size2 = uint.Parse(str2.Substring(0, str2Idx));
        block_size1 = signature1.getBlockSize();
        block_size2 = signature2.getBlockSize();

        // if the blocksizes don't match then we are comparing
        // apples to oranges. This isn't an 'error' per se. We could
        // have two valid signatures, but they can't be compared.
        if (block_size1 != block_size2 && block_size1 != block_size2 * 2 && block_size2 != block_size1 * 2) {
            return 0;
        }

        // move past the prefix
        //str1Idx++;
        //str2Idx++;

        //if (str1Idx >= str1.Length || str2Idx >= str2.Length)
        //{
        //    // badly formed ...
        //    return -1;
        //}

        // there is very little information content is sequences of
        // the same character like 'LLLLL'. Eliminate any sequences
        // longer than 3. This is especially important when combined
        // with the has_common_substring() test below.
        //s1 = eliminate_sequences(Encoding.ASCII.GetBytes(str1.Substring(str1Idx)));
        //s2 = eliminate_sequences(Encoding.ASCII.GetBytes(str2.Substring(str2Idx)));
        s1 = eliminate_sequences2(signature1.getHashPart1());
        s2 = eliminate_sequences2(signature2.getHashPart1());

        //if (!s1 || !s2) return 0;

        // now break them into the two pieces
        s1_1 = s1;
        s2_1 = s2;

        //s1_2 = strchr(s1, ':');
        //s2_2 = strchr(s2, ':');
        //string s1_2_str = Encoding.ASCII.GetString(s1);
        //string s2_2_str = Encoding.ASCII.GetString(s2);

        //s1_2 = Encoding.ASCII.GetBytes(s1_2_str.Substring(s1_2_str.IndexOf(':') + 1));
        //s2_2 = Encoding.ASCII.GetBytes(s2_2_str.Substring(s2_2_str.IndexOf(':') + 1));
        s1_2 = eliminate_sequences2(signature1.getHashPart2());
        s2_2 = eliminate_sequences2(signature2.getHashPart2());

        //if (!s1_2 || !s2_2) {
        //  // a signature is malformed - it doesn't have 2 parts
        //  return 0;
        //}

        //*s1_2++ = 0;
        //*s2_2++ = 0;

        // each signature has a string for two block sizes. We now
        // choose how to combine the two block sizes. We checked above
        // that they have at least one block size in common
        if (block_size1 == block_size2) {
            long score1, score2;
            score1 = score_strings(s1_1, s2_1, block_size1);
            score2 = score_strings(s1_2, s2_2, block_size2);

            //    s->block_size = block_size1;

            score = Math.max(score1, score2);
        } else if (block_size1 == block_size2 * 2) {
            score = score_strings(s1_1, s2_2, block_size1);
            //    s->block_size = block_size1;
        } else {
            score = score_strings(s1_2, s2_1, block_size2);
            //    s->block_size = block_size2;
        }

        return (int) score;
    }

    private int MAX(int a, int b) {
        if (a > b)
            return a;
        return b;
    }

    private int MIN(int a, int b) {
        if (a < b)
            return a;
        return b;
    }

    private class ss_context implements Serializable {
        //public byte ret, p;
        //public byte[] ret, p;
        public byte[] p;
        public long total_chars;
        public long h, h2, h3;
        public int j, n, i, k;
        public long block_size;
        public byte[] ret2;
        public SpamSumSignature signature; // ret has been replaced with SpamSumSignature

        public ss_context() {
            ret2 = new byte[SPAMSUM_LENGTH / 2 + 1];

            //ret = p = default(byte[]);
            p = null;
            total_chars = h = h2 = h3 = block_size = 0;
            j = n = i = k = 0;
        }
    }

    private class RollingState implements Serializable {
        public int[] window;
        public long h1, h2, h3;
        public long n;

        public RollingState() {
            window = new int[ROLLING_WINDOW];
            h1 = h2 = h3 = n = 0;
        }
    }
}
