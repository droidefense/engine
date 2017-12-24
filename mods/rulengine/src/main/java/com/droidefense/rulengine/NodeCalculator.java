package com.droidefense.rulengine;

public class NodeCalculator {

    //CLASS METHODS

    public static String getClassNameForFullPath(String fullname) {
        if (fullname == null)
            return "";
        fullname = fullname.replace(".", "/");
        int idx = fullname.lastIndexOf("/");
        if (idx != -1)
            return fullname.substring(idx + 1);
        return fullname.replace("/", ".");
    }

    public static String nodeTypeResolver(String sc) {
        switch (sc) {
            case "java/lang/Object":
                return "Developer | Generic";
            case "android/app/Activity":
            case "android/app/AppCompatActivity":
                return "Activity";
            case "android/database/sqlite/SQLiteOpenHelper":
                return "Developer | Database";
            default:
                return "Developer | " + getClassNameForFullPath(sc);
        }
    }
}
