package droidefense.emulator.machine.base.constants;

import java.io.Serializable;

public enum AccessFlag implements Serializable {

    /**
     * access_flags definitions
     * embedded in class_def_item, encoded_field, encoded_method, and InnerClass
     * <p>
     * Bitfields of these flags are used to indicate the accessibility and overall properties of classes and class members.
     */

    //https://source.android.com/devices/tech/dalvik/dex-format.html

    ACC_PUBLIC {
        public int getValue() {
            return 0x1;
        }

        public String getForClasses() {
            return "public";
        }

        public String getDescription() {
            return "public: visible everywhere";
        }

    },
    ACC_PRIVATE {
        public int getValue() {
            return 0x2;
        }

        public String getForClasses() {
            return "private";
        }

        public String getDescription() {
            return "private: only visible to defining class";
        }

    },
    ACC_PROTECTED {
        public int getValue() {
            return 0x4;
        }

        public String getForClasses() {
            return "protected";
        }

        public String getDescription() {
            return "protected: visible to package and subclasses";
        }

    },
    ACC_STATIC {
        public int getValue() {
            return 0x8;
        }

        public String getForClasses() {
            return "static";
        }

        public String getDescription() {
            return "static: is not constructed with an outer 'this' reference";
        }

    },
    ACC_FINAL {
        public int getValue() {
            return 0x10;
        }

        public String getForClasses() {
            return "final";
        }

        public String getDescription() {
            return "final: not subclassable, immutable after construction, not overridable";
        }

    },
    ACC_SYNCHRONIZED {
        public int getValue() {
            return 0x20;
        }

        public String getForClasses() {
            return "synchronized";
        }

        public String getDescription() {
            return "synchronized: associated lock automatically acquired around call to this method.";
        }

    },
    ACC_VOLATILE {
        public int getValue() {
            return 0x40;
        }

        public String getForClasses() {
            return "volatile";
        }

        public String getDescription() {
            return "volatile: special access rules to help with thread safety";
        }

    },
    ACC_BRIDGE {
        public int getValue() {
            return 0x40;
        }

        public String getForClasses() {
            return "bridge";
        }

        public String getDescription() {
            return "bridge method, added automatically by compiler as a type-safe bridge";
        }

    },
    ACC_TRANSIENT {
        public int getValue() {
            return 0x80;
        }

        public String getForClasses() {
            return "transient";
        }

        public String getDescription() {
            return "transient: not to be saved by default serialization";
        }

    },
    ACC_VARARGS {
        public int getValue() {
            return 0x80;
        }

        public String getForClasses() {
            return "varargs";
        }

        public String getDescription() {
            return "last argument should be treated as a \"rest\" argument by compiler";
        }

    },
    ACC_NATIVE {
        public int getValue() {
            return 0x100;
        }

        public String getForClasses() {
            return "native";
        }

        public String getDescription() {
            return "native: implemented in native code";
        }

    },
    ACC_INTERFACE {
        public int getValue() {
            return 0x200;
        }

        public String getForClasses() {
            return "interface";
        }

        public String getDescription() {
            return "interface: multiply-implementable abstract class";
        }

    },
    ACC_ABSTRACT {
        public int getValue() {
            return 0x400;
        }

        public String getForClasses() {
            return "abstract";
        }

        public String getDescription() {
            return "abstract: not directly instantiable	abstract: unimplemented by this class";
        }

    },
    ACC_STRICT {
        public int getValue() {
            return 0x800;
        }

        public String getForClasses() {
            return "strictfp";
        }

        public String getDescription() {
            return "strictfp: strict rules for floating-point arithmetic";
        }

    },
    ACC_SYNTHETIC {
        public int getValue() {
            return 0x1000;
        }

        public String getForClasses() {
            return "synthetic";
        }

        public String getDescription() {
            return "not directly defined in source code";
        }

    },
    ACC_ANNOTATION {
        public int getValue() {
            return 0x2000;
        }

        public String getForClasses() {
            return "annotation";
        }

        public String getDescription() {
            return "declared as an annotation class";
        }

    },
    ACC_ENUM {
        public int getValue() {
            return 0x4000;
        }

        public String getForClasses() {
            return "enum";
        }

        public String getDescription() {
            return "declared as an enumerated type";
        }

    },
    UNUSED {
        public int getValue() {
            return 0x8000;
        }

        public String getForClasses() {
            return "unused";
        }

        public String getDescription() {
            return "--- unused ---";
        }

    },
    ACC_CONSTRUCTOR {
        public int getValue() {
            return 0x10000;
        }

        public String getForClasses() {
            return "constructor";
        }

        public String getDescription() {
            return "constructor method (class or instance initializer)";
        }

    },
    ACC_DECLARED_SYNCHRONIZED {
        public int getValue() {
            return 0x20000;
        }

        public String getForClasses() {
            return "declared sync";
        }

        public String getDescription() {
            return "declared synchronized.";
        }

    };

    public abstract int getValue();

    public abstract String getForClasses();

    public abstract String getDescription();
}