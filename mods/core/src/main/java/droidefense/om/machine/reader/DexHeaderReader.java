package droidefense.om.machine.reader;

import com.droidefense.log4j.Log;
import com.droidefense.log4j.LoggerType;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.dex.DalvikDexModel;
import droidefense.sdk.model.io.DexHashedFile;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

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
    private transient ArrayList<DexHashedFile> dexFileList;
    private transient ArrayList<DexOperator> operator;
    private transient ArrayList<DalvikDexModel> dexModel;

    public DexHeaderReader(DroidefenseProject currentProject) {
        this.dexFileList = currentProject.getDexList();
        this.currentProject = currentProject;

        //init vars
        this.dexModel = new ArrayList<>();
        this.operator = new ArrayList<>();

        //populate dexmodel and operator
        for (DexHashedFile dex : dexFileList) {
            this.dexModel.add(new DalvikDexModel());
            try {
                this.operator.add(new DexOperator(dex.getContent()));
            } catch (IOException e) {
                Log.write(LoggerType.ERROR, "Could not read byte content of dex file " + dex.getAbsolutePath());
                Log.write(LoggerType.ERROR, "Error details: " + e.getLocalizedMessage());
            }
        }
    }

    public void readAllDexAvailable() {
        currentProject.setDexHeaderReader(this);
        for (int i = 0; i < this.dexFileList.size(); i++) {
            DalvikDexModel model = this.dexModel.get(i);
            DexOperator op = this.operator.get(i);
            DexHashedFile dex = this.dexFileList.get(i);
            readSingleDexFile(model, op, dex);
        }
        currentProject.setAllDexHeadersReaded(true);
    }

    private void readSingleDexFile(DalvikDexModel model, DexOperator op, DexHashedFile dex) {
        if (!dex.isHeaderReaded()) {
            try {
                readHeader(model, op);
                dex.setDexHeaderReaded(true);
                readData(model, op);
            } catch (IllegalArgumentException e) {
                Log.write(LoggerType.ERROR, "Could not read header of dex file " + dex.getAbsolutePath());
                Log.write(LoggerType.ERROR, "Error details: " + e.getLocalizedMessage());
            }
        }
    }

    private void readData(DalvikDexModel dexModel, DexOperator operator) {

        Log.write(LoggerType.DEBUG, "Reading DEX file data...");

        //TODO implement a secondary .dex file content reader
    }

    private void readHeader(DalvikDexModel dexModel, DexOperator operator) throws IllegalArgumentException {

        Log.write(LoggerType.DEBUG, "Reading DEX file header...");

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
        operator.checkData("File size", operator.toInt(dexModel.getFileSize(), DexOperator.LITTLE_ENDIAN), operator.getFileSize());

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

    public DalvikDexModel getDexModel(int index) {
        if (dexModel != null && index < dexModel.size() && index >= 0)
            return dexModel.get(index);
        return null;
    }
}
