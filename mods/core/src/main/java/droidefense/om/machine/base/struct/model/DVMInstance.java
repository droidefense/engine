package droidefense.om.machine.base.struct.model;


import droidefense.om.machine.base.struct.generic.IDroidefenseClass;
import droidefense.om.machine.base.struct.generic.IAtomField;
import droidefense.om.machine.base.struct.generic.IAtomInstance;
import droidefense.om.machine.reader.DexClassReader;

import java.io.Serializable;
import java.util.Hashtable;

public final class DVMInstance implements IAtomInstance, Serializable {

    private final Hashtable fieldsOfClasses = new Hashtable();

    private final IDroidefenseClass ownerClass;
    private Object parentInstance;

    public DVMInstance(final IDroidefenseClass cls) {
        this.ownerClass = cls;

        IDroidefenseClass current = cls;
        do {
            Hashtable fields = new Hashtable();
            IAtomField[] currentFields = current.getInstanceFields();
            if (currentFields != null) {
                for (IAtomField field : currentFields) {
                    fields.put(field.getName(), field.copy());
                }
                fieldsOfClasses.put(current.getName(), fields);
            }
            current = DexClassReader.getInstance().load(current.getSuperClass());
            //stop condition: class is fake or class represents a reflected java object class with no parent
            if (current.isFake() || current.getSuperClass() == null)
                break;
        } while (current != null);
    }

    public String toString() {
        return ownerClass.getName() + "@" + Integer.toHexString(hashCode());
    }

    public IAtomField getField(final String className, final String fieldName) {
        String currentClassName = className;
        while (true) {
            Hashtable fields = (Hashtable) fieldsOfClasses.get(currentClassName);
            if (fields == null) {
                return null;
            }
            IAtomField field = (IAtomField) fields.get(fieldName);
            if (field != null) {
                return field;
            }
            IDroidefenseClass currentClazz = DexClassReader.getInstance().load(currentClassName);
            if (currentClazz == null) {
                return null;
            }
            currentClassName = currentClazz.getSuperClass();
        }
    }

    //GETTERS AND SETTERS


    public IDroidefenseClass getOwnerClass() {
        return ownerClass;
    }

    public Hashtable getFieldsOfClasses() {
        return fieldsOfClasses;
    }

    public Object getParentInstance() {
        return parentInstance;
    }

    public void setParentInstance(Object parentInstance) {
        this.parentInstance = parentInstance;
    }
}
