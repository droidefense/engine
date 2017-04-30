/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)
package org.xmlpull.v1.wrapper.classic;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.util.XmlPullUtil;
import org.xmlpull.v1.wrapper.XmlPullParserWrapper;

/**
 * This class seemlesly extends exisiting parser implementation by adding new methods
 * (provided by XmlPullUtil) and delegating exisiting methods to parser implementation.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 * @author Naresh Bhatia
 */
public class StaticXmlPullParserWrapper extends XmlPullParserDelegate implements XmlPullParserWrapper {

    public StaticXmlPullParserWrapper(XmlPullParser pp) {

        super( pp );
    }

    public String getAttributeValue( String name ) {

        return XmlPullUtil.getAttributeValue( pp, name );
    }

    public String getRequiredAttributeValue( String name ) throws IOException, XmlPullParserException {

        return XmlPullUtil.getRequiredAttributeValue( pp, null, name );
    }

    public String getRequiredAttributeValue( String namespace, String name ) throws IOException, XmlPullParserException {

        return XmlPullUtil.getRequiredAttributeValue( pp, namespace, name );
    }

    /**
     * Read the text of a required element and return it or throw exception if
     * required element is not found. Useful for getting the text of simple
     * elements such as <username>johndoe</username>. Assumes that parser is
     * just before the start tag and leaves the parser at the end tag. If the
     * text is nil (e.g. <username xsi:nil="true"/>), then a null will be returned.
     */
    public String getRequiredElementText( String namespace, String name ) throws IOException, XmlPullParserException {

        if ( name == null ) {
            throw new XmlPullParserException( "name for element can not be null" );
        }

        String text = null;
        nextStartTag( namespace, name );
        if ( isNil() ) {
            nextEndTag( namespace, name );
        }
        else {
            text = pp.nextText();
        }
        pp.require( XmlPullParser.END_TAG, namespace, name );
        return text;
    }

    public boolean isNil() throws IOException, XmlPullParserException {

        boolean result = false;
        String value = pp.getAttributeValue( XSI_NS, "nil" );
        if ( "true".equals( value ) ) {
            result = true;
        }

        return result;
    }

    public String getPITarget() throws IllegalStateException {

        return XmlPullUtil.getPITarget( pp );
    }

    public String getPIData() throws IllegalStateException {

        return XmlPullUtil.getPIData( pp );
    }

    public boolean matches( int type, String namespace, String name ) throws XmlPullParserException {

        return XmlPullUtil.matches( pp, type, namespace, name );
    }

    public void nextStartTag() throws XmlPullParserException, IOException {

        if ( pp.nextTag() != XmlPullParser.START_TAG ) {
            throw new XmlPullParserException( "expected START_TAG and not " + pp.getPositionDescription() );
        }
    }

    public void nextStartTag( String name ) throws XmlPullParserException, IOException {

        pp.nextTag();
        pp.require( XmlPullParser.START_TAG, null, name );
    }

    public void nextStartTag( String namespace, String name ) throws XmlPullParserException, IOException {

        pp.nextTag();
        pp.require( XmlPullParser.START_TAG, namespace, name );
    }

    public void nextEndTag() throws XmlPullParserException, IOException {

        XmlPullUtil.nextEndTag( pp );
    }

    public void nextEndTag( String name ) throws XmlPullParserException, IOException {

        XmlPullUtil.nextEndTag( pp, null, name );
    }

    public void nextEndTag( String namespace, String name ) throws XmlPullParserException, IOException {

        XmlPullUtil.nextEndTag( pp, namespace, name );
    }

    public String nextText( String namespace, String name ) throws IOException, XmlPullParserException {

        return XmlPullUtil.nextText( pp, namespace, name );
    }

    public void skipSubTree() throws XmlPullParserException, IOException {

        XmlPullUtil.skipSubTree( pp );
    }

    public double readDouble() throws XmlPullParserException, IOException {

        String value = pp.nextText();
        double d;
        try {
            d = Double.parseDouble( value );
        }
        catch (NumberFormatException ex) {
            if ( value.equals( "INF" ) || value.toLowerCase().equals( "infinity" ) ) {
                d = Double.POSITIVE_INFINITY;
            }
            else if ( value.equals( "-INF" ) || value.toLowerCase().equals( "-infinity" ) ) {
                d = Double.NEGATIVE_INFINITY;
            }
            else if ( value.equals( "NaN" ) ) {
                d = Double.NaN;
            }
            else {
                throw new XmlPullParserException( "can't parse double value '" + value + "'", this, ex );
            }
        }
        return d;
    }

