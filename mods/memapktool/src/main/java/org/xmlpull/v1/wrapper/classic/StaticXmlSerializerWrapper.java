/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)
package org.xmlpull.v1.wrapper.classic;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
import org.xmlpull.v1.wrapper.XmlPullParserWrapper;
import org.xmlpull.v1.wrapper.XmlPullWrapperFactory;
import org.xmlpull.v1.wrapper.XmlSerializerWrapper;

/**
 * This class seemlesly extends exisiting serialzier implementation by adding new methods
 * (provided by XmlPullUtil) and delegating exisiting methods to parser implementation.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 * @author Naresh Bhatia
 */
public class StaticXmlSerializerWrapper extends XmlSerializerDelegate implements XmlSerializerWrapper {

    private final static String PROPERTY_XMLDECL_STANDALONE = "http://xmlpull.org/v1/doc/features.html#xmldecl-standalone";
    private static final boolean TRACE_SIZING = false;
    protected String currentNs;
    protected XmlPullWrapperFactory wf;
    protected XmlPullParserWrapper fragmentParser;

    public StaticXmlSerializerWrapper(XmlSerializer xs, XmlPullWrapperFactory wf) {

        super( xs );
        this.wf = wf;
    }

    public String getCurrentNamespaceForElements() {

        return currentNs;
    }

    public String setCurrentNamespaceForElements( String value ) {

        String old = currentNs;
        currentNs = value;
        return old;
    }

    public XmlSerializerWrapper attribute( String name, String value ) throws IOException, IllegalArgumentException, IllegalStateException {

        xs.attribute( null, name, value );
        return this;
    }

    public XmlSerializerWrapper startTag( String name ) throws IOException, IllegalArgumentException, IllegalStateException {

        xs.startTag( currentNs, name );
        return this;
    }

    public XmlSerializerWrapper endTag( String name ) throws IOException, IllegalArgumentException, IllegalStateException {

        endTag( currentNs, name );
        return this;
    }

    /** Write simple text element in current namespace */
    public XmlSerializerWrapper element( String elementName, String elementText ) throws IOException, XmlPullParserException {

        return element( currentNs, elementName, elementText );
    }

    public XmlSerializerWrapper element( String namespace, String elementName, String elementText ) throws IOException, XmlPullParserException {

        if ( elementName == null ) {
            throw new XmlPullParserException( "name for element can not be null" );
        }

        xs.startTag( namespace, elementName );
        if ( elementText == null ) {
            xs.attribute( XSI_NS, "nil", "true" );
        }
        else {
            xs.text( elementText );
        }
        xs.endTag( namespace, elementName );
        return this;
    }

    //namespace stack
    //protected int elNamespaceCount[] = new int[ 2 ];
    //protected int currentDepth = -1;

    protected int namespaceEnd = 0;
    protected String namespacePrefix[] = new String[8];
    protected String namespaceUri[] = new String[namespacePrefix.length];
    protected int namespaceDepth[] = new int[namespacePrefix.length];

    private void ensureNamespacesCapacity() {

        int newSize = namespaceEnd > 7 ? 2 * namespaceEnd : 8;
        if ( TRACE_SIZING ) {
            System.err.println( getClass().getName() + " namespaceSize " + namespacePrefix.length + " ==> " + newSize );
        }
        String[] newNamespacePrefix = new String[newSize];
        String[] newNamespaceUri = new String[newSize];
        int[] newNamespaceDepth = new int[newSize];
        if ( namespacePrefix != null ) {
            System.arraycopy( namespacePrefix, 0, newNamespacePrefix, 0, namespaceEnd );
            System.arraycopy( namespaceUri, 0, newNamespaceUri, 0, namespaceEnd );
            System.arraycopy( namespaceDepth, 0, newNamespaceDepth, 0, namespaceEnd );
        }
        namespacePrefix = newNamespacePrefix;
        namespaceUri = newNamespaceUri;
        namespaceDepth = newNamespaceDepth;
    }

    @Override
    public void setPrefix( String prefix, String namespace ) throws IOException, IllegalArgumentException, IllegalStateException {

        xs.setPrefix( prefix, namespace );

        int depth = getDepth();
        for (int pos = namespaceEnd - 1; pos >= 0; --pos) {
            if ( namespaceDepth[pos] <= depth ) {
                break;
            }
            --namespaceEnd;
        }

        if ( namespaceEnd >= namespacePrefix.length ) {
            ensureNamespacesCapacity();
        }
        namespacePrefix[namespaceEnd] = prefix;
        namespaceUri[namespaceEnd] = namespace;
        ++namespaceEnd;

    }

