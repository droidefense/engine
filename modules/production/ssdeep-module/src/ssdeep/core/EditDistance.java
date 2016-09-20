package ssdeep.core;

import java.io.Serializable;

/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2014-05-14T19:16:46Z
 * LicenseName: GPL-2.0+
 * FileCategory: SOURCE
 * FileCopyrightText: <text> Copyright (c) 2014, Nuno Brito </text>
 * FileCopyrightText: <text> Copyright Jesse Kornblum </text>
 * FileComment: <text> 
    The code inside this file has been adapted from the SSDEEP code 
    authored by Jesse Kornblum at http://jessekornblum.com/

    Other portions of the code may derive from spamsum/TRN.
    </text> 
 */
public class EditDistance implements Serializable {
    /*
      This edit distance code is taken from trn3.6. A few minor
      modifications have been made by Andrew Tridgell <tridge@samba.org>
      for use in spamsum.
    */


    /**
     * ***********************************************************************
     */


    /* The authors make no claims as to the fitness or correctness of this software
     * for any use whatsoever, and it is provided as is. Any use of this software
     * is at the user's own risk.
     */

    /* edit_dist -- returns the minimum edit distance between two strings

        Program by:  Mark Maimone   CMU Computer Science   13 Nov 89
        Last Modified:  28 Jan 90

       If the input strings have length n and m, the algorithm runs in time
       O(nm) and space O(min(m,n)).

    HISTORY
       13 Nov 89 (mwm) Created edit_dist() and set_costs().

       28 Jan 90 (mwm) Added view_costs().  Should verify that THRESHOLD
       computations will work even when THRESHOLD is not a multiple of
       sizeof(int).

       17 May 93 (mwm) Improved performance when used with trn's newsgroup
       processing; assume all costs are 1, and you can terminate when a
       threshold is exceeded.
    */

    private static final int MIN_DIST = 100;

    /* Use a less-general version of the
       routine, one that's better for trn.
       All change costs are 1, and it's okay
       to terminate if the edit distance is
       known to exceed MIN_DIST */

    private static final int THRESHOLD = 4000;     /* worry about allocating more memory only
                                                   when this # of bytes is exceeded */

    private static final int STRLENTHRESHOLD = (THRESHOLD / (Integer.SIZE / 8) - 3) / 2;

    // #define SAFE_ASSIGN(x,y) (((x) != NULL) ? (*(x) = (y)) : (y))
    static int insert_cost = 1;
    static int delete_cost = 1;
    static int _iswap;             /* swap_int temp variable */
    static char _cswap;             /* swap_char temp variable */
    static int _mx, _my, _mz;      /* min2, min3 temp variables */
    static int row, col, index = 0; // dynamic programming counters
    static int radix; // radix for modular indexing
    static int low;
    static int[] buffer; /* pointer to storage for one row
                               of the d.p. array */
    static int[] store = new int[THRESHOLD / (Integer.SIZE / 8)];
    static int ins = 1;
    static int del = 1;
    static int ch = 3;
    static int swap_cost = 5;
    /* a small amount of static
       storage, to be used when the
       input strings are small enough */

    /* Handle trivial cases when one string is empty */
    static int from_len;
    static int to_len;

    // #define swap_int(x,y)  (_iswap = (x), (x) = (y), (y) = _iswap)
    private static void swap_int(int[] x, int[] y) {
        int _iswap = x[0];
        x[0] = y[0];
        y[0] = _iswap;
    }

    // #define swap_char(x,y) (_cswap = (x), (x) = (y), (y) = _cswap)
    private static void swap_char(/*ref*/ byte[][] x, /*ref*/ byte[][] y) {
        byte[] _cswap = (x[0]);
        (x[0]) = (y[0]);
        (y[0]) = _cswap;
    }

    //#define min3(x,y,z) (_mx = (x), _my = (y), _mz = (z), (_mx < _my ? (_mx < _mz ? _mx : _mz) : (_mz < _my) ? _mz : _my))
    private static int min3(int x, int y, int z) {
        return ((x) < (y) ? ((x) < (z) ? (x) : (z)) : ((z) < (y)) ? (z) : (y));
    }

    //#define min2(x,y) (_mx = (x), _my = (y), (_mx < _my ? _mx : _my))
    private static int min2(int x, int y) {
        return ((x) < (y) ? (x) : (y));
    }

    private static int ar(int x, int y, int index) {
        return (((x) == 0) ? (y) * del : (((y) == 0) ? (x) * ins : buffer[mod(index)]));
    }

