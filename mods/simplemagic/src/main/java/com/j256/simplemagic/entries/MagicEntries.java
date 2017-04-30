package com.j256.simplemagic.entries;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil.ErrorCallBack;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Class which encompasses a set of entries and allows us to optimize their use.
 * 
 * @author graywatson
 */
public class MagicEntries {

	private static final int MAX_LEVELS = 20;
	private static final int FIRST_BYTE_LINKED_LIST_SIZE = 256;

	private MagicEntry entryLinkedList;
	private final MagicEntry[] firstByteLinkedLists = new MagicEntry[FIRST_BYTE_LINKED_LIST_SIZE];
	private MagicEntry[] levelNexts = new MagicEntry[MAX_LEVELS];

	/**
	 * Read the entries so later we can find matches with them.
	 */
	public void readEntries(BufferedReader lineReader, ErrorCallBack errorCallBack) throws IOException {
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
				entry = MagicEntryParser.parseLine(previousEntry, line, errorCallBack);
				if (entry == null) {
					continue;
				}
			} catch (IllegalArgumentException e) {
				if (errorCallBack != null) {
					errorCallBack.error(line, e.getMessage(), e);
				}
				// ignore this entry
				continue;
			}

			int level = entry.getLevel();
			if (previousEntry == null) {
				if (level != 0) {
					if (errorCallBack != null) {
						errorCallBack.error(line, "first entry of the file but the level (" + level + ") should be 0",
								null);
					}
					continue;
				}
			} else {
				// if we go down a level, we need to clear the nexts above us
				for (int levelCount = level + 1; levelCount <= previousEntry.getLevel(); levelCount++) {
					levelNexts[levelCount] = null;
				}
			}

			// if this is the first at this level?
			if (levelNexts[level] == null) {
				if (level == 0) {
					// first top level entry
					entryLinkedList = entry;
				} else if (levelNexts[level - 1] != null) {
					// if the level-next is null then we know that we are a child of the one above us
					levelNexts[level - 1].setChild(entry);
				}
			} else {
				// continue the linked list
				levelNexts[level].setNext(entry);
			}
			levelNexts[level] = entry;
			previousEntry = entry;
		}

		// if we are done reading this file then we clear the level-nexts above 0
		for (int levelCount = 1; levelCount < levelNexts.length; levelCount++) {
			levelNexts[levelCount] = null;
		}
	}

	/**
	 * Optimize the magic entries by removing the first-bytes information into their own lists
	 */
	public void optimizeFirstBytes() {
		// no reason toe keep that array around
		levelNexts = null;
		// now we post process the entries and remove the first byte ones we can optimize
		MagicEntry[] firstByteNexts = new MagicEntry[firstByteLinkedLists.length];
		MagicEntry previousNonFirstByteEntry = null;
		MagicEntry next;
		for (MagicEntry entry = entryLinkedList; entry != null; entry = next) {
			byte[] startingBytes = entry.getStartsWithByte();
			if (startingBytes == null || startingBytes.length == 0) {
				// continue the entry linked list
				if (previousNonFirstByteEntry == null) {
					entryLinkedList = entry;
				} else {
					previousNonFirstByteEntry.setNext(entry);
				}
				previousNonFirstByteEntry = entry;
			} else {
				int index = (0xFF & startingBytes[0]);
				if (firstByteNexts[index] == null) {
					firstByteLinkedLists[index] = entry;
				} else {
					firstByteNexts[index].setNext(entry);
				}
				firstByteNexts[index] = entry;
			}
			next = entry.getNext();
			entry.setNext(null);
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
		if (index < firstByteLinkedLists.length && firstByteLinkedLists[index] != null) {
			ContentInfo info = findMatch(bytes, firstByteLinkedLists[index]);
			if (info != null) {
				// XXX: not sure if it is right to return if only a partial match here
				return info;
			}
		}
		return findMatch(bytes, entryLinkedList);
	}

	private ContentInfo findMatch(byte[] bytes, MagicEntry entryLinkedList) {
		ContentInfo partialMatchInfo = null;
		for (MagicEntry entry = entryLinkedList; entry != null; entry = entry.getNext()) {
			ContentInfo info = entry.matchBytes(bytes);
			if (info == null) {
				continue;
			}
			if (!info.isPartial()) {
				// first non-partial wins
				return info;
			} else if (partialMatchInfo == null) {
				// first partial match may win
				partialMatchInfo = info;
				// continue to look for non-partial
			} else {
				// already have a partial match
			}
		}
		return partialMatchInfo;
	}
}
