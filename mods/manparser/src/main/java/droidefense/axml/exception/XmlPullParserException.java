package droidefense.axml.exception;

import droidefense.axml.base.XmlPullParser;

import java.io.PrintStream;

public final class XmlPullParserException extends Exception {

    private Throwable detail;
    private int row = -1;
    private int column = -1;

    public XmlPullParserException(String s) {
        super(s);
    }

    public XmlPullParserException(String msg, XmlPullParser parser, Throwable chain) {
        super((msg == null ? "" : msg + " ") + (parser == null ? "" : "(position:" + parser.getPositionDescription() + ") ") + (chain == null ? "" : "caused by: " + chain));
        if (parser != null) {
            this.row = parser.getLineNumber();
            this.column = parser.getColumnNumber();
        }

        this.detail = chain;
    }

    public Throwable getDetail() {
        return this.detail;
    }

    public int getLineNumber() {
        return this.row;
    }

    public int getColumnNumber() {
        return this.column;
    }

    public void printStackTrace() {
        if (this.detail == null) {
            super.printStackTrace();
        } else {
            PrintStream var1 = System.err;
            synchronized (System.err) {
                System.err.println(super.getMessage() + "; nested exception is:");
                this.detail.printStackTrace();
            }
        }

    }
}