    private static int NW(int x, int y) {
        return ar(x, y, index + from_len + 2);
    }

    private static int N(int x, int y) {
        return ar(x, y, index + from_len + 3);
    }

    private static int W(int x, int y) {
        return ar(x, y, index + radix - 1);
    }

    private static int NNWW(int x, int y) {
        return ar(x, y, index + 1);
    }

    private static int mod(int x) {
        return ((x) % radix);
    }

    /* edit_distn -- returns the edit distance between two strings, or -1 on
       failure */
    public static int edit_distn(byte[] from, int _from_len, byte[] to, int _to_len) {
        from_len = _from_len;
        to_len = _to_len;

        if (from == null) {
            if (to == null) {
                return 0;
            } else {
                return to_len * insert_cost;
            }
        } else if (to == null) {
            return from_len * delete_cost;
        }

        /* Initialize registers */

        radix = 2 * from_len + 3;

        /* Make   from   short enough to fit in the static storage, if it's at all possible */

        if (from_len > to_len && from_len > STRLENTHRESHOLD) {
            int[] x = new int[1];
            int[] y = new int[1];
            x[0] = from_len;
            y[0] = to_len;
            swap_int(x, y);
            byte[][] xx = new byte[1][];
            byte[][] yy = new byte[1][];
            xx[0] = from;
            yy[0] = to;
            swap_char(xx, yy);
        } // if from_len > to_len

        /* Allocate the array storage (from the heap if necessary) */

        if (from_len <= STRLENTHRESHOLD) {
            buffer = store;
        } else {
            buffer = new int[radix];
        }

        /* Here's where the fun begins.  We will find the minimum edit distance
            using dynamic programming.  We only need to store two rows of the matrix
            at a time, since we always progress down the matrix.  For example,
            given the strings "one" and "two", and insert, delete and change costs
            equal to 1:

                _  o  n  e
            _  0  1  2  3
            t  1  1  2  3
            w  2  2  2  3
            o  3  2  3  3

            The dynamic programming recursion is defined as follows:

            ar(x,0) := x * insert_cost
            ar(0,y) := y * delete_cost
            ar(x,y) := min(a(x - 1, y - 1) + (from[x] == to[y] ? 0 : change),
                        a(x - 1, y) + insert_cost,
                        a(x, y - 1) + delete_cost,
                        a(x - 2, y - 2) + (from[x] == to[y-1] &&
                                from[x-1] == to[y] ? swap_cost :
                                infinity))

            Since this only looks at most two rows and three columns back, we need
            only store the values for the two preceeding rows.  In this
            implementation, we do not explicitly store the zero column, so only 2 *
            from_len + 2   words are needed.  However, in the implementation of the
            swap_cost   check, the current matrix value is used as a buffer; we
            can't overwrite the earlier value until the   swap_cost   check has
            been performed.  So we use   2 * from_len + 3   elements in the buffer.
        */

        ///#define ar(x,y,index) (((x) == 0) ? (y) * del : (((y) == 0) ? (x) * ins :
        //    \ buffer[mod(index)]))
        ///#define NW(x,y)	  ar(x, y, index + from_len + 2)
        ///#define N(x,y)	  ar(x, y, index + from_len + 3)
        ///#define W(x,y)	  ar(x, y, index + radix - 1)
        ///#define NNWW(x,y) ar(x, y, index + 1)
        ///#define mod(x) ((x) % radix)


        buffer[index++] = min2(ins + del, (from[0] == to[0] ? 0 : ch));

        low = buffer[mod(index + radix - 1)];
        for (col = 1; col < from_len; col++) {
            buffer[index] = min3(col * del + ((from[col] == to[0]) ? 0 : ch), (col + 1) * del + ins, buffer[index - 1] + del);
            if (buffer[index] < low) {
                low = buffer[index];
            }
            index++;
        }

        /* Now handle the rest of the matrix */
        for (row = 1; row < to_len; row++) {
            for (col = 0; col < from_len; col++) {
                buffer[index] = min3(NW(row, col) + ((from[col] == to[row]) ? 0 : ch), N(row, col + 1) + ins, W(row + 1, col) + del);

                if (from[col] == to[row - 1] && col > 0 && from[col - 1] == to[row]) {
                    buffer[index] = min2(buffer[index], NNWW(row - 1, col - 1) + swap_cost);
                }

                if (buffer[index] < low || col == 0) {
                    low = buffer[index];
                }
                index = mod(index + 1);
            }
            if (low > MIN_DIST) {
                break;
            }
        }

        row = buffer[mod(index + radix - 1)];

        return row;
    } // edit_distn
}

