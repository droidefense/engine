package com.zerjioang.apkr.http.apimodel;

/**
 * Created by sergio on 3/4/16.
 */
public class FileUploadResponse {


    private final transient String path;
    private final int length;
    private final String hash;
    private final transient String fileHash;
    private final boolean integrity;
    private final long uploadFinishedTime;

    public FileUploadResponse(String path, int length, String hash, String fileHash, long uploadFinishedTime) {
        this.path = path;
        this.length = length;
        this.hash = hash;
        this.fileHash = fileHash;
        this.uploadFinishedTime = uploadFinishedTime;
        this.integrity = this.hash.equalsIgnoreCase(this.fileHash);
    }

    public int getLength() {
        return length;
    }

    public String getHash() {
        return hash;
    }

    public String getFileHash() {
        return fileHash;
    }

    public boolean isIntegrity() {
        return integrity;
    }

    public long getUploadFinishedTime() {
        return uploadFinishedTime;
    }

    public String getPath() {
        return path;
    }
}
