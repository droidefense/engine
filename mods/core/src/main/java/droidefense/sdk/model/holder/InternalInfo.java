package droidefense.sdk.model.holder;

import droidefense.om.machine.base.struct.generic.IAtomClass;
import droidefense.om.machine.base.struct.model.SharedPool;
import droidefense.om.machine.reader.DexClassReader;
import droidefense.sdk.model.manifest.base.AbstractManifestClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by sergio on 18/2/16.
 */
public class InternalInfo implements Serializable {

    //entry points
    private ArrayList<AbstractManifestClass> entryPoints;
    private transient IAtomClass[] dynamicEntryPoints;
    private SharedPool dexContentList;

    public InternalInfo() {
        //init data structures
        entryPoints = new ArrayList<>();
    }

    public void addClass(String name, IAtomClass newClass) {
        this.dexContentList.addClass(name, newClass);
    }


    public IAtomClass[] getAllClasses() {
        Collection<IAtomClass> data = this.dexContentList.getClasses().values();
        return data.toArray(new IAtomClass[data.size()]);
    }

    public boolean hasDexClass(String name) {
        return this.dexContentList.getClasses().containsKey(name);
    }

    public IAtomClass getDexClass(String name) {
        return this.dexContentList.getClasses().get(name);
    }

    public void addDexInfo(DexClassReader dexClassReader) {
        /*
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
        */
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

    public ArrayList<AbstractManifestClass> getEntryPoints() {
        return entryPoints;
    }

    public void setEntryPoints(ArrayList<AbstractManifestClass> entryArray) {
        this.entryPoints = entryArray;
    }

    public void setPool(SharedPool pool) {
        this.dexContentList = pool;
    }

    public SharedPool getPool() {
        return dexContentList;
    }
}
