package com.j256.simplemagic.entries;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil.ErrorCallBack;
import com.j256.simplemagic.logger.Logger;
import com.j256.simplemagic.logger.LoggerFactory;

/**
 * Class which encompasses a set of entries and allows us to optimize their use.
 * 
 * @author graywatson
 */
public class MagicEntries {

	private static final int MAX_LEVELS = 20;
	private static final int FIRST_BYTE_LIST_SIZE = 256;
	private static Logger logger = LoggerFactory.getLogger(MagicEntries.class);

	private final List<MagicEntry> entryList = new ArrayList<MagicEntry>();
	@SuppressWarnings("unchecked")
	private final List<MagicEntry>[] firstByteEntryLists = new ArrayList[FIRST_BYTE_LIST_SIZE];

	/**
	 * Read the entries so later we can find matches with them.
	 */
	public void readEntries(BufferedReader lineReader, ErrorCallBack errorCallBack) throws IOException {
		final MagicEntry[] levelParents = new MagicEntry[MAX_LEVELS];
		MagicEntry previousEntry = null;
		while (true) {
			String line = lineReader.readLine();
			if (line == null) {
				break;
			}
			// skip blanks and comments
			if (line.length() == 0 || line.charAt(0) == '#') {
				continue;
			}

			MagicEntry entry;
			try {
				// we need the previous entry because of mime-type, etc. which augment the previous line
				entry = MagicEntryParser.parseLine(previousEntry, line, errorCallBack);
				if (entry == null) {
					continue;
				}
			} catch (IllegalArgumentException e) {
				if (errorCallBack != null) {
					errorCallBack.error(line, e.getMessage(), e);
				}
				continue;
			}

			int level = entry.getLevel();
			if (previousEntry == null && level != 0) {
				if (errorCallBack != null) {
					errorCallBack.error(line, "first entry of the file but the level " + level + " should be 0", null);
				}
				continue;
			}

			if (level == 0) {
				// top level entry
				entryList.add(entry);
			} else if (levelParents[level - 1] == null) {
				if (errorCallBack != null) {
					errorCallBack.error(line,
							"entry has level " + level + " but no parent entry with level " + (level - 1), null);
				}
				continue;
			} else {
				// we are a child of the one above us
				levelParents[level - 1].addChild(entry);
			}
			levelParents[level] = entry;
			previousEntry = entry;
		}
	}

	/**
	 * Optimize the magic entries by removing the first-bytes information into their own lists
	 */
	public void optimizeFirstBytes() {
		// now we post process the entries and remove the first byte ones we can optimize
		for (MagicEntry entry : entryList) {
			byte[] startingBytes = entry.getStartsWithByte();
			if (startingBytes == null || startingBytes.length == 0) {
				continue;
			}
			int index = (0xFF & startingBytes[0]);
			if (firstByteEntryLists[index] == null) {
				firstByteEntryLists[index] = new ArrayList<MagicEntry>();
			}
			firstByteEntryLists[index].add(entry);
			/*
			 * We put an entry in the first-byte list but need to leave it in the main list because there may be
			 * optional characters or != or > comparisons in the match
			 */
		}
	}

	/**
	 * Find and return a match for the associated bytes.
	 */
	public ContentInfo findMatch(byte[] bytes) {
		if (bytes.length == 0) {
			return ContentInfo.EMPTY_INFO;
		}
		// first do the start byte ones
		int index = (0xFF & bytes[0]);
		if (index < firstByteEntryLists.length && firstByteEntryLists[index] != null) {
			ContentInfo info = findMatch(bytes, firstByteEntryLists[index]);
			if (info != null) {
				// this seems to be right to return even if only a partial match here
				return info;
			}
		}
		return findMatch(bytes, entryList);
	}

	private ContentInfo findMatch(byte[] bytes, List<MagicEntry> entryList) {
		ContentInfo partialMatchInfo = null;
		for (MagicEntry entry : entryList) {
			ContentInfo info = entry.matchBytes(bytes);
			if (info == null) {
				continue;
			}
			if (!info.isPartial()) {
				// first non-partial wins
				logger.trace("found full match {}", entry);
				logger.trace("returning full match {}", info);
				return info;
			} else if (partialMatchInfo == null) {
				// first partial match may win
				logger.trace("found partial match {}", entry);
				partialMatchInfo = info;
				// continue to look for non-partial
			} else {
				// already have a partial match
			}
		}
		if (partialMatchInfo == null) {
			logger.trace("returning no match");
			return null;
		} else {
			// returning first partial match
			logger.trace("returning partial match {}", partialMatchInfo);
			return partialMatchInfo;
		}
	}
}
