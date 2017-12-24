package droidefense.om.machine.reader;

import droidefense.om.machine.base.struct.generic.IDroidefenseClass;
import droidefense.om.machine.base.struct.generic.IDroidefenseMethod;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.om.machine.base.AbstractDVMThread;
import droidefense.om.machine.base.DalvikVM;
import droidefense.om.machine.base.DynamicUtils;
import droidefense.om.machine.base.constants.AccessFlag;
import droidefense.om.machine.base.constants.ValueFormat;
import droidefense.om.machine.base.exceptions.NotSupportedValueTypeException;
import droidefense.om.machine.base.struct.fake.DVMTaintClass;
import droidefense.om.machine.base.struct.fake.DVMTaintField;
import droidefense.om.machine.base.struct.fake.EncapsulatedClass;
import droidefense.om.machine.base.struct.generic.IAtomField;
import droidefense.om.machine.base.struct.generic.IAtomFrame;
import droidefense.om.machine.base.struct.model.DVMClass;
import droidefense.om.machine.base.struct.model.DVMField;
import droidefense.om.machine.base.struct.model.DVMMethod;
import droidefense.om.machine.base.struct.model.SharedPool;
import droidefense.sdk.helpers.InternalConstant;
import droidefense.sdk.model.base.DroidefenseProject;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Hashtable;

public final class DexClassReader implements Serializable {

    //set method shared pool. singleton
    private static SharedPool pool = SharedPool.getInstance();

    //Singleton class
    private static DexClassReader instance;

    private final DalvikVM vm;
    private final Object loadClassesMutex = new Object();
    private DroidefenseProject currentProject;
    private byte[] dexFileContent;
    private int offset;
    private int[] oldOffset = new int[5];
    private int oldOffsetIndex = 0;

    private String[] strings;
    private String[] types;
    private String[] descriptors;
    private String[] fieldClasses;
    private String[] fieldTypes;
    private String[] fieldNames;
    private String[] methodClasses;
    private String[] methodTypes;
    private String[] methodNames;

    private DexClassReader[] readerList;

    private DexClassReader(DalvikVM dalvikVM, DroidefenseProject currentProject) {
        super();
        this.vm = dalvikVM;
        //link this loader to a currentProject
        this.currentProject = currentProject;
    }

    public static DexClassReader init(DalvikVM dalvikVM, DroidefenseProject currentProject) {
        if (instance == null)
            instance = new DexClassReader(dalvikVM, currentProject);
        return instance;
    }

    public static DexClassReader getInstance() {
        return instance;
    }

    private static boolean hasNoValue(final int value) {
        return value == -1;
    }

    public IDroidefenseClass load(IDroidefenseClass cls) {
        return this.load(cls.getName());
    }

    public IDroidefenseClass load(String name) {
        IDroidefenseClass cls;
        name = name.replace(".", "/");
        if (pool.getClasses().containsKey(name)) {
            //class exist on dex file
            cls = pool.getClasses().get(name);
        } else {
            cls = findClass(name);
            pool.addClass(name, cls);
        }
        if ( !cls.isBinded() ){
            if( !cls.isFake() ){
                initializeLoadedClass(cls);
            } else{
                //class is fake, so no need to initialize varaibles and static attibutes
                Log.write(LoggerType.DEBUG, "Fake class no need to be binded");
            }
        } else {
            //class already binded, which means it is already initialized
            Log.write(LoggerType.DEBUG, cls.getName()+" already binded. No need to rebind");
        }
        return cls;
    }

    private void initializeLoadedClass(IDroidefenseClass cls) {
        IDroidefenseMethod initMethod = cls.findClassInitMethod();
        if (initMethod != null) {
            if(!initMethod.isFake()) {
                //TOdo changed this. may explode
                AbstractDVMThread firstThread = getVm().getThread(0);
                if (firstThread != null) {
                    IAtomFrame frame = firstThread.pushFrame();
                    frame.init(initMethod);
                    cls.setBinded(true);
                }
            }
        /*try {
            loadThread.run();
        } catch (ChangeThreadException e) {
            // TODO Implement here by checking the behavior of the class loading in The Java Virtual Machine Specification
        } catch (Throwable e) {
            com.error(e);
        }*/
        }
        else{
            //no init method found.possibly because init is inherited from parent class.
            //we temporaly mark it as successfully binded, but it is not
            cls.setBinded(true);
            //TODO go to parent class, search for init method and execute it
        }
    }

