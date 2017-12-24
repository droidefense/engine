package droidefense.om.machine.base;

import droidefense.om.machine.base.struct.generic.IDroidefenseClass;
import droidefense.om.machine.reader.DexClassReader;
import droidefense.sdk.helpers.DroidDefenseEnvironment;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.om.machine.base.constants.TypeDescriptorSemantics;
import droidefense.om.machine.base.struct.fake.EncapsulatedClass;
import droidefense.om.machine.base.struct.generic.IAtomInstance;
import droidefense.om.machine.base.struct.model.DVMInstance;
import droidefense.sdk.helpers.InternalConstant;
import droidefense.sdk.model.base.DroidefenseProject;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DynamicUtils {

    private static DroidDefenseEnvironment environment = DroidDefenseEnvironment.getInstance();

    public static String fromTypeToClassName(final String type) {
        return type.substring(1, type.length() - 1);
    }

    public static String toDotSeparatorClassName(final String slashSeparatorClassName) {
        return slashSeparatorClassName.substring(1, slashSeparatorClassName.length() - 1).replace('/', '.');
    }

    public static String convertStringBuilderToStringBuffer(final String value) {
        int start = value.indexOf("java/lang/StringBuilder");
        if (start == -1) {
            return value;
        }
        StringBuffer returned = new StringBuffer();
        int end = 0;
        while (start != -1) {
            returned.append(value.substring(end, start));
            returned.append("java/lang/StringBuffer");
            end = start + "java/lang/StringBuilder".length();
            start = value.indexOf("java/lang/StringBuilder", end);
        }
        returned.append(value.substring(end));
        return returned.toString();
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static String capitalizeString(String clname) {
        String start = "" + clname.charAt(0);
        start = start.toUpperCase();
        String end = clname.substring(1);
        clname = start + end;
        return clname;
    }

    public static String classNameToJava(String clname) {
        return clname.replace("/", ".");
    }

    public static String descriptorToClassName(String descriptor) {
        descriptor = descriptor.replace("(L", "");
        int idx = descriptor.indexOf(";)");
        if (idx != -1) {
            descriptor = descriptor.substring(0, idx);
            descriptor = descriptor.replace("/", ".");
        }
        return descriptor;
    }

    public static String[] descriptorListToClassName(String descriptor) {
        String[] data = descriptor.split(";");
        for (int i = 0; i < data.length; i++) {
            data[i] = descriptorToClassName(data[i]);
        }
        return data;
    }

    public static String format(final long value, final long divider, final String unit) {
        final double result = divider > 1 ? (double) value / (double) divider : (double) value;
        return String.format("%.1f %s", result, unit);
    }

    public static long getLong(final int[] ints, final int offset) {
        return (ints[offset] & 0xFFFFFFFFL) | ((long) ints[offset + 1] << 32);
    }

    public static void setLong(final int[] ints, final int offset, final long value) {
        ints[offset] = (int) (value);
        ints[offset + 1] = (int) (value >>> 32);
    }

    public static boolean hasNoValue(final int value) {
        return value == -1;
    }

	/*
     * Un poco de literatura:
	 * dalvik no tiene pla, tiene registros. concretamente unos 64k, la mayoria de instrucciones
	 * solo usan los 256 primeros.
	 * cualquier dato entra en un solo registro. A excepcion de los double y long que necesitan 2 registros consecutivos
	 * */
    //Dex file info on https://source.android.com/devices/tech/dalvik/dex-format.html
    //---- DEX DATA TYPES

	/*
     * byte	8-bit signed int
		ubyte	8-bit unsigned int
		short	16-bit signed int, little-endian
		ushort	16-bit unsigned int, little-endian
		int	32-bit signed int, little-endian
		uint	32-bit unsigned int, little-endian
		long	64-bit signed int, little-endian
		ulong	64-bit unsigned int, little-endian
		sleb128	signed LEB128, variable-length (see below)
		uleb128	unsigned LEB128, variable-length (see below)
		uleb128p1	unsigned LEB128 plus 1, variable-length (see below)
	 */

    public static byte[] toBytes(final File dexFile) {
        final byte[] bytes = new byte[(int) dexFile.length()];
        DataInputStream in = null;
        try {
            in = new DataInputStream(new BufferedInputStream(new FileInputStream(dexFile)));
            in.readFully(bytes);
        } catch (IOException e) {
            System.err.println("The specified dex file path is invalid: " + dexFile.getName());
            System.exit(-1);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }

    public static String toClassName(final String type) {
        return type.substring(1, type.length() - 1);
    }

    public static int toInt(final boolean value) {
        return value ? 1 : 0;
    }

    /**
     * Compare given data array with dex magic signature
     *
     * @param dexData
     * @return
     */
    public static boolean verifyDexFile(byte[] dexData) {
        boolean valid = true;
        byte[] signature = InternalConstant.DEX_FILE_MAGIC;
        Log.write(LoggerType.TRACE,"Checking .dex file signature");
        for (int i = 0; i < signature.length; i++) {
            valid &= dexData[i] == signature[i];
        }
        Log.write(LoggerType.TRACE,".dex file seems to be " + (valid ? "valid." : "not valid."));
        return true;
    }

    private static String resolvePrimaryClassName(String className) {
        return TypeDescriptorSemantics.resolveDescriptor(className);
    }

    public static IAtomInstance convertToEncapsulatedClass(Object o) {
        EncapsulatedClass ec = new EncapsulatedClass(o.getClass().getName());
        ec.setClass(o.getClass());
        ec.setJavaObject(o);
        return new DVMInstance(ec);
    }

    public static Class[] getParamsClasses(String desc) {
        String data[] = desc.split("\\)");
        String methodParamStr = data[0];
        methodParamStr = methodParamStr.replace("(", "").trim();
        if (methodParamStr.isEmpty()) {
            return new Class[]{};
        }
        String classNames[] = methodParamStr.split(";");
        Class[] cls = new Class[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            String s = classNames[i];
            if (s.length() == 1) {
                String clname = DynamicUtils.resolvePrimaryClassName(s);
                clname = capitalizeString(clname);
                EncapsulatedClass loaded = (EncapsulatedClass) DexClassReader.getInstance().load("java.lang." + clname);
                cls[i] = loaded.getJavaObject().getClass();
            } else {
                String clname = s.substring(1);
                EncapsulatedClass loaded = (EncapsulatedClass) DexClassReader.getInstance().load(clname);
                cls[i] = loaded.getJavaObject().getClass();
            }
        }
        return cls;
    }

    //TODO check and complete
    public static String beautifyClassName(String dexClassName) {
        return dexClassName;
    }

    public static IDroidefenseClass[] getExecutionEntryPoints(DroidefenseProject currentProject) {
        // old: return only main cls as entry point
        // return new DalvikClass[]{c};
        //return all next classes: Activities, services, receivers, events
        ArrayList<IDroidefenseClass> entry = new ArrayList<>();
        for (IDroidefenseClass c : currentProject.getListClasses()) {
            if (hasEntryPoint(c) && !isAndroidNative(c)) {
                entry.add(0, c);
            }
        }
        Log.write(LoggerType.INFO, "Static analysis detect " + entry.size() + " entry points");
        IDroidefenseClass[] entryArray = entry.toArray(new IDroidefenseClass[entry.size()]);

        //save detected entry points for report generation
        currentProject.setDynamicEntryPoints(entryArray);
        return entryArray;
    }

    private static boolean isAndroidNative(IDroidefenseClass c) {
        String clname = c.getName();
        clname = DynamicUtils.classNameToJava(clname);
        boolean full = environment.isAndroidNative(clname);
        boolean parentb = false;
        int lastPoint;
        do {
            lastPoint = clname.lastIndexOf(".");
            String parent = null;
            if (lastPoint != -1) {
                parent = clname.substring(0, lastPoint);
            }
            if (parent != null) {
                parentb |= environment.isAndroidNative(parent);
                clname = parent;
            }
        } while (lastPoint != -1);
        return full || parentb;
    }

    public static boolean hasEntryPoint(IDroidefenseClass c) {
        return c.getSuperClass().equals("android/app/Service")
                || c.getSuperClass().equals("android/content/ContentProvider")
                || c.getSuperClass().equals("android/app/Activity")
                || c.getSuperClass().equals("android/content/BroadcastReceiver");
    }

    public static String getParamStringFromDescriptor(String param) {
        if (param != null) {
            int idx = param.indexOf(")");
            if (idx != -1) {
                return param.substring(0, idx + 1);
            }
        }
        return param;
    }

    public static String getReturnTypeFromDescriptor(String param) {
        if (param != null) {
            int idx = param.indexOf(")");
            if (idx != -1) {
                return TypeDescriptorSemantics.resolveDescriptor(param.substring(idx + 1));
            }
        }
        return param;
    }
}
