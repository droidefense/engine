/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)
package org.xmlpull.v1.wrapper.classic;

import org.xmlpull.v1.CharacterEncodings;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * This is simple class that implements parser interface by delegating
 * all calls to actual wrapped class implementation that is passed in constructor.
 * Purpose of this class is to work as a base class when extending parser interface
 * by wrapping exsiting parser implementation and allowing to add new methods.
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */
public class XmlPullParserDelegate implements XmlPullParser {

    protected XmlPullParser pp;

    public XmlPullParserDelegate(XmlPullParser pp) {

        this.pp = pp;
    }

    public String getText() {

        return pp.getText();
    }

    public void setFeature(String name, boolean state) throws XmlPullParserException {

        pp.setFeature(name, state);
    }

    public char[] getTextCharacters(int[] holderForStartAndLength) {

        return pp.getTextCharacters(holderForStartAndLength);
    }

    public int getColumnNumber() {

        return pp.getColumnNumber();
    }

    public int getNamespaceCount(int depth) throws XmlPullParserException {

        return pp.getNamespaceCount(depth);
    }

    public String getNamespacePrefix(int pos) throws XmlPullParserException {

        return pp.getNamespacePrefix(pos);
    }

    public String getAttributeName(int index) {

        return pp.getAttributeName(index);
    }

    public String getName() {

        return pp.getName();
    }

    public boolean getFeature(String name) {

        return pp.getFeature(name);
    }

    public String getInputEncoding() {

        return pp.getInputEncoding();
    }

    public String getAttributeValue(int index) {

        return pp.getAttributeValue(index);
    }

    public String getNamespace(String prefix) {

        return pp.getNamespace(prefix);
    }

    public void setInput(Reader in) throws XmlPullParserException {

        pp.setInput(in);
    }

    public int getLineNumber() {

        return pp.getLineNumber();
    }

    public Object getProperty(String name) {

        return pp.getProperty(name);
    }

    public boolean isEmptyElementTag() throws XmlPullParserException {

        return pp.isEmptyElementTag();
    }

    public boolean isAttributeDefault(int index) {

        return pp.isAttributeDefault(index);
    }

    public String getNamespaceUri(int pos) throws XmlPullParserException {

        return pp.getNamespaceUri(pos);
    }

    public int next() throws XmlPullParserException, IOException {

        return pp.next();
    }

    public int nextToken() throws XmlPullParserException, IOException {

        return pp.nextToken();
    }

    public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {

        pp.defineEntityReplacementText(entityName, replacementText);
    }

    public int getAttributeCount() {

        return pp.getAttributeCount();
    }

    public boolean isWhitespace() throws XmlPullParserException {

        return pp.isWhitespace();
    }

    public String getPrefix() {

        return pp.getPrefix();
    }

    public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {

        pp.require(type, namespace, name);
    }

    public String nextText() throws XmlPullParserException, IOException {

        return pp.nextText();
    }

    public String getAttributeType(int index) {

        return pp.getAttributeType(index);
    }

    public int getDepth() {

        return pp.getDepth();
    }

    public int nextTag() throws XmlPullParserException, IOException {

        return pp.nextTag();
    }

    public int getEventType() throws XmlPullParserException {

        return pp.getEventType();
    }

    public String getAttributePrefix(int index) {

        return pp.getAttributePrefix(index);
    }

    public void setInput(InputStream inputStream) throws XmlPullParserException {

        setInput(inputStream, CharacterEncodings.UTF_8);
    }

    public void setInput(InputStream inputStream, String inputEncoding) throws XmlPullParserException {

        pp.setInput(inputStream, inputEncoding);
    }

    public String getAttributeValue(String namespace, String name) {

        return pp.getAttributeValue(namespace, name);
    }

    public void setProperty(String name, Object value) throws XmlPullParserException {

        pp.setProperty(name, value);
    }

    public String getPositionDescription() {

        return pp.getPositionDescription();
    }

    public String getNamespace() {

        return pp.getNamespace();
    }

    public String getAttributeNamespace(int index) {

        return pp.getAttributeNamespace(index);
    }
}
