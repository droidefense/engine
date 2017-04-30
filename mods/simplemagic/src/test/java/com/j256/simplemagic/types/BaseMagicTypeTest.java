package com.j256.simplemagic.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.j256.simplemagic.ContentInfoUtil.ErrorCallBack;

/**
 * Portions from rob stryker via github. Thanks dude.
 * 
 * @author graywatson, robstryker
 */
public abstract class BaseMagicTypeTest {

	protected byte[] hexToBytes(String str) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i = 0; i < str.length(); i += 2) {
			baos.write(Integer.decode("0x" + str.charAt(i) + str.charAt(i + 1)));
		}
		return baos.toByteArray();
	}

	protected byte[] byteArraysCombine(byte[]... arrays) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (byte[] array : arrays) {
			baos.write(array, 0, array.length);
		}
		return baos.toByteArray();
	}

	protected void testOutput(String magic, byte[] bytes, String expectedMsg) throws IOException {
		ContentInfoUtil contentInfoUtil = new ContentInfoUtil(new StringReader(magic), new ErrorCallBack() {
			@Override
			public void error(String line, String details, Exception e) {
				throw new RuntimeException("Problems compiling magic: '" + details + "', on line: " + line, e);
			}
		});
		ContentInfo result = contentInfoUtil.findMatch(bytes);
		if (expectedMsg == null) {
			assertNull("Got result instead of null: " + result, result);
		} else {
			assertNotNull("Got null result instead of: " + expectedMsg, result);
			assertEquals(expectedMsg, result.getMessage());
		}
	}
}
