/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)

package org.xmlpull.v1.wrapper;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/**
 * Extensions to XmlSerialzier interface
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 * @author Naresh Bhatia
 */
public interface XmlSerializerWrapper extends XmlSerializer {

    public static final String NO_NAMESPACE = XmlPullParserWrapper.NO_NAMESPACE;
    public static final String XSI_NS = XmlPullParserWrapper.XSI_NS;
    public static final String XSD_NS = XmlPullParserWrapper.XSD_NS;

    /**
     * Get namespace that is used as default when no namespace parameter is used for
     * startTag(), endTag() and element()
     */
    public String getCurrentNamespaceForElements();

    /**
     * Set namespace to use in startTag(), endTag() and element()
     * when methods called are those without namespace parameter.
     */
    public String setCurrentNamespaceForElements( String value );

    /**
     * Write an attribute without namespace.
     * Calls to attribute() MUST follow a call to
     * startTag() immediately. If there is no prefix defined for the
     * given namespace, a prefix will be defined automatically.
     * NOTE: current element namespace is not used attribute and attributre has no namespace.
     */
    public XmlSerializerWrapper attribute( String name, String value ) throws IOException, IllegalArgumentException, IllegalStateException;

    /** Write start tag in current namespace with name given as argument. */
    public XmlSerializerWrapper startTag( String name ) throws IOException, IllegalArgumentException, IllegalStateException;

    /** Write end tag in current namespace with name given as argument. */
    public XmlSerializerWrapper endTag( String name ) throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * Writes a simple element such as &lt;username>johndoe&lt;/username>. The namespace
     * and elementText are allowed to be null. If elementText is null, an xsi:nil="true"
     * will be added as an attribute.
     */
    public XmlSerializerWrapper element( String namespace, String elementName, String elementText ) throws IOException, XmlPullParserException;

    /** Write simple text element in current namespace */
    public XmlSerializerWrapper element( String elementName, String elementText ) throws IOException, XmlPullParserException;

    /** Write XML fragment using currently set namespace prefixes */
    public void fragment( String xmlFragment ) throws IOException, IllegalArgumentException, IllegalStateException, XmlPullParserException;

    /** Serializer current event form pull parser */
    public void event( XmlPullParser pp ) throws IOException, IllegalArgumentException, IllegalStateException, XmlPullParserException;

    public String escapeText( String text ) throws IllegalArgumentException;

    public String escapeAttributeValue( String text ) throws IllegalArgumentException;
}
