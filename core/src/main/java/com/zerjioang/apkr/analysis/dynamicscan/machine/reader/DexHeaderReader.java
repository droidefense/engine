package com.zerjioang.apkr.analysis.dynamicscan.machine.reader;

import com.zerjioang.apkr.sdk.helpers.Util;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;
import com.zerjioang.apkr.sdk.model.base.ApkrProject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by sergio on 26/2/16.
 */
public class DexHeaderReader implements Serializable {

    private static final byte[] MAGIC_NUMBER = {0x64, 0x65, 0x78, 0x0a, 0x30, 0x33, 0x35, 0x00}; //"dex\n035\0"
    private static final int SHA1_BYTES = 20;
    private static final int UINT = 4;
    private static final int MAGIC_NUMBER_BYTES = 8;
    private static final byte DEFAULT_HEADER_SIZE = 0x70;
    private transient ApkrProject currentProject;
    /**
     * Dex file byte content
     */
    private byte[] dexFileContent;
    private DexOperator operator;

    /**
     * Dalvik header related values
     */
    private byte[] magic;
    private byte[] checksum;
    private byte[] hash;
    private byte[] fileSize;
    private byte[] headerSize;
    private byte[] endian_tag;
    private byte[] link_size;
    private byte[] link_offset;
    private byte[] map_offset;
    private byte[] string_ids;
    private byte[] string_table;
    private byte[] type_ids;
    private byte[] type_table;
    private byte[] proto_ids;
    private byte[] proto_table;
    private byte[] field_ids;
    private byte[] field_offset;
    private byte[] methods_ids;
    private byte[] methods_offset;
    private byte[] class_def;
    private byte[] class_offset;
    private byte[] data_size;
    private byte[] data_offset;

    public DexHeaderReader(File dexFile, ApkrProject currentProject) {
        this.currentProject = currentProject;

        if (dexFile.exists() && dexFile.isFile() && dexFile.canRead()) {
            try {
                this.dexFileContent = Files.readAllBytes(Paths.get(dexFile.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Dex file reader cannot access to requested resource. Please make sure, resource exist and can be read");
        }

        //init vars
        this.operator = new DexOperator(this.dexFileContent);
    }

    public DexHeaderReader(byte[] dexFileContent, ApkrProject currentProject) {
        this.dexFileContent = dexFileContent;
        this.currentProject = currentProject;
        //init vars
        this.operator = new DexOperator(this.dexFileContent);
    }

    public void loadClasses(ApkrFile dex) {
        if (!currentProject.isHeaderReaded()) {
            try {
                readHeader();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            readData();
            currentProject.setHeaderReaded(true);
            currentProject.setDexHeaderReader(this);
        }
    }

    private void readData() {
        //read strings
        int totalStrings = operator.toInt(string_ids);
        //dexStrings = new DexString[totalStrings];
        int counter = 0;
        int currentOffset = operator.toInt(string_table);
        while (counter < totalStrings) {
            byte[] strData = operator.readRangeUntil(currentOffset, (byte) 0x0);
            counter++;
            currentOffset += strData.length;
        }
    }

    private void readHeader() throws IllegalArgumentException {
        //read magic number
        magic = operator.readRange(0x00, MAGIC_NUMBER_BYTES);
        operator.checkData("Magic Number", magic, MAGIC_NUMBER);
        currentProject.setMagicNumber(new String(magic));

        //read file checksum
        checksum = operator.readRange(0x08, UINT);
        operator.checkData("Header Adler32", checksum, operator.calculateDexChecksum());
        currentProject.setHeaderChecksum(Util.toHexString(operator.getAdler()));

        //read file sha1 signature
        hash = operator.readRange(0x0C, SHA1_BYTES);
        operator.checkData("File signature", hash, operator.calculateSignature());
        currentProject.setFileSignature(operator.getSha1());

        //read file size
        fileSize = operator.readRange(0x20, UINT);
        operator.checkData("File size", operator.toInt(fileSize, DexOperator.LITTLE_ENDIAN), dexFileContent.length);
        currentProject.setHeaderFileSize(operator.toInt(fileSize, DexOperator.LITTLE_ENDIAN));

        //read header size
        headerSize = operator.readRange(0x24, UINT);
        operator.checkData("Header size", operator.toInt(headerSize, DexOperator.LITTLE_ENDIAN), operator.toInt(new byte[]{DEFAULT_HEADER_SIZE}, DexOperator.LITTLE_ENDIAN));
        currentProject.setHeaderSize(Integer.toHexString(operator.toInt(headerSize, DexOperator.LITTLE_ENDIAN)));

        //read endian tag
        endian_tag = operator.readRange(0x28, UINT);
        operator.checkData("Endian tag", operator.checkEndianTag(endian_tag));
        currentProject.setEndianTag(endian_tag);
        currentProject.setEndianString(operator.getEndianness(endian_tag));

        //read link size
        link_size = operator.readRange(0x2C, UINT);
        currentProject.setLinkSize(link_size);

        //read link offset
        link_offset = operator.readRange(0x30, UINT);
        currentProject.setLinkOffset(link_offset);

        //check link content
        //operator.checkContent(link_size, link_offset);

        //read map offset
        map_offset = operator.readRange(0x34, UINT);

        //read string ids
        string_ids = operator.readRange(0x38, UINT);

        //read string table
        string_table = operator.readRange(0x3C, UINT);

        //read type ids
        type_ids = operator.readRange(0x40, UINT);

        //read type table
        type_table = operator.readRange(0x44, UINT);

        //read proto ids
        proto_ids = operator.readRange(0x48, UINT);

        //read proto table
        proto_table = operator.readRange(0x4C, UINT);

        //read  fields ids
        field_ids = operator.readRange(0x50, UINT);

        //read fields offset
        field_offset = operator.readRange(0x54, UINT);

        //read methods ids
        methods_ids = operator.readRange(0x58, UINT);

        //read methods offset
        methods_offset = operator.readRange(0x5C, UINT);

        //read class def
        class_def = operator.readRange(0x60, UINT);

        //read class offset
        class_offset = operator.readRange(0x64, UINT);

        //read data size
        data_size = operator.readRange(0x68, UINT);

        //read data offset
        data_offset = operator.readRange(0x6C, UINT);
    }
}
