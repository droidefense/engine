package droidefense.om.machine.reader;

import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.dex.DalvikDexModel;
import droidefense.sdk.model.io.AbstractHashedFile;

import java.io.Serializable;

/**
 * Created by sergio on 26/2/16.
 */
public class DexHeaderReader implements Serializable {

    private static final byte[] MAGIC_NUMBER = {0x64, 0x65, 0x78, 0x0a, 0x30, 0x33, 0x35, 0x00}; //"dex\n035\0"
    private static final int SHA1_BYTES = 20;
    private static final int UINT = 4;
    private static final int MAGIC_NUMBER_BYTES = 8;
    private static final byte DEFAULT_HEADER_SIZE = 0x70;
    private transient DroidefenseProject currentProject;
    /**
     * Dex file byte content
     */
    private byte[] dexFileContent;
    private DexOperator operator;
    private DalvikDexModel dexModel;

    public DexHeaderReader(byte[] dexFileContent, DroidefenseProject currentProject) {
        this.dexFileContent = dexFileContent;
        this.currentProject = currentProject;
        //init vars
        this.dexModel = new DalvikDexModel();
        this.operator = new DexOperator(this.dexFileContent);
    }

    public void loadClasses(AbstractHashedFile dex) {
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
        int totalStrings = operator.toInt(dexModel.getString_ids());
        //dexStrings = new DexString[totalStrings];
        int counter = 0;
        int currentOffset = operator.toInt(dexModel.getString_table());
        while (counter < totalStrings) {
            byte[] strData = operator.readRangeUntil(currentOffset, (byte) 0x0);
            counter++;
            currentOffset += strData.length;
        }
    }

    private void readHeader() throws IllegalArgumentException {
        //read magic number
        dexModel.setMagic(operator.readRange(0x00, MAGIC_NUMBER_BYTES));
        operator.checkData("Magic Number", dexModel.getMagic(), MAGIC_NUMBER);

        //read file checksum
        dexModel.setChecksum(operator.readRange(0x08, UINT));
        operator.checkData("Header Adler32", dexModel.getChecksum(), operator.calculateDexChecksum());

        //read file sha1 signature
        dexModel.setHash(operator.readRange(0x0C, SHA1_BYTES));
        operator.checkData("File signature", dexModel.getHash(), operator.calculateSignature());

        //read file size
        dexModel.setFileSize(operator.readRange(0x20, UINT));
        operator.checkData("File size", operator.toInt(dexModel.getFileSize(), DexOperator.LITTLE_ENDIAN), dexFileContent.length);

        //read header size
        dexModel.setHeaderSize(operator.readRange(0x24, UINT));
        operator.checkData("Header size", operator.toInt(dexModel.getHeaderSize(), DexOperator.LITTLE_ENDIAN), operator.toInt(new byte[]{DEFAULT_HEADER_SIZE}, DexOperator.LITTLE_ENDIAN));

        //read endian tag
        dexModel.setEndian_tag(operator.readRange(0x28, UINT));
        operator.checkData("Endian tag", operator.checkEndianTag(dexModel.getEndian_tag()));
        dexModel.setEndianString(operator.getEndianness(dexModel.getEndian_tag()));

        //read link size
        dexModel.setLink_size(operator.readRange(0x2C, UINT));

        //read link offset
        dexModel.setLink_offset(operator.readRange(0x30, UINT));

        //check link content
        //operator.checkContent(link_size, link_offset);

        //read map offset
        dexModel.setMap_offset(operator.readRange(0x34, UINT));

        //read string ids
        dexModel.setString_ids(operator.readRange(0x38, UINT));

        //read string table
        dexModel.setString_table(operator.readRange(0x3C, UINT));

        //read type ids
        dexModel.setType_ids(operator.readRange(0x40, UINT));

        //read type table
        dexModel.setType_table(operator.readRange(0x44, UINT));

        //read proto ids
        dexModel.setProto_ids(operator.readRange(0x48, UINT));

        //read proto table
        dexModel.setProto_table(operator.readRange(0x4C, UINT));

        //read  fields ids
        dexModel.setField_ids(operator.readRange(0x50, UINT));

        //read fields offset
        dexModel.setField_offset(operator.readRange(0x54, UINT));

        //read methods ids
        dexModel.setMethods_ids(operator.readRange(0x58, UINT));

        //read methods offset
        dexModel.setMethods_offset(operator.readRange(0x5C, UINT));

        //read class def
        dexModel.setClass_def(operator.readRange(0x60, UINT));

        //read class offset
        dexModel.setClass_offset(operator.readRange(0x64, UINT));

        //read data size
        dexModel.setData_size(operator.readRange(0x68, UINT));

        //read data offset
        dexModel.setData_offset(operator.readRange(0x6C, UINT));
    }

    public DalvikDexModel getDexModel() {
        return dexModel;
    }
}
