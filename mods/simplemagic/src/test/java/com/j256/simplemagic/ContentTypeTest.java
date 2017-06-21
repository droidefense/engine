package com.j256.simplemagic;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ContentTypeTest {

    @Test
    public void testPrintDuplicates() {
        Map<String, ContentType> mimeMap = new HashMap<String, ContentType>();
        for (ContentType type : ContentType.values()) {
            if (type.getMimeType() == null) {
                continue;
            }
            ContentType match = mimeMap.get(type.getMimeType());
            if (match == null) {
                mimeMap.put(type.getMimeType(), type);
            } else {
                System.out.println(type + " has duplicate mime-type '" + type.getMimeType() + "' as " + match);
            }
        }

        Map<String, ContentType> extMap = new HashMap<String, ContentType>();
        for (ContentType type : ContentType.values()) {
            for (String ext : type.getFileExtensions()) {
                ContentType match = extMap.get(ext);
                if (match == null) {
                    extMap.put(ext, type);
                } else {
                    System.out.println(type + " has duplicate extension '" + ext + "' as " + match);
                }
            }
        }
    }

    @Test
    public void testFileExtensions() {
        assertEquals(ContentType.GIF, ContentType.fromFileExtension("gif"));
        assertEquals(ContentType.OTHER, ContentType.fromFileExtension("xyzzy"));
    }
}
