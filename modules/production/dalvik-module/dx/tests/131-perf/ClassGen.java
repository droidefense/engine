import java.io.File;
import java.io.PrintWriter;

public class ClassGen {

    public static void main(String... args) {

        int start = 1;
        int end = 8024;
        int fields = 4;
        int methods = 6;
        if (args.length > 0) {
            start = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            end = Integer.parseInt(args[1]);
        }
        if (args.length > 2) {
            fields = Integer.parseInt(args[2]);
        }
        if (args.length > 3) {
            methods = Integer.parseInt(args[3]);
        }

        for (int file = start; file <= end; file++) {
            try {
                File f = new File("src/Clazz" + file + ".java");
                PrintWriter pw = new PrintWriter(f);
                pw.println("class Clazz" + file + " {");
                for (int field = 1; field <= fields; field++) {
                    pw.println("    public static int f" + field + ";");
                }
                for (int method = 1; method <= methods; method++) {
                    pw.println("    boolean m" + method + "_" + (file % (end / 2)) + "() {"
                    );
                    pw.println("      int max = Thread.MAX_PRIORITY;");
                    pw.println("      for (int i = 0; i < max; i++) {");
                    pw.println("        System.out.println(\"Hello from: \" + Clazz"
                            + file + ".class + \".method" + method
                            + "() \" + Clazz" + (end - file + 1) + ".f1);");
                    pw.println("        Thread.dumpStack();");
                    pw.println("      }");
                    pw.println("      return Thread.holdsLock(this);");
                    pw.println("    }");
                }
                pw.println("}");
                pw.close();
            } catch (Exception ex) {
                System.out.println("Ups");
            }
        }
    }
}