    public float readFloat() throws XmlPullParserException, IOException {

        String value = pp.nextText();
        float f;
        try {
            f = Float.parseFloat( value );
        }
        catch (NumberFormatException ex) {
            if ( value.equals( "INF" ) || value.toLowerCase().equals( "infinity" ) ) {
                f = Float.POSITIVE_INFINITY;
            }
            else if ( value.equals( "-INF" ) || value.toLowerCase().equals( "-infinity" ) ) {
                f = Float.NEGATIVE_INFINITY;
            }
            else if ( value.equals( "NaN" ) ) {
                f = Float.NaN;
            }
            else {
                throw new XmlPullParserException( "can't parse float value '" + value + "'", this, ex );
            }
        }
        return f;
    }

    // method copied from JiBX see http://sourceforge.net/projects/jibx/ for details
    private int parseDigits( String text, int offset, int length ) throws XmlPullParserException {

        // check if overflow a potential problem
        int value = 0;
        if ( length > 9 ) {

            // use library parse code for potential overflow
            try {
                value = Integer.parseInt( text.substring( offset, offset + length ) );
            }
            catch (NumberFormatException ex) {
                throw new XmlPullParserException( ex.getMessage() );
            }

        }
        else {

            // parse with no overflow worries
            int limit = offset + length;
            while (offset < limit) {
                char chr = text.charAt( offset++ );
                if ( chr >= '0' && chr <= '9' ) {
                    value = value * 10 + ( chr - '0' );
                }
                else {
                    throw new XmlPullParserException( "non-digit in number value", this, null );
                }
            }

        }
        return value;
    }

    // method copied from JiBX see http://sourceforge.net/projects/jibx/ for details
    private int parseInt( String text ) throws XmlPullParserException {

        // make sure there's text to be processed
        int offset = 0;
        int limit = text.length();
        if ( limit == 0 ) {
            throw new XmlPullParserException( "empty number value", this, null );
        }

        // check leading sign present in text
        boolean negate = false;
        char chr = text.charAt( 0 );
        if ( chr == '-' ) {
            if ( limit > 9 ) {

                // special case to make sure maximum negative value handled
                try {
                    return Integer.parseInt( text );
                }
                catch (NumberFormatException ex) {
                    throw new XmlPullParserException( ex.getMessage(), this, null );
                }

            }
            else {
                negate = true;
                offset++;
            }
        }
        else if ( chr == '+' ) {
            offset++;
        }
        if ( offset >= limit ) {
            throw new XmlPullParserException( "Invalid number format", this, null );
        }

        // handle actual value conversion
        int value = parseDigits( text, offset, limit - offset );
        if ( negate ) {
            return -value;
        }
        else {
            return value;
        }
    }

    public int readInt() throws XmlPullParserException, IOException {

        try {
            //int i = Integer.parseInt(pp.nextText());
            int i = parseInt( pp.nextText() );
            return i;
        }
        catch (NumberFormatException ex) {
            throw new XmlPullParserException( "can't parse int value", this, ex );
        }
    }

    public String readString() throws XmlPullParserException, IOException {

        String xsiNil = pp.getAttributeValue( XSD_NS, "nil" );
        if ( "true".equals( xsiNil ) ) {
            nextEndTag();
            return null;
        }
        return pp.nextText();
    }

    public double readDoubleElement( String namespace, String name ) throws XmlPullParserException, IOException {

        pp.require( XmlPullParser.START_TAG, namespace, name );
        return readDouble();
    }

    public float readFloatElement( String namespace, String name ) throws XmlPullParserException, IOException {

        pp.require( XmlPullParser.START_TAG, namespace, name );
        return readFloat();
    }

    public int readIntElement( String namespace, String name ) throws XmlPullParserException, IOException {

        pp.require( XmlPullParser.START_TAG, namespace, name );
        return readInt();
    }

    public String readStringElemet( String namespace, String name ) throws XmlPullParserException, IOException {

        pp.require( XmlPullParser.START_TAG, namespace, name );
        return readString();
    }
}
