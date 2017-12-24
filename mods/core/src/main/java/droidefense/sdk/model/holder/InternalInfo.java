package droidefense.sdk.model.holder;

import droidefense.om.machine.base.struct.generic.IDroidefenseClass;
import droidefense.om.machine.base.struct.model.SharedPool;
import droidefense.sdk.manifest.base.AbstractManifestClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by sergio on 18/2/16.
 */
public class InternalInfo implements Serializable {

    //entry points
    private ArrayList<AbstractManifestClass> entryPoints;
    private transient IDroidefenseClass[] dynamicEntryPoints;
    private SharedPool dexContentList;

    public InternalInfo() {
        //init data structures
        entryPoints = new ArrayList<>();
    }

    public void addClass(String name, IDroidefenseClass newClass) {
        this.dexContentList.addClass(name, newClass);
    }


    public IDroidefenseClass[] getAllClasses() {
        Collection<IDroidefenseClass> data = this.dexContentList.getClasses().values();
        return data.toArray(new IDroidefenseClass[data.size()]);
    }

    public boolean hasDexClass(String name) {
        return this.dexContentList.getClasses().containsKey(name);
    }

    public IDroidefenseClass getDexClass(String name) {
        return this.dexContentList.getClasses().get(name);
    }

    public void cleanup() {
        if (this.dexContentList != null) {
            this.dexContentList.cleanup();
        }
    }

    public IDroidefenseClass[] getDynamicEntryPoints() {
        return dynamicEntryPoints;
    }

    public void setDynamicEntryPoints(IDroidefenseClass[] dynamicEntryPoints) {
        this.dynamicEntryPoints = dynamicEntryPoints;
    }

    public ArrayList<AbstractManifestClass> getEntryPoints() {
        return entryPoints;
    }

    public void setEntryPoints(ArrayList<AbstractManifestClass> entryArray) {
        this.entryPoints = entryArray;
    }

    public SharedPool getPool() {
        return dexContentList;
    }

    public void setPool(SharedPool pool) {
        this.dexContentList = pool;
    }

    public String[] getStrings() {
        return this.getPool().getStrings();
    }
}
