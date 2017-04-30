package droidefense.rulengine;

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
        if (sc.equals("java/lang/Object")) {
            return "Developer | Generic";
        } else if (sc.equals("android/database/sqlite/SQLiteOpenHelper")) {
            return "Developer | Database";
        } else {
            return "Developer | " + getClassNameForFullPath(sc);
        }
    }
}
