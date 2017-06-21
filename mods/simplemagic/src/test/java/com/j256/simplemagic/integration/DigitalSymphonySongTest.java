package com.j256.simplemagic.integration;

import com.j256.simplemagic.types.BaseMagicTypeTest;
import org.junit.Test;

import java.io.IOException;

/**
 * Run tests from a real magic entry. Mostly from rob stryker via github. Thanks dude.
 *
 * @author robstryker
 */
public class DigitalSymphonySongTest extends BaseMagicTypeTest {

    private static final String DIGITAL_SYMPHONY_SONG_DEFINITION = //
            "0               string  \\x02\\x01\\x13\\x13\\x13\\x01\\x0d\\x10        Digital Symphony sound sample (RISC OS),\n" //
                    + ">8              byte    x       version %d,\n" //
                    + ">9              pstring x       named \"%s\",\n" //
                    + ">(9.b+19)       byte    =0      8-bit logarithmic\n" //
                    + ">(9.b+19)       byte    =1      LZW-compressed linear\n" //
                    + ">(9.b+19)       byte    =2      8-bit linear signed\n"//
                    + ">(9.b+19)       byte    =3      16-bit linear signed\n"//
                    + ">(9.b+19)       byte    =4      SigmaDelta-compressed linear\n"//
                    + ">(9.b+19)       byte    =5      SigmaDelta-compressed logarithmic\n"//
                    + ">(9.b+19)       byte    >5      unknown format";

    /*
     * These test pstring. It prints the name in pstring, and tests a dereferenced byte
     */
    @Test
    public void testDigitalSymphony1() throws IOException {
        // only 5 bytes of the TESTING string
        byte[] objpart = hexToBytes("0201131313010d100505");
        byte[] filler = "TESTING".getBytes();
        byte[] fileToTest = byteArraysCombine(objpart, filler);

        String expectedResults = "Digital Symphony sound sample (RISC OS), version 5, named \"TESTI\",";
        testOutput(DIGITAL_SYMPHONY_SONG_DEFINITION, fileToTest, expectedResults);
    }

    /*
     * Increasing size of pstring increases the string being printed
     */
    @Test
    public void testDigitalSymphony1a() throws IOException {
        // 7 bytes of the TESTING
        byte[] objpart = hexToBytes("0201131313010d100507");
        byte[] filler = "TESTING".getBytes();
        byte[] fileToTest = byteArraysCombine(objpart, filler);
        String expectedResults = "Digital Symphony sound sample (RISC OS), version 5, named \"TESTING\",";
        testOutput(DIGITAL_SYMPHONY_SONG_DEFINITION, fileToTest, expectedResults);
    }

    @Test
    public void testDigitalSymphony2() throws IOException {
        // magic key 7-bytes |vers | pstring | 9 byte unknown
        String hex = "0201131313010d10" + "05" + "027272" + "aaaaaaaaaaaaaaaaaa" + "04";
        byte[] fileToTest = hexToBytes(hex);
        String expectedResults =
                "Digital Symphony sound sample (RISC OS), version 5, named \"rr\", SigmaDelta-compressed linear";
        testOutput(DIGITAL_SYMPHONY_SONG_DEFINITION, fileToTest, expectedResults);
    }

    @Test
    public void testDigitalSymphony3() throws IOException {
        // magic key 7-bytes |vers | pstring | 9 byte unknown
        String hex = "0201131313010d10" + "05" + "0C726f62206973206772656174" + "aaaaaaaaaaaaaaaaaa" + "04";
        byte[] fileToTest = hexToBytes(hex);
        String expectedResults = "Digital Symphony sound sample (RISC OS), version 5, "
                + "named \"rob is great\", SigmaDelta-compressed linear";
        testOutput(DIGITAL_SYMPHONY_SONG_DEFINITION, fileToTest, expectedResults);
    }

    /*
     * Tests greater-than comparison
     */
    @Test
    public void testDigitalSymphony4() throws IOException {
        // magic key 7-bytes |vers | pstring | 9 byte unknown
        String hex = "0201131313010d10" + "05" + "0C726f62206973206772656174" + "aaaaaaaaaaaaaaaaaa" + "07";
        byte[] fileToTest = hexToBytes(hex);
        String expectedResults =
                "Digital Symphony sound sample (RISC OS), version 5, named \"rob is great\", unknown format";
        testOutput(DIGITAL_SYMPHONY_SONG_DEFINITION, fileToTest, expectedResults);
    }
}
