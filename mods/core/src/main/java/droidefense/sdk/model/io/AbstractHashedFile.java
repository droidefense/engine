package droidefense.sdk.model.io;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.j256.simplemagic.ContentType;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.sdk.helpers.Util;
import droidefense.util.DroidefenseIntel;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public abstract class AbstractHashedFile implements Serializable {

    private transient static final String NO_EXTENSION = "";
    private transient final boolean generateInformation;
    private static final ContentInfoUtil util = new ContentInfoUtil();

    protected long filesize;
    protected String beautyFilesize;
    protected String crc32, md5, sha1, sha256, sha512, ssdeep;
    protected boolean suspiciousWin32File;
    protected boolean signatureMatches;
    protected boolean isDefaultFile;
    protected String filename, headerBasedExtension, declaredExtension, description;
    private String mimetype;
    protected String magicDescription;
    private ContentInfo contentInfo;

    protected transient InputStream stream;

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
        contentInfo = null;
        if (generateInformation) {
            generateHashes();
            isDefaultFile = DroidefenseIntel.getInstance().isDefaultFile(this.sha256);
            calculateHeaderBasedExtension();
        }
    }

    private void calculateHeaderBasedExtension() {
        ContentInfo info;
        try {
            if(getDataStream()!=null){
                info = util.findMatch(getDataStream());
                if(info!=null) {
                    //get info
                    this.description = info.getMessage();
                    ContentType type = info.getContentType();
                    if(type!=null){
                        this.mimetype = type.getMimeType();
                        this.headerBasedExtension = type.name();
                        this.signatureMatches = this.headerBasedExtension.toLowerCase().equals(this.declaredExtension.toLowerCase()) || isApkFile();
                    }
                    else{
                        Log.write(LoggerType.ERROR, "No content type information is present"+this.filename);
                        this.signatureMatches = false;
                    }
                }
                else{
                    Log.write(LoggerType.ERROR, "No content information is present for "+this.filename);
                    this.signatureMatches = false;
                }
            }
            else{
                Log.write(LoggerType.ERROR, "Not data stream is present for "+this.filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isApkFile() {
        return this.declaredExtension.toLowerCase().equals("apk") && this.headerBasedExtension.toLowerCase().equals("zip");
    }

    protected InputStream getDataStream(){
        return this.stream;
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

    public boolean isSuspiciousWin32File() {
        return suspiciousWin32File;
    }

    public void setSuspiciousWin32File(boolean suspiciousWin32File) {
        this.suspiciousWin32File = suspiciousWin32File;
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
        suspiciousWin32File = headerBasedExtension.equals("exe")
                || headerBasedExtension.equals("dll")
                || headerBasedExtension.equals("vbs")
                || headerBasedExtension.equals("com")
                || headerBasedExtension.equals("js")
                || headerBasedExtension.equals("jar")
                || headerBasedExtension.equals("zip")
                || headerBasedExtension.equals("js")
                || headerBasedExtension.equals("html")
                || headerBasedExtension.equals("elf");
        return suspiciousWin32File;
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

    public abstract byte[] getContent() throws IOException;

    public ContentInfo getContentInfo() {
        return contentInfo;
    }

    public void setContentInfo(ContentInfo contentInfo) {
        this.contentInfo = contentInfo;
    }

    public boolean isGenerateInformation() {
        return generateInformation;
    }

    public String getCrc32() {
        return crc32;
    }

    public void setCrc32(String crc32) {
        this.crc32 = crc32;
    }

    public boolean isDefaultFile() {
        return isDefaultFile;
    }

    public void setDefaultFile(boolean defaultFile) {
        isDefaultFile = defaultFile;
    }

    public String getDeclaredExtension() {
        return declaredExtension;
    }

    public void setDeclaredExtension(String declaredExtension) {
        this.declaredExtension = declaredExtension;
    }
}