    private IDroidefenseClass findClass(final String name) {
        //class does not exists on .dex file.
        //check if class belongs to java sdk or to android sdk.
        //anyway, if does not exist, send a fake class

        IDroidefenseClass javaClass = null;
        String cname = name.replace(".", "/");

        //1 try to load cls from java jdk via reflection
        try {
            Class<?> s = Class.forName(name.replace("/", "."));

            //TODO Object[] lastCallArgs = loadThread.getLastMethodArgs();
            Object[] lastCallArgs = null;
            Class<?>[] classes;
            if (lastCallArgs == null && name.equals(InternalConstant.SUPERCLASS)) {
                //Special case. this class has no super
                Object newInstance = s.newInstance();
                EncapsulatedClass newClass = buildFakeClss(name, newInstance);
                newClass.setClass(s);
                newClass.setJavaObject(newInstance);
                newClass.setSuperClass(null);
                currentProject.addDexClass(name, newClass);
                return newClass;
            } else if (lastCallArgs == null && name.startsWith("java/lang/")) {
                //this class belongs to java/lang core
                Object newInstance = s.newInstance();
                EncapsulatedClass newClass = buildFakeClss(name, newInstance);
                newClass.setClass(s);
                newClass.setJavaObject(newInstance);
                currentProject.addDexClass(name, newClass);
                return newClass;
            } else if (lastCallArgs == null) {
                Constructor<?> constructor = s.getConstructor();
                if (constructor != null) {
                    Object newInstance = constructor.newInstance();
                    EncapsulatedClass newClass = buildFakeClss(name, newInstance);
                    newClass.setClass(s);
                    newClass.setJavaObject(newInstance);
                    currentProject.addDexClass(name, newClass);
                    return newClass;
                }
            } else {
                classes = new Class[lastCallArgs.length];
                int i = 0;
                for (Object obj : lastCallArgs) {
                    classes[i] = obj.getClass();
                    i++;
                }
                Constructor<?> constructor = s.getConstructor(classes);
                if (constructor != null) {
                    Object newInstance = constructor.newInstance(lastCallArgs);
                    IDroidefenseClass newClass = buildFakeClss(name, newInstance);
                    currentProject.addDexClass(name, newClass);
                    return newClass;
                }
            }
        } catch (ClassNotFoundException e) {
            Log.write(LoggerType.ERROR, "Could not find class on java SDK " + name, e);
        } catch (Exception e) {
            Log.write(LoggerType.ERROR, "Error when loading class from java SDK " + name, e);
        }

        //Last option, emulate cls behaviour, emulate it!
        if (cname.contains("$")) {
            String[] data = cname.split("\\$");
            cname = data[0];
            javaClass = new DVMTaintClass(name);
            for (int i = 1; i < data.length; i++) {
                ((DVMTaintClass) javaClass).addDVMTaintField(new DVMTaintField(data[i], javaClass));
            }
            currentProject.addDexClass(name, javaClass);
        } else {
            javaClass = new DVMTaintClass(name);
            currentProject.addDexClass(name, javaClass);
        }

        return javaClass;
    }

    private EncapsulatedClass buildFakeClss(String name, Object newInstance) {
        EncapsulatedClass newClass = new EncapsulatedClass(name);
        newClass.setName(name);
        newClass.setJavaObject(newInstance);
        newClass.setSuperClass(InternalConstant.SUPERCLASS);
        return newClass;
    }

    public boolean loadClasses(final byte[] dexFileContent, boolean multidex) {
        //todo add support for multiple dex files
        synchronized (loadClassesMutex) {
            this.dexFileContent = dexFileContent;
            offset = 0;

            checkData("magic number", "6465780A30333500");

            skip("checksum", 4);
            skip("SHA-1 signature", 20);

            checkUInt("file size", dexFileContent.length);
            checkUInt("header size", 0x70);
            checkUInt("endian", 0x12345678);

            checkUInt("link size", 0);
            checkUInt("link offset", 0);

            readMap();
            readStrings();
            readTypes();
            readDescriptors();
            readFields();
            readMethods();
            readClassContents();
        }
        currentProject.setPool(pool);
        return true;
    }

