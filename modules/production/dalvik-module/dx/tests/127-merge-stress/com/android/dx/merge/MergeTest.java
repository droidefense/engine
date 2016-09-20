package com.android.dx.merge;

import com.android.dex.Dex;
import com.android.dex.DexIndexOverflowException;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

/**
 * This test tries to merge given dex files at random, a first pass at 2 by 2, followed by
 * a second pass doing multi-way merges.
 */
public class MergeTest {

  private static final int NUMBER_OF_TRIES = 1000;

  public static void main(String[] args) throws Throwable {

    Random random = new Random();
    for (int pass = 0; pass < 2; pass++) {
      for (int i = 0; i < NUMBER_OF_TRIES; i++) {
        // On the first pass only do 2-way merges, then do from 3 to 10 way merges
        // but not more to avoid dex index overflow.
        int numDex = pass == 0 ? 2 : random.nextInt(8) + 3;

        String[] fileNames = new String[numDex]; // only for the error message
        try {
          Dex[] dexesToMerge = new Dex[numDex];
          for (int j = 0; j < numDex; j++) {
            String fileName = args[random.nextInt(args.length)];
            fileNames[j] = fileName;
            dexesToMerge[j] = new Dex(new File(fileName));
          }
          new DexMerger(dexesToMerge, CollisionPolicy.KEEP_FIRST).merge();
        } catch (DexIndexOverflowException e) {
          // ignore index overflow
        } catch (Throwable t) {
          System.err.println(
                  "Problem merging those dexes: " + Arrays.toString(fileNames));
          throw t;
        }
      }
    }
  }
}
