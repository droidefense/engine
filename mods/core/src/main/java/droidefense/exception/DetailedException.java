package droidefense.exception;

/**
 * Created by .local on 14/10/2016.
 */
public class DetailedException extends Exception {

    //create one static buffer instead of calling 'new StringBuffer()' for each exception found
    private static StringBuffer sb = new StringBuffer();
    private final String baseMessage;

    public DetailedException(String s) {
        super(getDetailedMessage(s));
        this.baseMessage = s;
    }

    private static String getDetailedMessage(String s) {
        sb.setLength(0);
        String callerClass = getCallerClassName();
        //clear buffer
        sb.setLength(0);
        //add new content
        sb.append("\n\tException trace: \t\n");
        sb.append(callerClass);
        sb.append("\n");
        sb.append("\tException Message: ");
        sb.append(s);
        return sb.toString();
    }

    public static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();

        for (int i = 4; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (ste.getClassName().startsWith("droidefense.")) {
                sb.append("\t\t");
                sb.append(ste.getClassName());
                sb.append("->");
                sb.append(ste.getMethodName());
                sb.append("::");
                sb.append(ste.getLineNumber());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public String getBaseMessage() {
        return baseMessage;
    }
}