    private void readClassContents() {
        int count = readUInt();
        int offset = readUInt();
        if (offset != 0) {
            pushOffset(offset);
            for (int i = 0; i < count; i++) {
                IDroidefenseClass cls = new DVMClass();

                String clsName = DynamicUtils.fromTypeToClassName(types[readUInt()]);
                pool.addClassName(clsName);
                cls.setName(clsName);

                cls.setFlag(readUInt());
                boolean isInterface = ((cls.getFlag() & AccessFlag.ACC_INTERFACE.getValue()) != 0) | ((cls.getFlag() & AccessFlag.ACC_ABSTRACT.getValue()) != 0);
                cls.setInterface(isInterface);

                int superClassIndex = readUInt();
                if (hasNoValue(superClassIndex)) {
                    cls.setSuperClass(InternalConstant.SUPERCLASS);
                } else {
                    cls.setSuperClass(DynamicUtils.fromTypeToClassName(types[superClassIndex]));
                }

                int interfacesOffset = readUInt();
                if (interfacesOffset != 0) {
                    pushOffset(interfacesOffset);

                    int interfaceCount = readUInt();
                    String[] interfaces = new String[interfaceCount];
                    cls.setInterfaces(interfaces);
                    for (int j = 0; j < interfaceCount; j++) {
                        interfaces[j] = DynamicUtils.fromTypeToClassName(types[readUShort()]);
                    }

                    popOffset();
                }
                skip("source file index", 4);
                skip("anotations offset", 4);

                int classDataOffset = readUInt();
                if (classDataOffset != 0) {
                    pushOffset(classDataOffset);

                    IAtomField[] staticFields = new IAtomField[readULEB128()];
                    IAtomField[] instanceFields = new IAtomField[readULEB128()];
                    DVMMethod[] directMethods = new DVMMethod[readULEB128()];
                    DVMMethod[] virtualMethods = new DVMMethod[readULEB128()];

                    readFields(cls, staticFields, false);
                    readFields(cls, instanceFields, true);
                    readMethodContents(cls, directMethods);
                    readMethodContents(cls, virtualMethods);

                    cls.setStaticFields(staticFields);
                    cls.setStaticFieldMap(new Hashtable());
                    for (IAtomField field : staticFields) {
                        cls.getStaticFieldMap().put(field.getName(), field);
                    }
                    cls.setInstanceFields(instanceFields);
                    cls.setDirectMethods(directMethods);
                    cls.setVirtualMethods(virtualMethods);

                    popOffset();
                }

                int staticValuesOffset = readUInt();
                if (staticValuesOffset != 0) {
                    pushOffset(staticValuesOffset);

                    int length = readULEB128();
                    for (int j = 0; j < length; j++) {
                        IAtomField staticField = cls.getStaticFields()[j];

                        int data = readUByte();
                        int valueType = data & 0x1F;
                        int valueArgument = data >> 5;
                        ValueFormat dataEnum = ValueFormat.getDataType(valueType);
                        boolean valueTypeSupported = dataEnum.setValue(staticField, valueArgument, this);
                        if (!valueTypeSupported) {
                            throw new NotSupportedValueTypeException(dataEnum);
                        }
                    }
                    popOffset();
                }

                pool.addClass(cls.getName(), cls);
            }
            popOffset();
        }
    }

    public long readValueByTypeArgument(final int typeArgument) {
        return readSigned(typeArgument + 1);
    }

