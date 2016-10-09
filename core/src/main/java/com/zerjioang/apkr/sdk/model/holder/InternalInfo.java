package com.zerjioang.apkr.sdk.model.holder;

import apkr.external.module.datamodel.manifest.base.AbstractManifestClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by sergio on 18/2/16.
 */
public class InternalInfo implements Serializable {

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

    //private transient Map<String, IAtomClass> dexClasses;

    //entry points
    private transient ArrayList<AbstractManifestClass> entryPoints;
    //private transient IAtomClass[] dynamicEntryPoints;

    public InternalInfo() {
        //init data structures
        //dexClasses = new Hashtable<>();
        entryPoints = new ArrayList<>();
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

    public void printDexInfo() {

    }

    public String[] getDexFieldClasses() {
        return dexFieldClasses;
    }

    public void setDexFieldClasses(String[] dexFieldClasses) {
        this.dexFieldClasses = dexFieldClasses;
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

    //TOSTRING

    @Override
    public String toString() {
        return "InternalInfo{" +
                ", dexDescriptors=" + Arrays.toString(dexDescriptors) +
                ", dexStrings=" + Arrays.toString(dexStrings) +
                ", dexTypes=" + Arrays.toString(dexTypes) +
                ", dexMethodNames=" + Arrays.toString(dexMethodNames) +
                ", dexMethodTypes=" + Arrays.toString(dexMethodTypes) +
                ", dexMethodClasses=" + Arrays.toString(dexMethodClasses) +
                ", dexFieldNames=" + Arrays.toString(dexFieldNames) +
                ", dexFieldTypes=" + Arrays.toString(dexFieldTypes) +
                ", dexFieldClasses=" + Arrays.toString(dexFieldClasses) +
                '}';
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

    public void setHeaderFileSize(int headerFileSie) {
        this.headerFileSize = headerFileSie;
    }

    public String getHeaderSize() {
        return headerSize;
    }

    public void setHeaderSize(String headerSize) {
        this.headerSize = "0x" + headerSize;
    }

    public byte[] getEndianTag() {
        return endianTag;
    }

    public void setEndianTag(byte[] endianTag) {
        this.endianTag = endianTag;
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

    /*
    public void addClass(String name, IAtomClass newClass) {
        this.dexClasses.put(name, newClass);
    }


    public IAtomClass[] getListClasses() {
        Collection<IAtomClass> data = this.dexClasses.values();
        return data.toArray(new IAtomClass[data.size()]);
    }

    public boolean hasDexClass(String name) {
        return dexClasses.containsKey(name);
    }

    public IAtomClass getDexClass(String name) {
        return dexClasses.get(name);
    }

    public void addDexInfo(DexClassReader dexClassReader) {
        this.dexStrings = removeRepeatedStrings(dexClassReader.getStrings());
        this.dexTypes = removeRepeatedStrings(dexClassReader.getTypes());
        this.dexDescriptors = removeRepeatedStrings(dexClassReader.getDescriptors());
        this.dexFieldClasses = removeRepeatedStrings(dexClassReader.getFieldClasses());
        this.dexFieldTypes = removeRepeatedStrings(dexClassReader.getFieldTypes());
        this.dexFieldNames = removeRepeatedStrings(dexClassReader.getFieldNames());
        this.dexMethodClasses = removeRepeatedStrings(dexClassReader.getMethodClasses());
        this.dexMethodTypes = removeRepeatedStrings(dexClassReader.getMethodTypes());
        this.dexMethodNames = removeRepeatedStrings(dexClassReader.getMethodNames());
        this.dexClasses = dexClassReader.getClasses();
    }

    private String[] removeRepeatedStrings(String[] source) {
        String[] data = new HashSet<>(Arrays.asList(source)).toArray(new String[0]);
        Arrays.sort(data);
        return data;
    }

    public IAtomClass[] getDynamicEntryPoints() {
        return dynamicEntryPoints;
    }

    public void setDynamicEntryPoints(IAtomClass[] dynamicEntryPoints) {
        this.dynamicEntryPoints = dynamicEntryPoints;
    }

    */
    public ArrayList<AbstractManifestClass> getEntryPoints() {
        return entryPoints;
    }

    public void setEntryPoints(ArrayList<AbstractManifestClass> entryArray) {
        this.entryPoints = entryArray;
    }

    public String getEndianString() {
        return endianString;
    }

    public void setEndianString(String endianString) {
        this.endianString = endianString;
    }
}
