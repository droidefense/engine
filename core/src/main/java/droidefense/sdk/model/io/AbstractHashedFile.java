package droidefense.sdk.model.io;

import droidefense.sdk.helpers.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public abstract class AbstractHashedFile implements Serializable {

    private static final String NO_EXTENSION = "";
    private transient final boolean generateInformation;

    protected long filesize;
    protected String beautyFilesize;
    protected String crc32, md5, sha1, sha256, sha512, ssdeep;
    protected boolean suspiciousFile;
    protected boolean signatureMatches;
    protected String filename, headerBasedExtension, declaredExtension, description;
    protected String magicDescription;

    public AbstractHashedFile(boolean generateInformation) {
        this.generateInformation = generateInformation;
    }

    protected void init() {
        //filesize info
        filesize = getContentLength();
        if (this.filesize > 0) {
            beautyFilesize = Util.beautifyFileSize(this.filesize);
        } else {
            beautyFilesize = "0 b";
        }
        //filename info
        filename = getName();
        //extension info
        declaredExtension = Util.getFileExtension(this.filename);
        //hash info
        if (generateInformation) {
            generateHashes();
            //TODO calculate header based extension vs declared extension
            //variables: signatureMatches, headerBasedExtension, description, magicDescription
        }
    }

    protected abstract void generateHashes();

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
        return this.getPath();
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

    public String getAbsolutePath() {
        return this.getPath();
    }

    public boolean hasExtension() {
        return this.getName().contains(".");
    }

    public String extractExtensionFromName() {
        //file with extension detected
        String[] data = getName().split("\\.");
        //return last value found
        if (data.length > 0)
            return data[data.length - 1].toUpperCase();
        return NO_EXTENSION;
    }

    public abstract long getContentLength();

    public abstract String getPath();

    public abstract String getName();

    public abstract String getSha256();

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public abstract Object getThisFile();

    public abstract boolean exists();

    public abstract boolean isFile();

    public abstract boolean canRead();

    public abstract boolean canWrite();

    public abstract InputStream getStream() throws IOException;
}
