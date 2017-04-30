package droidefense.xmodel.base;

import droidefense.xmodel.manifest.base.AbstractManifestClass;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by sergio on 26/4/16.
 */
public enum DataType implements Serializable {

    BOOLEAN {
        @Override
        public boolean parseData(String data, Class cls, AbstractManifestClass readedClass, String methodName) {
            boolean value = data.equals("true");
            try {
                this.invokeManifestMethod(cls, boolean.class, readedClass, value, methodName);
                return true;
            } catch (NoSuchMethodException | IllegalAccessException e) {
                System.err.println(e.getLocalizedMessage());
            } catch (InvocationTargetException e) {
                System.err.println(e.getLocalizedMessage());
            }
            return false;
        }
    }, STRING {
        @Override
        public boolean parseData(String data, Class cls, AbstractManifestClass readedClass, String methodName) {
            try {
                this.invokeManifestMethod(cls, String.class, readedClass, data, methodName);
                return true;
            } catch (NoSuchMethodException | InvocationTargetException e) {
                System.err.println(e.getLocalizedMessage());
            } catch (IllegalAccessException e) {
                System.err.println(e.getLocalizedMessage());
            }
            return false;
        }
    }, INT {
        @Override
        public boolean parseData(String data, Class cls, AbstractManifestClass readedClass, String methodName) {
            try {
                int i = Integer.parseInt(data);
                this.invokeManifestMethod(cls, int.class, readedClass, i, methodName);
                return true;
            } catch (NoSuchMethodException e) {
                System.err.println(e.getLocalizedMessage());
            } catch (IllegalAccessException e) {
                System.err.println(e.getLocalizedMessage());
            } catch (InvocationTargetException e) {
                System.err.println(e.getLocalizedMessage());
            }
            return false;
        }
    }, ENUM {
        @Override
        public boolean parseData(String data, Class cls, AbstractManifestClass readedClass, String methodName) {
            try {
                this.invokeManifestMethod(cls, Enum.class, readedClass, data, methodName);
                return true;
            } catch (NoSuchMethodException e) {
                System.err.println(e.getLocalizedMessage());
            } catch (IllegalAccessException e) {
                System.err.println(e.getLocalizedMessage());
            } catch (InvocationTargetException e) {
                System.err.println(e.getLocalizedMessage());
            }
            return false;
        }
    };

    public abstract boolean parseData(String data, Class cls, AbstractManifestClass readedClass, String methodName);

    void invokeManifestMethod(Class cls, Class methodParamClass, AbstractManifestClass readedClass, Object data, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class[] paramClass;
        paramClass = new Class[1];
        //set param type
        paramClass[0] = methodParamClass;
        //get method to be called
        Method method = null;
        try {
            method = cls.getDeclaredMethod(methodName, paramClass);
            //call
            method.invoke(readedClass, data);
        } catch (NoSuchMethodException | SecurityException e) {
            System.err.println(e.getLocalizedMessage());
            //save as other in the map
            for (Method m : cls.getMethods()) {
                if (m.getName().equals("saveInMap")) {
                    method = m;
                    break;
                }
            }
            //call
            if (method != null)
                method.invoke(readedClass, methodName.replace("set", ""), data);
        }
    }
}
