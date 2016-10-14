package droidefense.sdk.model.dex;

import java.io.Serializable;

public class DexContent implements Serializable {

    //base dex info
    private String[] dexDescriptors;
    private String[] dexStrings;
    private String[] dexTypes;
    private String[] dexMethodNames;
    private String[] dexMethodTypes;
    private String[] dexMethodClasses;
    private String[] dexFieldNames;
    private String[] dexFieldTypes;
    private String[] dexFieldClasses;
    private String magicNumber;
    private String headerChecksum;
    private String fileSignature;
    private int headerFileSize;
    private String headerSize;
    private byte[] endianTag;
    private String endianString;
    private byte[] linkSize;
    private byte[] linkOffset;

    public DexContent() {
    }

    public String[] getDexDescriptors() {
        return dexDescriptors;
    }

    public void setDexDescriptors(String[] dexDescriptors) {
        this.dexDescriptors = dexDescriptors;
    }

    public String[] getDexStrings() {
        return dexStrings;
    }

    public void setDexStrings(String[] dexStrings) {
        this.dexStrings = dexStrings;
    }

    public String[] getDexTypes() {
        return dexTypes;
    }

    public void setDexTypes(String[] dexTypes) {
        this.dexTypes = dexTypes;
    }

    public String[] getDexMethodNames() {
        return dexMethodNames;
    }

    public void setDexMethodNames(String[] dexMethodNames) {
        this.dexMethodNames = dexMethodNames;
    }

    public String[] getDexMethodTypes() {
        return dexMethodTypes;
    }

    public void setDexMethodTypes(String[] dexMethodTypes) {
        this.dexMethodTypes = dexMethodTypes;
    }

    public String[] getDexMethodClasses() {
        return dexMethodClasses;
    }

    public void setDexMethodClasses(String[] dexMethodClasses) {
        this.dexMethodClasses = dexMethodClasses;
    }

    public String[] getDexFieldNames() {
        return dexFieldNames;
    }

    public void setDexFieldNames(String[] dexFieldNames) {
        this.dexFieldNames = dexFieldNames;
    }

    public String[] getDexFieldTypes() {
        return dexFieldTypes;
    }

    public void setDexFieldTypes(String[] dexFieldTypes) {
        this.dexFieldTypes = dexFieldTypes;
    }

    public String[] getDexFieldClasses() {
        return dexFieldClasses;
    }

    public void setDexFieldClasses(String[] dexFieldClasses) {
        this.dexFieldClasses = dexFieldClasses;
    }

    public String getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(String magicNumber) {
        this.magicNumber = magicNumber;
    }

    public String getHeaderChecksum() {
        return headerChecksum;
    }

    public void setHeaderChecksum(String headerChecksum) {
        this.headerChecksum = headerChecksum;
    }

    public String getFileSignature() {
        return fileSignature;
    }

    public void setFileSignature(String fileSignature) {
        this.fileSignature = fileSignature;
    }

    public int getHeaderFileSize() {
        return headerFileSize;
    }

    public void setHeaderFileSize(int headerFileSize) {
        this.headerFileSize = headerFileSize;
    }

    public String getHeaderSize() {
        return headerSize;
    }

    public void setHeaderSize(String headerSize) {
        this.headerSize = headerSize;
    }

    public byte[] getEndianTag() {
        return endianTag;
    }

    public void setEndianTag(byte[] endianTag) {
        this.endianTag = endianTag;
    }

    public String getEndianString() {
        return endianString;
    }

    public void setEndianString(String endianString) {
        this.endianString = endianString;
    }

    public byte[] getLinkSize() {
        return linkSize;
    }

    public void setLinkSize(byte[] linkSize) {
        this.linkSize = linkSize;
    }

    public byte[] getLinkOffset() {
        return linkOffset;
    }

    public void setLinkOffset(byte[] linkOffset) {
        this.linkOffset = linkOffset;
    }
}