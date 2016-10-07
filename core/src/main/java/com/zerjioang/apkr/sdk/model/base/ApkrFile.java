package com.zerjioang.apkr.sdk.model.base;

import apkr.external.module.ssdeep.exception.SSDeepException;
import com.zerjioang.apkr.sdk.helpers.CheckSumGen;
import com.zerjioang.apkr.sdk.helpers.Util;

import java.io.File;
import java.io.Serializable;

public class ApkrFile implements Serializable {

    private static final String NO_EXTENSION = "";
    protected transient final File f;
    private long filesize;
    private String beautyFilesize;
    private String crc32, md5, sha1, sha256, sha512, ssdeep;
    private boolean suspiciousFile;
    private boolean signatureMatches;
    private String filename, headerBasedExtension, declaredExtension, description;
    private String magicDescription;

    public ApkrFile(String apkPath, boolean generateInformation) {
        this(new File(apkPath), generateInformation);
    }

    public ApkrFile(File parent, boolean generateInformation) {
        this.f = parent;
        if (this.f.isFile()) {
            filesize = this.f.length();
            filename = this.f.getName();
            declaredExtension = Util.getFileExtension(this.f);
            if (generateInformation) {
                if (this.filesize > 0) {
                    beautyFilesize = Util.beautifyFileSize(this.filesize);
                } else {
                    beautyFilesize = "0 b";
                }

                //TODO HASHING BOTTLENECK
                File currentFile = getThisFile();
                crc32 = Util.toHexString(CheckSumGen.getInstance().calculateCRC32(currentFile));
                md5 = CheckSumGen.getInstance().calculateMD5(currentFile);
                sha1 = CheckSumGen.getInstance().calculateSHA1(currentFile);
                sha256 = CheckSumGen.getInstance().calculateSHA256(currentFile);
                sha512 = CheckSumGen.getInstance().calculateSHA512(currentFile);
                try {
                    ssdeep = CheckSumGen.getInstance().calculateSSDeep(currentFile);
                } catch (SSDeepException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ApkrFile(String path) {
        this(new File(path), true);
    }

    public ApkrFile(File parent) {
        this(parent, true);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public String getSha256() {
        if (sha256 == null)
            //calculate
            sha256 = CheckSumGen.getInstance().calculateSHA256(this.f);
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public String getSha512() {
        return sha512;
    }

    public void setSha512(String sha512) {
        this.sha512 = sha512;
    }

    public String getSsdeep() {
        return ssdeep;
    }

    public void setSsdeep(String ssdeep) {
        this.ssdeep = ssdeep;
    }

    public boolean isSuspiciousFile() {
        return suspiciousFile;
    }

    public void setSuspiciousFile(boolean suspiciousFile) {
        this.suspiciousFile = suspiciousFile;
    }

    /**
     * @return the signatureMatches
     */
    public boolean isSignatureMatches() {
        return signatureMatches && !suspiciousFile();
    }

    /**
     * @param signatureMatches the signatureMatches to set
     */
    public void setSignatureMatches(boolean signatureMatches) {
        this.signatureMatches = signatureMatches;
    }

    public void setSignatureMatches() {
        if (getHeaderBasedExtension() != null && getExtension() != null)
            this.signatureMatches = getHeaderBasedExtension().equalsIgnoreCase(getExtension());
    }

    /**
     * @return the headerBasedExtension
     */
    public String getHeaderBasedExtension() {
        return headerBasedExtension;
    }

    /**
     * @param headerBasedExtension the headerBasedExtension to set
     */
    public void setHeaderBasedExtension(String headerBasedExtension) {
        this.headerBasedExtension = headerBasedExtension.toUpperCase();
    }

    private boolean suspiciousFile() {
        suspiciousFile = headerBasedExtension.equals("exe")
                || headerBasedExtension.equals("dll")
                || headerBasedExtension.equals("manifest")
                || headerBasedExtension.equals("vbs")
                || headerBasedExtension.equals("com")
                || headerBasedExtension.equals("js")
                || headerBasedExtension.equals("jar")
                || headerBasedExtension.equals("zip")
                || headerBasedExtension.equals("js")
                || headerBasedExtension.equals("html")
                || headerBasedExtension.equals("elf");
        return suspiciousFile;
    }

    public String getExtension() {
        return declaredExtension;
    }

    public void setExtension(String extension) {
        this.declaredExtension = extension.toUpperCase();
    }

    public String getDescription() {
        return description == null ? "" : description.trim();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.f.getAbsolutePath();
    }

    public String getBeautyFilesize() {
        return beautyFilesize;
    }

    public void setBeautyFilesize(String beautyFilesize) {
        this.beautyFilesize = beautyFilesize;
    }

    public String getMagicDescription() {
        return magicDescription == null ? "" : magicDescription.trim();
    }

    public void setMagicDescription(String magicDescription) {
        this.magicDescription = magicDescription;
    }

    public File getThisFile() {
        return this.f;
    }

    public String getAbsolutePath() {
        return this.f.getAbsolutePath();
    }

    public boolean hasExtension() {
        return this.f.getName().contains(".");
    }

    public String extractExtensionFromName() {
        //file with extension detected
        String[] data = this.f.getName().split("\\.");
        //return last value found
        if (data.length > 0)
            return data[data.length - 1].toUpperCase();
        return NO_EXTENSION;
    }
}
