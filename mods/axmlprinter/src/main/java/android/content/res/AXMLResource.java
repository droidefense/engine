/* 
 * Copyright 2015 Red Naga
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.content.res;

import android.content.res.chunk.ChunkType;
import android.content.res.chunk.ChunkUtil;
import android.content.res.chunk.sections.ResourceSection;
import android.content.res.chunk.sections.StringSection;
import android.content.res.chunk.types.AXMLHeader;
import android.content.res.chunk.types.Attribute;
import android.content.res.chunk.types.Chunk;
import android.content.res.chunk.types.StartTag;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Main AXMLResource object
 *
 * @author tstrazzere
 */
public class AXMLResource {

    AXMLHeader header;
    StringSection stringSection;
    ResourceSection resourceSection;
    LinkedHashSet<Chunk> chunks;

    public AXMLResource() {
        chunks = new LinkedHashSet<Chunk>();
    }

    public AXMLResource(InputStream stream) throws IOException {
        chunks = new LinkedHashSet<Chunk>();
        if (!read(stream)) {
            throw new IOException();
        }
    }

    public void injectApplicationAttribute(Attribute attribute) {
        StartTag tag = getApplicationTag();

        tag.insertOrReplaceAttribute(attribute);
    }

    public StartTag getApplicationTag() {
        Iterator<Chunk> iterator = chunks.iterator();
        while (iterator.hasNext()) {
            Chunk chunk = iterator.next();
            if (chunk instanceof StartTag &&
                    ((StartTag) chunk).getName(stringSection).equalsIgnoreCase("application")) {
                return (StartTag) chunk;
            }
        }

        return null;
    }

    public StringSection getStringSection() {
        return stringSection;
    }

    public boolean read(InputStream stream) throws IOException {

        IntReader reader = new IntReader(stream, false);

        // Get an attempted size until we know the read size
        int size = stream.available();

        while ((size - reader.getBytesRead()) > 4) {
            // This should just read all the chunks
            Chunk chunk = ChunkUtil.createChunk(reader);

            switch (chunk.getChunkType()) {
                case AXML_HEADER:
                    header = (AXMLHeader) chunk;
                    // TODO : This should warn if true
                    // This will cause breakages if the header is lying
                    //size = header.getSize();
                    break;
                case STRING_SECTION:
                    stringSection = (StringSection) chunk;
                    break;
                // operational = true;
                case RESOURCE_SECTION:
                    resourceSection = (ResourceSection) chunk;
                    break;
                case START_NAMESPACE:
                case END_NAMESPACE:
                case START_TAG:
                case END_TAG:
                case TEXT_TAG:
                    chunks.add(chunk);
                    break;
                case BUFFER:
                    // Do nothing right now, not even add it to the chunk stuff
                    break;
                default:
                    throw new IOException("Hit an unknown chunk type!");
            }
        }

        if ((header != null) && (stringSection != null) && (resourceSection != null)) {
            if (header.getSize() != reader.getBytesRead()) {
                System.out.println("Potential issue as the bytes read is not equal to the amount of bytes in the file");
            }
            return true;
        }

        return false;
    }

    public void write(OutputStream outputStream) throws IOException {

        int chunkSizes = 0;
        Iterator<Chunk> iterator = chunks.iterator();
        while (iterator.hasNext()) {
            Chunk chunk = iterator.next();
            chunkSizes += chunk.getSize();
        }

        System.out.println("String section size: " + stringSection.getSize());
        System.out.println("Resource section size: " + resourceSection.getSize());
        System.out.println("chunk section size: " + chunkSizes);
        System.out.println("Size was " + ((2 * 4) + stringSection.getSize() + resourceSection.getSize() + chunkSizes));

        outputStream.write(ByteBuffer.allocate(8)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(ChunkType.AXML_HEADER.getIntType())
                .putInt(((2 * 4) + stringSection.getSize() + resourceSection.getSize() + chunkSizes))
                .array());
        outputStream.write(stringSection.toBytes());
        outputStream.write(resourceSection.toBytes());
        iterator = chunks.iterator();
        while (iterator.hasNext()) {
            Chunk chunk = iterator.next();
            outputStream.write(chunk.toBytes());
        }

    }

    public void print() {

        log("%s", header.toXML(stringSection, resourceSection, 0));
        Iterator<Chunk> iterator = chunks.iterator();
        int indents = 0;
        List<String> namespaceXmlList = new ArrayList<String>();

        while (iterator.hasNext()) {
            Chunk chunk = iterator.next();
            if (chunk.getChunkType() == ChunkType.END_TAG) {
                indents--;
            }

            if (chunk.getChunkType() == ChunkType.START_NAMESPACE) {
                namespaceXmlList.add(chunk.toXML(stringSection, resourceSection, indents));
            } else if (chunk.getChunkType() == ChunkType.END_NAMESPACE) {
                // ignore
            } else {
                if (namespaceXmlList.isEmpty()) {
                    log("%s", chunk.toXML(stringSection, resourceSection, indents));
                } else {
                    log("%s", appendNameSpace(chunk.toXML(stringSection, resourceSection, indents), namespaceXmlList));
                    namespaceXmlList.clear();
                }
            }

            if (chunk.getChunkType() == ChunkType.START_TAG) {
                indents++;
            }
        }

    }

    public String toXML() {
        StringBuilder xmlStrbui = new StringBuilder();
        xmlStrbui.append(header.toXML(stringSection, resourceSection, 0)).append('\n');
        Iterator<Chunk> iterator = chunks.iterator();
        int indents = 0;
        List<String> namespaceXmlList = new ArrayList<String>();

        while (iterator.hasNext()) {
            Chunk chunk = iterator.next();
            if (chunk.getChunkType() == ChunkType.END_TAG) {
                indents--;
            }

            if (chunk.getChunkType() == ChunkType.START_NAMESPACE) {
                namespaceXmlList.add(chunk.toXML(stringSection, resourceSection, indents));
            } else if (chunk.getChunkType() == ChunkType.END_NAMESPACE) {
                // ignore
            } else {
                if (namespaceXmlList.isEmpty()) {
                    xmlStrbui.append(chunk.toXML(stringSection, resourceSection, indents)).append('\n');
                } else {
                    xmlStrbui.append(appendNameSpace(chunk.toXML(stringSection, resourceSection, indents), namespaceXmlList)).append('\n');
                    namespaceXmlList.clear();
                }
            }

            if (chunk.getChunkType() == ChunkType.START_TAG) {
                indents++;
            }
        }
        return xmlStrbui.toString();
    }

    private String appendNameSpace(String preChunkXml, List<String> namespaceXmlList) {
        StringBuilder strbui = new StringBuilder(preChunkXml);
        int index;
        if ((index = strbui.indexOf("\n")) == -1 && (index = strbui.indexOf(" ")) == -1
                && (index = strbui.indexOf("/>")) == -1 && (index = strbui.indexOf(">")) == -1) {
            throw new RuntimeException("Append name space fail. chunk xml: " + preChunkXml);
        }
        StringBuilder namespaceStrbui = new StringBuilder();
        for (String namespaceXml : namespaceXmlList) {
            namespaceStrbui.append("\n\t");
            namespaceStrbui.append(namespaceXml);
        }
        strbui.insert(index, namespaceStrbui.toString());
        return strbui.toString();
    }

    private static void log(String format, Object... arguments) {
        System.out.printf(format, arguments);
        System.out.println();
    }
}
