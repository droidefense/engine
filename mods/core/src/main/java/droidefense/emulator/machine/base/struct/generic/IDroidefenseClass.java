package droidefense.emulator.machine.base.struct.generic;

import droidefense.sdk.helpers.DroidDefenseEnvironment;

import java.util.Hashtable;

/**
 * Created by sergio on 25/3/16.
 */
public abstract class IDroidefenseClass {

    private IDroidefenseClass topParentClass;

    public static String[] getAndroidRClasses() {
        return new String[]{
                ".R$anim",
                ".R$attr",
                ".R$integer",
                ".R$styleable",
                ".R$layout",
                ".R$mipmap",
                ".R$style",
                ".R",
                ".R$string",
                ".R$drawable",
                ".R$color",
                ".R$dimen",
                ".R$id",
                ".BuildConfig",
                ".R$bool"
        };
    }

    public abstract String toString();

    public abstract IDroidefenseMethod getVirtualMethod(final String name, final String descriptor, boolean getRealMethod);

    public abstract IDroidefenseMethod getDirectMethod(final String name, final String descriptor, boolean getRealMethod);

    //GETTERS AND SETTERS

    public abstract IDroidefenseField getStaticField(final String name);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract int getFlag();

    public abstract void setFlag(int flag);

    public abstract boolean isInterface();

    public abstract void setInterface(boolean anInterface);

    public abstract String getSuperClass();

    public abstract void setSuperClass(String superClass);

    public abstract String[] getInterfaces();

    public abstract void setInterfaces(String[] interfaces);

    public abstract IDroidefenseField[] getInstanceFields();

    public abstract void setInstanceFields(IDroidefenseField[] instanceFields);

    public abstract IDroidefenseField[] getStaticFields();

    public abstract void setStaticFields(IDroidefenseField[] staticFields);

    public abstract Hashtable getStaticFieldMap();

    public abstract void setStaticFieldMap(Hashtable staticFieldMap);

    public abstract IDroidefenseMethod[] getDirectMethods();

    public abstract void setDirectMethods(IDroidefenseMethod[] directMethods);

    public abstract IDroidefenseMethod[] getVirtualMethods();

    public abstract void setVirtualMethods(IDroidefenseMethod[] virtualMethods);

    public abstract boolean isBinded();

    public abstract void setBinded(boolean binded);

    public abstract IDroidefenseMethod getMethod(String name, String descriptor, boolean getRealMethod);

    public abstract IDroidefenseMethod[] getMethod(String name);

    public abstract IDroidefenseField getField(String fieldName, String fieldType);

    public abstract boolean isFake();

    public abstract IDroidefenseMethod[] getAllMethods();

    public abstract void addMethod(IDroidefenseMethod methodToCall);

    public String getAndroifiedClassName() {
        String name = getName();
        int idx = name.lastIndexOf("/");
        if (idx != -1)
            name = name.substring(0, idx).replace("/", ".");
        return name;
    }

    public boolean isAnnotationClass() {
        String className = getAndroifiedClassName();
        return className.startsWith("android.annotation");
    }

    public boolean isAndroidv4v7Class() {
        String className = getAndroifiedClassName();
        className = cleanClassName(className);
        return className.startsWith("android.support.v4")
                || className.startsWith("android.support.v7")
                || className.startsWith("android.support.v13")
                || className.startsWith("android.support.v14")
                || className.startsWith("android.support.v17")
                || className.startsWith("android.support.graphics")
                || className.startsWith("android.support.design")
                || className.startsWith("android.support.customtabs")
                || className.startsWith("android.support.annotation");
    }

    public boolean isAndroidRclass() {
        String className = getName();
        return className.endsWith("R$attr")
                || className.endsWith("R")
                || className.endsWith("R$drawable")
                || className.endsWith("R$dimen")
                || className.endsWith("R$integer")
                || className.endsWith("R$mipmap")
                || className.endsWith("R$styleable")
                || className.endsWith("R$id")
                || className.endsWith("R$style")
                || className.endsWith("R$bool")
                || className.endsWith("R$color")
                || className.endsWith("R$anim")
                || className.endsWith("R$string")
                || className.endsWith("R$xml")
                || className.endsWith("R$menu")
                || className.endsWith("R$layout");
    }

    public boolean isAndroidUIRelatedClass() {
        String className = getAndroifiedClassName();
        return className.equals("android.widget.TextView")
                || className.equals("android.app.Activity");
    }

    public String cleanClassName(String name) {
        name = name.replace("/", ".");
        int idx = name.indexOf("$");
        if (idx != -1)
            name = name.substring(0, idx);
        return name;
    }

    public boolean isDeveloperClass() {
        return !isAndroidUIRelatedClass()
                && !isAnnotationClass()
                && !isAndroidRclass()
                && !isAndroidv4v7Class();
    }

    public IDroidefenseMethod findClassInitMethod() {
        //first: check if current class has a method init (constructor method)
        IDroidefenseMethod init = this.getSimpleConstructorMethod();
        if (init == null) {
            //this class does not has a constructor. it is inherited
            //look for it
            String superClassName = this.getSuperClass();
            IDroidefenseClass parentClass = DroidDefenseEnvironment.getInstance().getParentClass(superClassName);
            return parentClass.findClassInitMethod();
        } else {
            //constructor method found in this class. return it
            return init;
        }
    }

    private IDroidefenseMethod getSimpleConstructorMethod() {
        IDroidefenseMethod init = this.getDirectMethod("<clinit>", "()V", true);
        if (init == null) {
            init = this.getDirectMethod("<init>", "()V", true);
        }
        return init;
    }

    public IDroidefenseClass getTopParentClass() {
        return topParentClass;
    }

    public void setTopParentClass(IDroidefenseClass topParentClass) {
        this.topParentClass = topParentClass;
    }
}
