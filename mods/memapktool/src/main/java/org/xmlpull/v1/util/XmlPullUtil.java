/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)
package org.xmlpull.v1.util;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/**
 * Handy functions that combines XmlPull API into higher level functionality.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 * @author Naresh Bhatia
 */
public class XmlPullUtil {

    public static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";

    private XmlPullUtil() {

    }

    /**
     * Return value of attribute with given name and no namespace.
     */
    public static String getAttributeValue( XmlPullParser pp, String name ) {

        return pp.getAttributeValue( XmlPullParser.NO_NAMESPACE, name );
    }

    /**
     * Return PITarget from Processing Instruction (PI) as defined in
     * XML 1.0 Section 2.6 Processing Instructions
     *  <code>[16] PI ::= '&lt;?' PITarget (S (Char* - (Char* '?>' Char*)))? '?>'</code>
     */
    public static String getPITarget( XmlPullParser pp ) throws IllegalStateException {

        int eventType;
        try {
            eventType = pp.getEventType();
        }
        catch (XmlPullParserException ex) {
            // should never happen ...
            throw new IllegalStateException( "could not determine parser state: " + ex + pp.getPositionDescription() );
        }
        if (eventType != XmlPullParser.PROCESSING_INSTRUCTION) {
            throw new IllegalStateException( "parser must be on processing instruction and not " + XmlPullParser.TYPES[eventType] + pp.getPositionDescription() );
        }
        final String PI = pp.getText();
        for (int i = 0; i < PI.length(); i++) {
            if (isS( PI.charAt( i ) )) {
                // assert i > 0
                return PI.substring( 0, i );
            }
        }
        return PI;
    }

    /**
     * Return everything past PITarget and S from Processing Instruction (PI) as defined in
     * XML 1.0 Section 2.6 Processing Instructions
     *  <code>[16] PI ::= '&lt;?' PITarget (S (Char* - (Char* '?>' Char*)))? '?>'</code>
     *
     * <p><b>NOTE:</b> if there is no PI data it returns empty string.
     */
    public static String getPIData( XmlPullParser pp ) throws IllegalStateException {

        int eventType;
        try {
            eventType = pp.getEventType();
        }
        catch (XmlPullParserException ex) {
            // should never happen ...
            throw new IllegalStateException( "could not determine parser state: " + ex + pp.getPositionDescription() );
        }
        if (eventType != XmlPullParser.PROCESSING_INSTRUCTION) {
            throw new IllegalStateException( "parser must be on processing instruction and not " + XmlPullParser.TYPES[eventType] + pp.getPositionDescription() );
        }
        final String PI = pp.getText();
        int pos = -1;
        for (int i = 0; i < PI.length(); i++) {
            if (isS( PI.charAt( i ) )) {
                pos = i;
            }
            else if (pos > 0) {
                return PI.substring( i );
            }
        }
        return "";

    }

    /**
     * Return true if chacters is S as defined in XML 1.0
     * <code>S ::=  (#x20 | #x9 | #xD | #xA)+</code>
     */
    private static boolean isS( char ch ) {

        return ( ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t' );
    }

    /**
     * Skip sub tree that is currently porser positioned on.
     * <br>NOTE: parser must be on START_TAG and when funtion returns
     * parser will be positioned on corresponding END_TAG
     */
    public static void skipSubTree( XmlPullParser pp ) throws XmlPullParserException, IOException {

        pp.require( XmlPullParser.START_TAG, null, null );
        int level = 1;
        while (level > 0) {
            int eventType = pp.next();
            if (eventType == XmlPullParser.END_TAG) {
                --level;
            }
            else if (eventType == XmlPullParser.START_TAG) {
                ++level;
            }
        }
    }

    /**
     * call parser nextTag() and check that it is START_TAG, throw exception if not.
     */
    public static void nextStartTag( XmlPullParser pp ) throws XmlPullParserException, IOException {

        if (pp.nextTag() != XmlPullParser.START_TAG) {
            throw new XmlPullParserException( "expected START_TAG and not " + pp.getPositionDescription() );
        }
    }

    /**
     * combine nextTag(); pp.require(XmlPullParser.START_TAG, null, name);
     */
    public static void nextStartTag( XmlPullParser pp, String name ) throws XmlPullParserException, IOException {

        pp.nextTag();
        pp.require( XmlPullParser.START_TAG, null, name );
    }

    /**
     * combine nextTag(); pp.require(XmlPullParser.START_TAG, namespace, name);
     */
    public static void nextStartTag( XmlPullParser pp, String namespace, String name ) throws XmlPullParserException, IOException {

        pp.nextTag();
        pp.require( XmlPullParser.START_TAG, namespace, name );
    }

    /**
     * combine nextTag(); pp.require(XmlPullParser.END_TAG, namespace, name);
     */
    public static void nextEndTag( XmlPullParser pp, String namespace, String name ) throws XmlPullParserException, IOException {

        pp.nextTag();
        pp.require( XmlPullParser.END_TAG, namespace, name );
    }

    /**
     * Read text content of element ith given namespace and name
     * (use null namespace do indicate that nemspace should not be checked)
     */

    public static String nextText( XmlPullParser pp, String namespace, String name ) throws IOException, XmlPullParserException {

        if (name == null) {
            throw new XmlPullParserException( "name for element can not be null" );
        }
        pp.require( XmlPullParser.START_TAG, namespace, name );
        return pp.nextText();
    }

    /**
     * Read attribute value and return it or throw exception if
     * current element does not have such attribute.
     */

    public static String getRequiredAttributeValue( XmlPullParser pp, String namespace, String name ) throws IOException, XmlPullParserException {

        String value = pp.getAttributeValue( namespace, name );
        if (value == null) {
            throw new XmlPullParserException( "required attribute " + name + " is not present" );
        }
        else {
            return value;
        }
    }

    /**
     * Call parser nextTag() and check that it is END_TAG, throw exception if not.
     */
    public static void nextEndTag( XmlPullParser pp ) throws XmlPullParserException, IOException {

        if (pp.nextTag() != XmlPullParser.END_TAG) {
            throw new XmlPullParserException( "expected END_TAG and not" + pp.getPositionDescription() );
        }
    }

    /**
     * Tests if the current event is of the given type and if the namespace and name match.
     * null will match any namespace and any name. If the test passes a true is returned
     * otherwise a false is returned.
     */
    public static boolean matches( XmlPullParser pp, int type, String namespace, String name ) throws XmlPullParserException {

        boolean matches = type == pp.getEventType() && ( namespace == null || namespace.equals( pp.getNamespace() ) ) && ( name == null || name.equals( pp.getName() ) );

        return matches;
    }

    /**
     * Writes a simple element such as <username>johndoe</username>. The namespace
     * and elementText are allowed to be null. If elementText is null, an xsi:nil="true"
     * will be added as an attribute.
     */
    public static void writeSimpleElement( XmlSerializer serializer, String namespace, String elementName, String elementText ) throws IOException, XmlPullParserException {

        if (elementName == null) {
            throw new XmlPullParserException( "name for element can not be null" );
        }

        serializer.startTag( namespace, elementName );
        if (elementText == null) {
            serializer.attribute( XSI_NS, "nil", "true" );
        }
        else {
            serializer.text( elementText );
        }
        serializer.endTag( namespace, elementName );
    }

}
