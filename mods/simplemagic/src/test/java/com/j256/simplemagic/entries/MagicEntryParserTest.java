package com.j256.simplemagic.entries;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.j256.simplemagic.ContentInfoUtil.ErrorCallBack;

public class MagicEntryParserTest {

	@Test
	public void testCoverage() {
		// no previous line
		assertNull(MagicEntryParser.parseLine(null, "!:stuff", null));
		// no non-whitespace
		assertNull(MagicEntryParser.parseLine(null, "            ", null));
		// 0 level
		assertNull(MagicEntryParser.parseLine(null, ">0   ", null));

		// no whitespace
		assertNull(MagicEntryParser.parseLine(null, "100", null));
		// no whitespace, with error
		LocalErrorCallBack error = new LocalErrorCallBack();
		assertNull(MagicEntryParser.parseLine(null, "100", error));
		assertNotNull(error.details);

		// no pattern
		assertNull(MagicEntryParser.parseLine(null, ">1   ", null));
		assertNull(MagicEntryParser.parseLine(null, ">1   ", error));
		assertNotNull(error.details);

		// no type
		assertNull(MagicEntryParser.parseLine(null, ">1   wow", null));
		assertNull(MagicEntryParser.parseLine(null, ">1   wow", error));
		assertNotNull(error.details);

		// no value
		assertNull(MagicEntryParser.parseLine(null, ">1   wow     ", null));
		assertNull(MagicEntryParser.parseLine(null, ">1   wow     ", error));
		assertNotNull(error.details);
	}

	@Test
	public void testBadLevel() {
		// no level number
		assertNull(MagicEntryParser.parseLine(null, ">   string   SONG   Format", null));
		// no level number with error
		LocalErrorCallBack error = new LocalErrorCallBack();
		assertNull(MagicEntryParser.parseLine(null, ">   string   SONG   Format", error));
		assertNotNull(error.details);
		// level not a number
		assertNull(MagicEntryParser.parseLine(null, ">a   string   SONG   Format", null));
		// level not a number with error
		error = new LocalErrorCallBack();
		assertNull(MagicEntryParser.parseLine(null, ">b   string   SONG   Format", error));
		assertNotNull(error.details);
	}

	@Test
	public void testTypeString() {
		// & part not a number
		assertNull(MagicEntryParser.parseLine(null, ">1   short&a    Format", null));
		// & part not a number with error
		LocalErrorCallBack error = new LocalErrorCallBack();
		assertNull(MagicEntryParser.parseLine(null, ">1   short&a    Format", error));
		assertNotNull(error.details);

		// no type string
		assertNull(MagicEntryParser.parseLine(null, ">1   &0    Format", null));
		// no type string with error
		error = new LocalErrorCallBack();
		assertNull(MagicEntryParser.parseLine(null, ">1   &0    Format", error));
		assertNotNull(error.details);

		// unknown matcher
		assertNull(MagicEntryParser.parseLine(null, ">1   unknowntype    Format", null));
		// unknown matcher with error
		error = new LocalErrorCallBack();
		assertNull(MagicEntryParser.parseLine(null, ">1   unknowntype    Format", error));
		assertNotNull(error.details);
	}

	@Test
	public void testValue() {
		// value not a number
		assertNull(MagicEntryParser.parseLine(null, ">0    byte     =z     format", null));
		// value not a number, with error
		LocalErrorCallBack error = new LocalErrorCallBack();
		assertNull(MagicEntryParser.parseLine(null, ">0    byte     =z     format", error));
		assertNotNull(error.details);
	}

	@Test
	public void testSpecial() {
		MagicEntry prev = MagicEntryParser.parseLine(null, "0   string   SONG   Format", null);
		assertNotNull(prev);

		// no whitespace
		assertNull(MagicEntryParser.parseLine(prev, "!:", null));
		// no whitespace, no error
		LocalErrorCallBack error = new LocalErrorCallBack();
		assertNull(MagicEntryParser.parseLine(prev, "!:", error));
		assertNotNull(error.details);

		// no value after whitespace
		assertNull(MagicEntryParser.parseLine(prev, "!:    ", null));
		// no value after whitespace, with error
		error = new LocalErrorCallBack();
		assertNull(MagicEntryParser.parseLine(prev, "!:    ", error));
		assertNotNull(error.details);
	}

	private static class LocalErrorCallBack implements ErrorCallBack {
		@SuppressWarnings("unused")
		String line;
		String details;
		@SuppressWarnings("unused")
		Exception e;

		@Override
		public void error(String line, String details, Exception e) {
			this.line = line;
			this.details = details;
			this.e = e;
		}
	}
}