    private void readMethodContents(final IDroidefenseClass cls, final IDroidefenseMethod[] methods) {

        pool.setStrings(strings);
        pool.setTypes(types);
        pool.setDescriptors(descriptors);
        pool.setFieldClasses(fieldClasses);
        pool.setFieldTypes(fieldTypes);
        pool.setFieldNames(fieldNames);
        pool.setMethodClasses(methodClasses);
        pool.setMethodTypes(methodTypes);
        pool.setMethodNames(methodNames);

        int methodIndex = 0;
        for (int i = 0, length = methods.length; i < length; i++) {
            if (i == 0) {
                methodIndex = readULEB128();
            } else {
                methodIndex += readULEB128();
            }
            IDroidefenseMethod method = new DVMMethod(cls);

            method.setFlag(readULEB128());
            method.setInstance(((byte) method.getFlag() & AccessFlag.ACC_STATIC.getValue()) == 0);
            method.setSynchronized((method.getFlag() & AccessFlag.ACC_SYNCHRONIZED.getValue()) != 0);

            method.setName(methodNames[methodIndex]);
            method.setDescriptor(methodTypes[methodIndex]);

            int codeOffset = readULEB128();
            if (codeOffset != 0) {
                pushOffset(codeOffset);

                method.setRegisterCount(readUShort());
                method.setIncomingArgumentCount(readUShort());
                method.setOutgoingArgumentCount(readUShort());

                int tryItemCount = readUShort();
                int debugInfoOffset = readUInt();

                int codeLength = readUInt();
                int[] lowerCodes = new int[codeLength];
                int[] upperCodes = new int[codeLength];
                int[] codes = new int[codeLength];

                method.setOpcodes(lowerCodes);
                method.setRegistercodes(upperCodes);
                method.setIndex(codes);

                for (int j = 0; j < codeLength; j++) {
                    int data = readUShort();
                    lowerCodes[j] = data & 0xFF;
                    upperCodes[j] = data >> 8;
                    codes[j] = data;
                }
                if (codeLength % 2 != 0 && tryItemCount != 0) {
                    skip("padding", 2);
                }

                int[] exceptionStartAddresses = new int[tryItemCount];
                int[] exceptionEndAddresses = new int[tryItemCount];
                int[] exceptionHandlerIndex = new int[tryItemCount];

                method.setExceptionStartAddresses(exceptionStartAddresses);
                method.setExceptionEndAdresses(exceptionEndAddresses);
                method.setExceptionHandlerIndexes(exceptionHandlerIndex);

                if (tryItemCount != 0) {
                    for (int j = 0; j < tryItemCount; j++) {
                        int startAddress = exceptionStartAddresses[j] = readUInt();
                        exceptionEndAddresses[j] = startAddress + readUShort();
                        exceptionHandlerIndex[j] = readUShort();
                    }

                    int baseOffset = offset;
                    int listCount = readULEB128();
                    String[][] exceptionHandlerTypes = new String[listCount][];
                    int[][] exceptionHandlerAddresses = new int[listCount][];

                    method.setExceptionHandlerTypes(exceptionHandlerTypes);
                    method.setExceptionHandlerAddresses(exceptionHandlerAddresses);
                    for (int j = 0; j < listCount; j++) {
                        int comaredOffset = offset - baseOffset;
                        for (int k = 0, k_length = exceptionStartAddresses.length; k < k_length; k++) {
                            if (exceptionHandlerIndex[k] == comaredOffset) {
                                exceptionHandlerIndex[k] = j;
                            }
                        }
                        int handlerCount = readSLEB128();
                        if (handlerCount <= 0) {
                            exceptionHandlerTypes[j] = new String[-handlerCount + 1];
                            exceptionHandlerAddresses[j] = new int[-handlerCount + 1];
                        } else {
                            exceptionHandlerTypes[j] = new String[handlerCount];
                            exceptionHandlerAddresses[j] = new int[handlerCount];
                        }
                        for (int k = 0, k_length = Math.abs(handlerCount); k < k_length; k++) {
                            exceptionHandlerTypes[j][k] = DynamicUtils.toDotSeparatorClassName(types[readULEB128()]);
                            exceptionHandlerAddresses[j][k] = readULEB128();
                        }
                        if (handlerCount <= 0) {
                            exceptionHandlerTypes[j][-handlerCount] = "java.lang.Throwable";
                            exceptionHandlerAddresses[j][-handlerCount] = readULEB128();
                        }
                    }
                }

                popOffset();
            }

            methods[i] = method;
        }
    }