    public void fragment( String xmlFragment ) throws IOException, IllegalArgumentException, IllegalStateException, XmlPullParserException {

        StringBuffer buf = new StringBuffer( xmlFragment.length() + namespaceEnd * 30 );
        buf.append( "<fragment" );
        LOOP: for (int pos = namespaceEnd - 1; pos >= 0; --pos) {
            String prefix = namespacePrefix[pos];
            for (int i = namespaceEnd - 1; i > pos; --i) {
                if ( prefix.equals( namespacePrefix[i] ) ) {
                    continue LOOP;
                }
            }
            buf.append( " xmlns" );
            if ( prefix.length() > 0 ) {
                buf.append( ':' ).append( prefix );
            }
            buf.append( "='" );
            buf.append( escapeAttributeValue( namespaceUri[pos] ) );
            buf.append( "'" );
        }

        buf.append( ">" );
        buf.append( xmlFragment );
        buf.append( "</fragment>" );

        if ( fragmentParser == null ) {
            fragmentParser = wf.newPullParserWrapper();
        }
        String s = buf.toString();
        //System.err.println(getClass().getName()+" fragment XML="+s);
        fragmentParser.setInput( new StringReader( s ) );
        fragmentParser.nextTag();
        fragmentParser.require( XmlPullParser.START_TAG, null, "fragment" );
        while (true) {
            fragmentParser.nextToken();
            if ( fragmentParser.getDepth() == 1 && fragmentParser.getEventType() == XmlPullParser.END_TAG ) {
                break;
            }
            event( fragmentParser );
        }
        fragmentParser.require( XmlPullParser.END_TAG, null, "fragment" );
    }

    public void event( XmlPullParser pp ) throws XmlPullParserException, IOException {

        int eventType = pp.getEventType();
        switch (eventType) {
            case XmlPullParser.START_DOCUMENT:
                //use Boolean.TRUE to make it standalone
                Boolean standalone = (Boolean) pp.getProperty( PROPERTY_XMLDECL_STANDALONE );
                startDocument( pp.getInputEncoding(), standalone );
                break;

            case XmlPullParser.END_DOCUMENT:
                endDocument();
                break;

            case XmlPullParser.START_TAG:
                writeStartTag( pp );
                break;

            case XmlPullParser.END_TAG:
                endTag( pp.getNamespace(), pp.getName() );
                break;

            case XmlPullParser.IGNORABLE_WHITESPACE:
                //comment it to remove ignorable whtespaces from XML infoset
                String s = pp.getText();
                ignorableWhitespace( s );
                break;

            case XmlPullParser.TEXT:
                if ( pp.getDepth() > 0 ) {
                    text( pp.getText() );
                }
                else {
                    ignorableWhitespace( pp.getText() );
                }
                break;

            case XmlPullParser.ENTITY_REF:
                entityRef( pp.getName() );
                break;

            case XmlPullParser.CDSECT:
                cdsect( pp.getText() );
                break;

            case XmlPullParser.PROCESSING_INSTRUCTION:
                processingInstruction( pp.getText() );
                break;

            case XmlPullParser.COMMENT:
                comment( pp.getText() );
                break;

            case XmlPullParser.DOCDECL:
                docdecl( pp.getText() );
                break;
        }
    }

    private void writeStartTag( XmlPullParser pp ) throws XmlPullParserException, IOException {

        if ( !pp.getFeature( XmlPullParser.FEATURE_REPORT_NAMESPACE_ATTRIBUTES ) ) {
            int nsStart = pp.getNamespaceCount( pp.getDepth() - 1 );
            int nsEnd = pp.getNamespaceCount( pp.getDepth() );
            for (int i = nsStart; i < nsEnd; i++) {
                String prefix = pp.getNamespacePrefix( i );
                String ns = pp.getNamespaceUri( i );
                setPrefix( prefix, ns );
            }
        }
        startTag( pp.getNamespace(), pp.getName() );

        for (int i = 0; i < pp.getAttributeCount(); i++) {
            attribute( pp.getAttributeNamespace( i ), pp.getAttributeName( i ), pp.getAttributeValue( i ) );
        }
        //ser.closeStartTag();
    }

