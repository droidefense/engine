package droidefense.sdk.model.holder;

import droidefense.om.machine.base.struct.generic.IAtomClass;
import droidefense.om.machine.base.struct.model.SharedPool;
import droidefense.om.machine.reader.DexClassReader;
import droidefense.sdk.manifest.base.AbstractManifestClass;

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

    public void cleanup() {
        if(this.dexContentList != null){
            this.dexContentList.cleanup();
        }
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

    public String[] getStrings() {
        return this.getPool().getStrings();
    }
}