    private void readFields(final IDroidefenseClass cls, final IAtomField[] fields, final boolean isInstance) {
        int fieldIndex = 0;
        for (int i = 0, length = fields.length; i < length; i++) {
            if (i == 0) {
                fieldIndex = readULEB128();
            } else {
                fieldIndex += readULEB128();
            }
            IAtomField field = new DVMField(cls);

            field.setFlag(readULEB128());
            field.setInstance(isInstance);

            field.setName(fieldNames[fieldIndex]);
            field.setType(fieldTypes[fieldIndex]);

            fields[i] = field;
        }
    }

    private void readMethods() {
        int count = readUInt();
        methodClasses = new String[count];
        methodTypes = new String[count];
        methodNames = new String[count];
        int offset = readUInt();
        if (offset != 0) {
            pushOffset(offset);
            for (int i = 0; i < count; i++) {
                String classType = types[readUShort()];
                methodClasses[i] = classType.substring(1, classType.length() - 1);
                methodTypes[i] = descriptors[readUShort()];
                methodNames[i] = strings[readUInt()];
            }
            popOffset();
        }
    }

    private void readFields() {
        int count = readUInt();
        fieldClasses = new String[count];
        fieldTypes = new String[count];
        fieldNames = new String[count];
        int offset = readUInt();
        if (offset != 0) {
            pushOffset(offset);
            for (int i = 0; i < count; i++) {
                String classType = types[readUShort()];
                fieldClasses[i] = classType.substring(1, classType.length() - 1);
                fieldTypes[i] = types[readUShort()];
                fieldNames[i] = strings[readUInt()];
            }
            popOffset();
        }
    }

    private void readDescriptors() {
        int count = readUInt();
        descriptors = new String[count];
        pushOffset(readUInt());
        for (int i = 0; i < count; i++) {
            StringBuilder buffer = new StringBuilder();
            skip("shorty index", 4);
            String returnType = types[readUInt()];

            int offset = readUInt();
            if (offset == 0) {
                buffer.append("()");
            } else {
                pushOffset(offset);

                buffer.append("(");
                int typeCount = readUInt();
                for (int j = 0; j < typeCount; j++) {
                    buffer.append(types[readUShort()]);
                }
                buffer.append(")");
                popOffset();
            }

            buffer.append(returnType);
            descriptors[i] = buffer.toString();
        }
        popOffset();
    }

    private void readTypes() {
        int count = readUInt();
        types = new String[count];
        pushOffset(readUInt());
        for (int i = 0; i < count; i++) {
            types[i] = strings[readUInt()];
        }
        popOffset();
    }

    private void readStrings() {
        int count = readUInt();
        strings = new String[count];
        pushOffset(readUInt());
        for (int i = 0; i < count; i++) {
            pushOffset(readUInt());

            int stringLength = readULEB128();
            char[] chars = new char[stringLength];
            for (int j = 0, j_length = chars.length; j < j_length; j++) {
                int data = readUByte();
                switch (data >> 4) {
                    case 0:
                        //break;
                    case 1:
                        //break;
                    case 2:
                        //break;
                    case 3:
                        //break;
                    case 4:
                        //break;
                    case 5:
                        //break;
                    case 6:
                        //break;
                    case 7:
                        chars[j] = (char) data;
                        break;
                    case 12:
                        //break;
                    case 13:
                        chars[j] = (char) (((data & 0x1F) << 6) | (readUByte() & 0x3F));
                        break;
                    case 14:
                        chars[j] = (char) (((data & 0x0F) << 12) | ((readUByte() & 0x3F) << 6) | (readUByte() & 0x3F));
                        break;
                    default:
                        throw new NoClassDefFoundError("illegal modified utf-8");
                }
            }
            strings[i] = DynamicUtils.convertStringBuilderToStringBuffer(new String(chars));

            popOffset();
        }
        popOffset();
    }

    private int readSLEB128() {
        int value = 0;
        int shiftCount = 0;
        boolean hasNext = true;
        while (hasNext) {
            int data = readUByte();
            value |= (data & 0x7F) << shiftCount;
            shiftCount += 7;
            hasNext = (data & 0x80) != 0;
        }
        return (value << (32 - shiftCount)) >> (32 - shiftCount);
    }