    public String escapeAttributeValue( String value ) {

        int posLt = value.indexOf( '<' );
        int posAmp = value.indexOf( '&' );
        int posQuot = value.indexOf( '"' );
        int posApos = value.indexOf( '\'' );
        if ( posLt == -1 && posAmp == -1 && posQuot == -1 && posApos == -1 ) {
            return value;
        }
        StringBuffer buf = new StringBuffer( value.length() + 10 );

        // painful loop ...
        for (int pos = 0, len = value.length(); pos < len; ++pos) {
            char ch = value.charAt( pos );
            switch (ch) {
                case '<':
                    buf.append( "&lt;" );
                    break;
                case '&':
                    buf.append( "&amp;" );
                    break;
                case '\'':
                    buf.append( "&apos;" );
                    break;
                case '"':
                    buf.append( "&quot;" );
                    break;
                default:
                    buf.append( ch );
            }
        }
        return buf.toString();
    }

    public String escapeText( String text ) {

        //<, & esccaped]
        //out.write(text);
        int posLt = text.indexOf( '<' );
        int posAmp = text.indexOf( '&' );
        if ( posLt == -1 && posAmp == -1 ) { // this is shortcut
            return text;
        }
        StringBuffer buf = new StringBuffer( text.length() + 10 );
        // painful loop ...
        int pos = 0;
        while (true) {
            if ( posLt == -1 && posAmp == -1 ) { // this is shortcut
                buf.append( text.substring( pos ) );
                break;
            }
            else if ( posLt == -1 || ( posLt != -1 && posAmp != -1 && posAmp < posLt ) ) {
                if ( pos < posAmp )
                    buf.append( text.substring( pos, posAmp ) );
                buf.append( "&amp;" );
                pos = posAmp + 1;
                posAmp = text.indexOf( '&', pos );
            }
            else if ( posAmp == -1 || ( posLt != -1 && posAmp != -1 && posLt < posAmp ) ) {
                if ( pos < posLt )
                    buf.append( text.substring( pos, posLt ) );
                buf.append( "&lt;" );
                pos = posLt + 1;
                posLt = text.indexOf( '<', pos );
            }
            else {
                throw new IllegalStateException( "wrong state posLt=" + posLt + " posAmp=" + posAmp + " for " + text );
            }
        }
        return buf.toString();
    }

    public void writeDouble( double d ) throws XmlPullParserException, IOException, IllegalArgumentException {

        if ( d == Double.POSITIVE_INFINITY ) {
            xs.text( "INF" );
        }
        else if ( d == Double.NEGATIVE_INFINITY ) {
            xs.text( "-INF" );
        }
        else {
            xs.text( Double.toString( d ) );
        }
    }

    public void writeFloat( float f ) throws XmlPullParserException, IOException, IllegalArgumentException {

        if ( f == Float.POSITIVE_INFINITY ) {
            xs.text( "INF" );
        }
        else if ( f == Float.NEGATIVE_INFINITY ) {
            xs.text( "-INF" );
        }
        else {
            xs.text( Float.toString( f ) );
        }
    }

    public void writeInt( int i ) throws XmlPullParserException, IOException, IllegalArgumentException {

        xs.text( Integer.toString( i ) );
    }

    public void writeString( String s ) throws XmlPullParserException, IOException, IllegalArgumentException {

        if ( s == null ) {
            throw new IllegalArgumentException( "null string can not be written" );
        }
        xs.text( s );
    }

    public void writeDoubleElement( String namespace, String name, double d ) throws XmlPullParserException, IOException, IllegalArgumentException {

        xs.startTag( namespace, name );
        writeDouble( d );
        xs.endTag( namespace, name );
    }

    public void writeFloatElement( String namespace, String name, float f ) throws XmlPullParserException, IOException, IllegalArgumentException {

        xs.startTag( namespace, name );
        writeFloat( f );
        xs.endTag( namespace, name );
    }

    public void writeIntElement( String namespace, String name, int i ) throws XmlPullParserException, IOException, IllegalArgumentException {

        xs.startTag( namespace, name );
        writeInt( i );
        xs.endTag( namespace, name );
    }

    public void writeStringElement( String namespace, String name, String s ) throws XmlPullParserException, IOException, IllegalArgumentException {

        xs.startTag( namespace, name );
        if ( s == null ) {
            xs.attribute( XSD_NS, "nil", "true" );
        }
        else {
            writeString( s );
        }
        xs.endTag( namespace, name );
    }
}
