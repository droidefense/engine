package droidefense.sdk.model.io;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.j256.simplemagic.ContentType;
import droidefense.handler.SignatureHandler;
import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.sdk.helpers.DroidDefenseEnvironment;
import droidefense.sdk.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public abstract class AbstractHashedFile implements Serializable {

    public static final boolean ENABLE_HASHING = true;
    public static final boolean DISABLE_HASHING = false;

    private transient static final String NO_EXTENSION = "";
    private static final ContentInfoUtil util = new ContentInfoUtil();
    private transient final boolean generateInformation;
    protected String filename;
    protected long filesize;

    protected String crc32, md5, sha1, sha256, sha512, ssdeep;
    protected String beautyFilesize;

    protected boolean suspiciousWin32File;
    protected boolean androidDefaultFile;
    protected boolean extensionMatches;
    protected String extensionFromHeader, extensionFromFilename;
    protected transient InputStream stream;
    private String mimetype, description, magicDescription;
    private transient ContentInfo contentInfo;

    public AbstractHashedFile(boolean generateInformation) {
        this.generateInformation = generateInformation;
        if (generateInformation) {
            Log.write(LoggerType.INFO, "Full sample hashing is enabled!", "It may slow down overall analysis time");
        }
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
        extensionFromFilename = Util.getFileExtension(this.filename);
        //hash info
        contentInfo = null;
        if (generateInformation) {
            Log.write(LoggerType.DEBUG, "Generating hashes...");
            generateHashes();
            androidDefaultFile = DroidDefenseEnvironment.getInstance().isDefaultFile(this.sha256);
            calculateHeaderBasedExtension();
        }
    }

    private void calculateHeaderBasedExtension() {
        ContentInfo info;
        try {
            if (getDataStream() != null) {
                info = util.findMatch(getDataStream());
                if (info != null) {
                    getContentInfoFromMagicGz(info);
                } else {
                    //try to detect filetype using our custom header based content detector
                    boolean fileClassified = getContentInfoFromCustom();
                    if (!fileClassified) {
                        Log.write(LoggerType.ERROR, "No content information is present for " + this.filename);
                        this.extensionMatches = false;
                    }
                }
            } else {
                Log.write(LoggerType.ERROR, "Not data stream is present for " + this.filename);
            }
        } catch (IOException e) {
            Log.write(LoggerType.ERROR, "IO Error", e.getLocalizedMessage());
        }
    }

    private boolean getContentInfoFromCustom() {
        Log.write(LoggerType.TRACE, "getting content info from custom file classifier...");
        SignatureHandler handler = SignatureHandler.getInstance();
        handler.setFile(this);
        handler.setNameExtension(this.getExtensionFilename());
        handler.doTheJob();
        handler.updateDescription();
        return handler.isSignatureFound();
    }

    private void getContentInfoFromMagicGz(ContentInfo info) {
        Log.write(LoggerType.TRACE, "getting content info from magic.gz...");

        //get info
        this.description = info.getMessage();
        ContentType type = info.getContentType();
        if (type != null) {
            this.mimetype = type.getMimeType();
            this.extensionFromHeader = type.name();
            this.extensionMatches = this.extensionFromHeader.toLowerCase().equals(this.extensionFromFilename.toLowerCase()) || isApkFile();
        } else {
            Log.write(LoggerType.ERROR, "No content type information is present" + this.filename);
            this.extensionMatches = false;
        }
    }

    private boolean isApkFile() {
        return this.extensionFromFilename.toLowerCase().equals("apk") && this.extensionFromHeader.toLowerCase().equals("zip");
    }

    protected InputStream getDataStream() {
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
     * @return the extensionMatches
     */
    public boolean isExtensionMatches() {
        return extensionMatches && !suspiciousFile();
    }

    /**
     * @param extensionMatches the extensionMatches to set
     */
    public void setExtensionMatches(boolean extensionMatches) {
        this.extensionMatches = extensionMatches;
    }

    public void setSignatureMatches() {
        if (getExtensionFromHeader() != null && getExtension() != null)
            this.extensionMatches = getExtensionFromHeader().equalsIgnoreCase(getExtension());
    }

    /**
     * @return the extensionFromHeader
     */
    public String getExtensionFromHeader() {
        return extensionFromHeader;
    }

    /**
     * @param extensionFromHeader the extensionFromHeader to set
     */
    public void setExtensionFromHeader(String extensionFromHeader) {
        this.extensionFromHeader = extensionFromHeader.toUpperCase();
    }

    private boolean suspiciousFile() {
        suspiciousWin32File = extensionFromHeader.equals("exe")
                || extensionFromHeader.equals("dll")
                || extensionFromHeader.equals("vbs")
                || extensionFromHeader.equals("com")
                || extensionFromHeader.equals("js")
                || extensionFromHeader.equals("jar")
                || extensionFromHeader.equals("zip")
                || extensionFromHeader.equals("js")
                || extensionFromHeader.equals("html")
                || extensionFromHeader.equals("elf");
        return suspiciousWin32File;
    }

    public String getExtension() {
        return extensionFromFilename;
    }

    public void setExtension(String extension) {
        this.extensionFromFilename = extension.toUpperCase();
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

    public boolean isAndroidDefaultFile() {
        return androidDefaultFile;
    }

    public void setAndroidDefaultFile(boolean androidDefaultFile) {
        this.androidDefaultFile = androidDefaultFile;
    }

    public String getExtensionFilename() {
        return extensionFromFilename;
    }

    public void setExtensionFilename(String extensionFilename) {
        this.extensionFromFilename = extensionFilename;
    }
}
