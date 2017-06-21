package com.j256.simplemagic.integration;

import com.j256.simplemagic.types.BaseMagicTypeTest;
import org.junit.Test;

import java.io.IOException;

/**
 * Run tests from a sample magic snippets. Mostly from rob stryker via github. Thanks dude.
 *
 * @author robstryker
 */
public class RiscOsTest extends BaseMagicTypeTest {

    @Test
    public void testRiscOSChunk() throws IOException {
        String magicFile = "0   lelong   0xc3cbc6c5   RISC OS Chunk data";
        byte[] fileToTest = hexToBytes("C5C6CbC3");

        String expectedResults = "RISC OS Chunk data";
        testOutput(magicFile, fileToTest, expectedResults);
    }

    @Test
    public void testRiscOSChunkAOF() throws IOException {
        String magicFile = "0   lelong   0xc3cbc6c5   RISC OS Chunk data\n" //
                + ">12 string OBJ_ \\b, AOF object";

        byte[] hex = hexToBytes("C5C6CbC3");
        byte[] objpart = "OBJ_\0aaaaaaa".getBytes();
        byte[] fileToTest = byteArraysCombine(hex, hex, hex, objpart);

        String expectedResults = "RISC OS Chunk data, AOF object";
        testOutput(magicFile, fileToTest, expectedResults);
    }

    @Test
    public void testRiscOSChunkALF() throws IOException {
        String magicFile = "0   lelong   0xc3cbc6c5   RISC OS Chunk data\n" //
                + ">12 string OBJ_ \\b, AOF object\n" //
                + ">12 string LIB_ \\b, ALF object";

        byte[] hex = hexToBytes("C5C6CbC3");
        byte[] objpart = "LIB_\0aaaaaaa".getBytes();
        byte[] fileToTest = byteArraysCombine(hex, hex, hex, objpart);

        String expectedResults = "RISC OS Chunk data, ALF object";
        testOutput(magicFile, fileToTest, expectedResults);
    }

    @Test
    public void testRiscOSCAIFExec() throws IOException {
        String magicFile = "16   lelong   0xef000011   RISC OS AIF executable";

        byte[] filler = hexToBytes("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        byte[] objpart = hexToBytes("110000EF");
        byte[] fileToTest = byteArraysCombine(filler, objpart);

        String expectedResults = "RISC OS AIF executable";
        testOutput(magicFile, fileToTest, expectedResults);
    }

    @Test
    public void testRiscOSDraw() throws IOException {
        String magicFile = "0   string   Draw   RISC OS Draw file data";
        byte[] fileToTest = "Draw_____________".getBytes();

        String expectedResults = "RISC OS Draw file data";
        testOutput(magicFile, fileToTest, expectedResults);
    }

    @Test
    public void testRiscOSFont0() throws IOException {
        String magicFile = "0 string FONT\\0 RISC OS outline font data,\n" //
                + ">5 byte x version %d";
        byte[] filler = "FONT".getBytes();
        byte[] objpart = hexToBytes("0006");
        byte[] fileToTest = byteArraysCombine(filler, objpart);

        String expectedResults = "RISC OS outline font data, version 6";
        testOutput(magicFile, fileToTest, expectedResults);
    }

    @Test
    public void testRiscOSFont1FailsFont4() throws IOException {
        String magicFile = "0 string FONT\\1 RISC OS 1bpp font data,\n" //
                + ">5 byte x version %d";
        byte[] filler = "FONT".getBytes();
        byte[] objpart = hexToBytes("04");
        byte[] fileToTest = byteArraysCombine(filler, objpart);

        testOutput(magicFile, fileToTest, null);
    }

    @Test
    public void testRiscOSMusicFile() throws IOException {
        String magicFile = "0 string Maestro\\r RISC OS music file\n" //
                + ">8 byte x version %d\n" //
                + ">8 byte x type %d";

        byte[] filler = "Maestro\r".getBytes();
        byte[] objpart = hexToBytes("04aaaaaaaa");
        byte[] fileToTest = byteArraysCombine(filler, objpart);

        String expectedResults = "RISC OS music file version 4 type 4";
        testOutput(magicFile, fileToTest, expectedResults);
    }
}
