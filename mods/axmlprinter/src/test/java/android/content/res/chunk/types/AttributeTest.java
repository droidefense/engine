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
package android.content.res.chunk.types;

import android.content.res.IntReader;
import junit.framework.TestCase;
import org.junit.Assert;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author tstrazzere
 */
public class AttributeTest extends TestCase {

    private Attribute underTest;

    private IntReader mockReader;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        mockReader = mock(IntReader.class);
        // Mock the attribute data
        when(mockReader.readInt()).thenReturn(0x35, 0x60, 0x10, 0x3000008, 0x2C);

        underTest = new Attribute(mockReader);
    }

    public void testToBytes() throws Exception {
        byte[] expected = {
                // uri
                (byte) 0x35, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // name
                (byte) 0x60, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // string data
                (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // type
                (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x03,
                // data
                (byte) 0x2C, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };

        byte[] actual = underTest.toBytes();
        Assert.assertArrayEquals(expected, actual);
    }
}