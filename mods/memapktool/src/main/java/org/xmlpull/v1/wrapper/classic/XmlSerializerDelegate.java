/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)
package org.xmlpull.v1.wrapper.classic;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.xmlpull.v1.XmlSerializer;

/**
 * This is simple class that implements serializer interface by delegating
 * all calls to actual serialzier implementation passed in constructor.
 * Purpose of this class is to work as base class to allow extending interface
 * by wrapping exsiting parser implementation and allowing ot add new methods.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class XmlSerializerDelegate implements XmlSerializer {

    protected XmlSerializer xs;

    public XmlSerializerDelegate(XmlSerializer serializer) {

        this.xs = serializer;
    }

    public String getName() {

        return xs.getName();
    }

    public void setPrefix( String prefix, String namespace ) throws IOException, IllegalArgumentException, IllegalStateException {

        xs.setPrefix( prefix, namespace );
    }

    public void setOutput( OutputStream os, String encoding ) throws IOException, IllegalArgumentException, IllegalStateException {

        xs.setOutput( os, encoding );
    }

    public void endDocument() throws IOException, IllegalArgumentException, IllegalStateException {

        xs.endDocument();
    }

    public void comment( String text ) throws IOException, IllegalArgumentException, IllegalStateException {

        xs.comment( text );
    }

    public int getDepth() {

        return xs.getDepth();
    }

    public void setProperty( String name, Object value ) throws IllegalArgumentException, IllegalStateException {

        xs.setProperty( name, value );
    }

    public void cdsect( String text ) throws IOException, IllegalArgumentException, IllegalStateException {

        xs.cdsect( text );
    }

    public void setFeature( String name, boolean state ) throws IllegalArgumentException, IllegalStateException {

        xs.setFeature( name, state );
    }

    public void entityRef( String text ) throws IOException, IllegalArgumentException, IllegalStateException {

        xs.entityRef( text );
    }

    public void processingInstruction( String text ) throws IOException, IllegalArgumentException, IllegalStateException {

        xs.processingInstruction( text );
    }

    public void setOutput( Writer writer ) throws IOException, IllegalArgumentException, IllegalStateException {

        xs.setOutput( writer );
    }

    public void docdecl( String text ) throws IOException, IllegalArgumentException, IllegalStateException {

        xs.docdecl( text );
    }

    public void flush() throws IOException {

        xs.flush();
    }

    public Object getProperty( String name ) {

        return xs.getProperty( name );
    }

    public XmlSerializer startTag( String namespace, String name ) throws IOException, IllegalArgumentException, IllegalStateException {

        return xs.startTag( namespace, name );
    }

    public void ignorableWhitespace( String text ) throws IOException, IllegalArgumentException, IllegalStateException {

        xs.ignorableWhitespace( text );
    }

    public XmlSerializer text( String text ) throws IOException, IllegalArgumentException, IllegalStateException {

        return xs.text( text );
    }

    public boolean getFeature( String name ) {

        return xs.getFeature( name );
    }

    public XmlSerializer attribute( String namespace, String name, String value ) throws IOException, IllegalArgumentException, IllegalStateException {

        return xs.attribute( namespace, name, value );
    }

    public void startDocument( String encoding, Boolean standalone ) throws IOException, IllegalArgumentException, IllegalStateException {

        xs.startDocument( encoding, standalone );
    }

    public String getPrefix( String namespace, boolean generatePrefix ) throws IllegalArgumentException {

        return xs.getPrefix( namespace, generatePrefix );
    }

    public String getNamespace() {

        return xs.getNamespace();
    }

    public XmlSerializer endTag( String namespace, String name ) throws IOException, IllegalArgumentException, IllegalStateException {

        return xs.endTag( namespace, name );
    }

    public XmlSerializer text( char[] buf, int start, int len ) throws IOException, IllegalArgumentException, IllegalStateException {

        return xs.text( buf, start, len );
    }
}
