package droidefense.sdk.model.holder;

import droidefense.sdk.model.dex.DexContent;
import droidefense.sdk.model.manifest.base.AbstractManifestClass;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sergio on 18/2/16.
 */
public class InternalInfo implements Serializable {


    private final ArrayList<DexContent> dexContentList;

    //entry points
    private transient ArrayList<AbstractManifestClass> entryPoints;
    //private transient IAtomClass[] dynamicEntryPoints;

    public InternalInfo() {
        //init data structures
        //dexClasses = new Hashtable<>();
        entryPoints = new ArrayList<>();
        dexContentList = new ArrayList<>();
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
}
