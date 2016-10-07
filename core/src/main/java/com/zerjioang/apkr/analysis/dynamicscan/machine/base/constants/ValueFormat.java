package com.zerjioang.apkr.analysis.dynamicscan.machine.base.constants;

import com.zerjioang.apkr.analysis.dynamicscan.machine.base.struct.generic.IAtomField;
import com.zerjioang.apkr.analysis.dynamicscan.machine.reader.DexClassReader;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by B328316 on 26/02/2016.
 */
public enum ValueFormat implements Serializable {

    /**
     * Type safe class for dalvik value type option handling
     */

    VALUE_BYTE {
        @Override
        public byte getInstructionByteId() {
            return 0x00;
        }

        @Override
        public boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader) {
            staticField.setIntValue(loader.readByte());
            return SUPPORTED;
        }

        @Override
        public String getName() {
            return "BYTE";
        }
    },
    VALUE_SHORT {
        @Override
        public byte getInstructionByteId() {
            return 0x02;
        }

        @Override
        public boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader) {
            staticField.setIntValue((short) loader.readValueByTypeArgument(valueArgument));
            return SUPPORTED;
        }

        @Override
        public String getName() {
            return "SHORT";
        }
    },
    VALUE_CHAR {
        @Override
        public byte getInstructionByteId() {
            return 0x03;
        }

        @Override
        public boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader) {
            staticField.setIntValue((char) loader.readValueByTypeArgument(valueArgument));
            return SUPPORTED;
        }

        @Override
        public String getName() {
            return "CHAR";
        }
    },
    VALUE_INT {
        @Override
        public byte getInstructionByteId() {
            return 0x04;
        }

        @Override
        public boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader) {
            staticField.setIntValue((int) loader.readValueByTypeArgument(valueArgument));
            return SUPPORTED;
        }

        @Override
        public String getName() {
            return "INT";
        }
    },
    VALUE_LONG {
        @Override
        public byte getInstructionByteId() {
            return 0x06;
        }

        @Override
        public boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader) {
            staticField.setLongValue(loader.readValueByTypeArgument(valueArgument));
            return SUPPORTED;
        }

        @Override
        public String getName() {
            return "LONG";
        }

    },
    VALUE_FLOAT {
        @Override
        public byte getInstructionByteId() {
            return 0x10;
        }

        @Override
        public boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader) {
            staticField.setLongValue(loader.readValueByTypeArgument(valueArgument));
            return SUPPORTED;
        }

        @Override
        public String getName() {
            return "FLOAT";
        }
    },
    VALUE_DOUBLE {
        @Override
        public byte getInstructionByteId() {
            return 0x11;
        }

        @Override
        public boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader) {
            staticField.setLongValue(loader.readValueByTypeArgument(valueArgument));
            return SUPPORTED;
        }

        @Override
        public String getName() {
            return "DOUBLE";
        }
    },
    VALUE_STRING {
        @Override
        public byte getInstructionByteId() {
            return 0x17;
        }

        @Override
        public boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader) {
            staticField.setObjectValue(loader.getStrings()[(int) loader.readValueByTypeArgument(valueArgument)]);
            return SUPPORTED;
        }

        @Override
        public String getName() {
            return "STRING";
        }
    },
    VALUE_TYPE {
        @Override
        public byte getInstructionByteId() {
            return 0x18;
        }

        @Override
        public boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader) {
            staticField.setObjectValue(loader.getTypes()[(int) loader.readValueByTypeArgument(valueArgument)]);
            return SUPPORTED_NOT_TESTED;
        }

        @Override
        public String getName() {
            return "TYPE";
        }
    },
    VALUE_FIELD {
        @Override
        public byte getInstructionByteId() {
            return 0x19;
        }

        @Override
        public boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader) {
            staticField.setObjectValue(loader.getFieldTypes()[(int) loader.readValueByTypeArgument(valueArgument)]);
            return SUPPORTED_NOT_TESTED;
        }

        @Override
        public String getName() {
            return "FIELD";
        }
    },
    VALUE_METHOD {
        @Override
        public byte getInstructionByteId() {
            return 0x1A;
        }

        @Override
        public boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader) {
            staticField.setObjectValue(loader.getMethodTypes()[(int) loader.readValueByTypeArgument(valueArgument)]);
            return SUPPORTED_NOT_TESTED;
        }

        @Override
        public String getName() {
            return "METHOD";
        }
    },
    VALUE_ENUM {
        @Override
        public byte getInstructionByteId() {
            return 0X1B;
        }

        @Override
        public boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader) {
            //staticField.setObjectValue(loader.getTypes()[(int) loader.readValueByTypeArgument(valueArgument)]);
            //return SUPPORTED_NOT_TESTED;
            return NOT_SUPPORTED;
        }

        @Override
        public String getName() {
            return "ENUM";
        }
    },
    VALUE_ARRAY {
        @Override
        public byte getInstructionByteId() {
            return 0X1C;
        }

        @Override
        public boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader) {
            //staticField.setObjectValue(loader.getTypes()[(int) loader.readValueByTypeArgument(valueArgument)]);
            //return SUPPORTED_NOT_TESTED;
            return NOT_SUPPORTED;
        }

        @Override
        public String getName() {
            return "ARRAY";
        }
    },
    VALUE_ANNOTATION {
        @Override
        public byte getInstructionByteId() {
            return 0X1D;
        }

        @Override
        public boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader) {
            //staticField.setObjectValue(loader.getTypes()[(int) loader.readValueByTypeArgument(valueArgument)]);
            //eturn SUPPORTED_NOT_TESTED;
            return NOT_SUPPORTED;
        }

        @Override
        public String getName() {
            return "ANNOTATION";
        }
    },
    VALUE_NULL {
        @Override
        public byte getInstructionByteId() {
            return 0X1E;
        }

        @Override
        public boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader) {
            staticField.setObjectValue(null);
            return SUPPORTED;
        }

        @Override
        public String getName() {
            return "NULL";
        }
    },
    VALUE_BOOLEAN {
        @Override
        public byte getInstructionByteId() {
            return 0X1F;
        }

        @Override
        public boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader) {
            staticField.setIntValue(valueArgument);
            return SUPPORTED;
        }

        @Override
        public String getName() {
            return "BOOLEAN";
        }
    };

    public static final boolean SUPPORTED = true;
    public static final boolean NOT_SUPPORTED = false;
    public static final boolean SUPPORTED_NOT_TESTED = true;

    private static Map<Integer, Integer> valMap = new HashMap<>();

    public static ValueFormat getDataType(int valueType) {
        if (valMap.isEmpty()) {
            //map instruction id with position of the enum array. Using this, I avoid 'for' searching
            valMap.put(0x00, 0);
            valMap.put(0x02, 1);
            valMap.put(0x03, 2);
            valMap.put(0x04, 3);
            valMap.put(0x06, 4);
            valMap.put(0x10, 5);
            valMap.put(0x11, 6);
            valMap.put(0x17, 7);
            valMap.put(0x18, 8);
            valMap.put(0x19, 9);
            valMap.put(0x1A, 10);
            valMap.put(0x1B, 11);
            valMap.put(0x1C, 12);
            valMap.put(0x1D, 13);
            valMap.put(0x1E, 14);
            valMap.put(0x1F, 15);
        }
        int index = valMap.get(valueType);
        return values()[index];
    }

    public abstract byte getInstructionByteId();

    public abstract boolean setValue(IAtomField staticField, int valueArgument, DexClassReader loader);

    public abstract String getName();
}