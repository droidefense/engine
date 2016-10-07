package com.zerjioang.apkr.exception;

import java.util.Arrays;

/**
 * Created by .local on 15/09/2016.
 */
public class InvalidScanParametersException extends Exception {

    public InvalidScanParametersException(String str, String[] args) {
        super(str + parse(args));
    }

    private static String parse(String[] args) {

        boolean isNull = args == null;
        String details = "\nException details:\n";
        if (isNull) {
            details += "Parameter is NULL\n";
        } else {
            String type = args.getClass().toString();
            details += "Type: " + type + "\n";
            int length = args.length;
            String content = Arrays.asList(args).toString();
            details += "Parameter has a length of " + length + "\n";
            if (length > 0) {
                details += "Parameter content is:\n\t\t" + content;
            }
        }
        return details;
    }
}