    private int readULEB128() {
        int value = 0;
        int shiftCount = 0;
        boolean hasNext = true;
        while (hasNext) {
            int data = readUByte();
            value |= (data & 0x7F) << shiftCount;
            shiftCount += 7;
            hasNext = (data & 0x80) != 0;
        }
        return value;
    }

    private void readMap() {
        pushOffset(readUInt());

        int count = readUInt();
        for (int i = 0; i < count; i++) {
            int type = readUShort();
            skip("unused", 2);
            int itemSize = readUInt();
            int itemOffset = readUInt();
        }

        popOffset();
    }

    private void popOffset() {
        offset = oldOffset[--oldOffsetIndex];
    }

    private void pushOffset(final int offset) {
        oldOffset[oldOffsetIndex++] = this.offset;
        this.offset = offset;
    }

    private void checkUInt(final String type, final int valueToCheck) {
        if (readUInt() != valueToCheck) {
            throw new NoClassDefFoundError("illegal " + type);
        }
    }

    private void skip(final String type, final int count) {
        offset += count;
    }

    // TODO Change the return value from int to long
    private int readUInt() {
        return readUByte() | (readUByte() << 8) | (readUByte() << 16) | (readUByte() << 24);
    }

    private int readUShort() {
        return readUByte() | (readUByte() << 8);
    }

    public int readByte() {
        return dexFileContent[offset++];
    }

    private int readUByte() {
        return dexFileContent[offset++] & 0xFF;
    }

    private long readSigned(final int byteLength) {
        long value = 0;
        for (int i = 0; i < byteLength; i++) {
            value |= (long) readUByte() << (8 * i);
        }
        int shift = 8 * byteLength;
        return (value << shift) >> shift;
    }

    private void checkData(final String type, final String valueToCheck) {
        for (int i = 0, length = valueToCheck.length() / 2; i < length; i++) {
            if (readUByte() != Integer.parseInt(valueToCheck.substring(i * 2, i * 2 + 2), 16)) {
                throw new NoClassDefFoundError("illegal " + type);
            }
        }
    }

    //GETTERS AND SETTERS

    public String[] getMethodNames() {
        return methodNames;
    }

    public void setMethodNames(String[] methodNames) {
        this.methodNames = methodNames;
    }

    public DalvikVM getVm() {
        return vm;
    }

    public Hashtable<String, IDroidefenseClass> getClasses() {
        return pool.getClasses();
    }

    public Object getLoadClassesMutex() {
        return loadClassesMutex;
    }

    public byte[] getDexFileContent() {
        return dexFileContent;
    }

    public void setDexFileContent(byte[] dexFileContent) {
        this.dexFileContent = dexFileContent;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int[] getOldOffset() {
        return oldOffset;
    }

    public void setOldOffset(int[] oldOffset) {
        this.oldOffset = oldOffset;
    }

    public int getOldOffsetIndex() {
        return oldOffsetIndex;
    }

    public void setOldOffsetIndex(int oldOffsetIndex) {
        this.oldOffsetIndex = oldOffsetIndex;
    }

    public String[] getStrings() {
        return strings;
    }

    public void setStrings(String[] strings) {
        this.strings = strings;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String[] getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(String[] descriptors) {
        this.descriptors = descriptors;
    }

    public String[] getFieldClasses() {
        return fieldClasses;
    }

    public void setFieldClasses(String[] fieldClasses) {
        this.fieldClasses = fieldClasses;
    }

    public String[] getFieldTypes() {
        return fieldTypes;
    }

    public void setFieldTypes(String[] fieldTypes) {
        this.fieldTypes = fieldTypes;
    }

    public String[] getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
    }

    public String[] getMethodClasses() {
        return methodClasses;
    }

    public void setMethodClasses(String[] methodClasses) {
        this.methodClasses = methodClasses;
    }

    public String[] getMethodTypes() {
        return methodTypes;
    }

    public void setMethodTypes(String[] methodTypes) {
        this.methodTypes = methodTypes;
    }

    public IDroidefenseClass[] getAllClasses() {
        Collection<IDroidefenseClass> data = pool.getClasses().values();
        return data.toArray(new IDroidefenseClass[data.size()]);
    }
}