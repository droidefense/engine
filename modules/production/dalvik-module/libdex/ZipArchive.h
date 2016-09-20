/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Read-only access to Zip archives, with minimal heap allocation.
 */
#ifndef LIBDEX_ZIPARCHIVE_H_
#define LIBDEX_ZIPARCHIVE_H_

#include <ziparchive/zip_archive.h>

#include "SysUtil.h"
#include "DexFile.h"            // need DEX_INLINE

/*
 * Open a Zip archive.
 *
 * On success, returns 0 and populates "pArchive".  Returns nonzero errno
 * value on failure.
 */
DEX_INLINE int dexZipOpenArchive(const char* fileName, ZipArchiveHandle* pArchive) {
    return OpenArchive(fileName, pArchive);
}

/*
 * Like dexZipOpenArchive, but takes a file descriptor open for reading
 * at the start of the file.  The descriptor must be mappable (this does
 * not allow access to a stream).
 *
 * "debugFileName" will appear in error messages, but is not otherwise used.
 */
DEX_INLINE int dexZipOpenArchiveFd(int fd, const char* debugFileName,
                                   ZipArchiveHandle* pArchive) {
    return OpenArchiveFd(fd, debugFileName, pArchive);
}

/*
 * Close archive, releasing resources associated with it.
 *
 * Depending on the implementation this could unmap pages used by classes
 * stored in a Jar.  This should only be done after unloading classes.
 */
DEX_INLINE void dexZipCloseArchive(ZipArchiveHandle archive) {
    CloseArchive(archive);
}

/*
 * Return the archive's file descriptor.
 */
DEX_INLINE int dexZipGetArchiveFd(const ZipArchiveHandle pArchive) {
    return GetFileDescriptor(pArchive);
}

/*
 * Find an entry in the Zip archive, by name.  Returns NULL if the entry
 * was not found.
 */
DEX_INLINE int  dexZipFindEntry(const ZipArchiveHandle pArchive,
    const char* entryName, ZipEntry* data) {
    return FindEntry(pArchive, ZipString(entryName), data);
}

/*
 * Uncompress and write an entry to a file descriptor.
 *
 * Returns 0 on success.
 */
DEX_INLINE int dexZipExtractEntryToFile(ZipArchiveHandle handle,
    ZipEntry* entry, int fd) {
    return ExtractEntryToFile(handle, entry, fd);
}

#endif  // LIBDEX_ZIPARCHIVE_H_
