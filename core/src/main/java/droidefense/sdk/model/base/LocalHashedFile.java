package droidefense.sdk.model.base;

import apkr.external.module.ssdeep.exception.SSDeepException;
import droidefense.sdk.helpers.CheckSumGen;
import droidefense.sdk.helpers.Util;

import java.io.*;

public class LocalHashedFile extends AbstractHashedFile implements Serializable {

    protected transient File f;

    public LocalHashedFile(File f, boolean generateInformation) {
        super(generateInformation);
        this.f = f;
        init();
    }

    @Override
    public void generateHashes() {
        //TODO POSSIBLE HASHING BOTTLENECK
        crc32 = Util.toHexString(CheckSumGen.getInstance().calculateCRC32(f));
        md5 = CheckSumGen.getInstance().calculateMD5(f);
        sha1 = CheckSumGen.getInstance().calculateSHA1(f);
        sha256 = CheckSumGen.getInstance().calculateSHA256(f);
        sha512 = CheckSumGen.getInstance().calculateSHA512(f);
        try {
            ssdeep = CheckSumGen.getInstance().calculateSSDeep(f);
        } catch (SSDeepException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getContentLength() {
        return f.length();
    }

    @Override
    public String getPath() {
        return f.getAbsolutePath();
    }

    @Override
    public String getName() {
        return f.getName();
    }

    public String getSha256() {
        if (sha256 == null)
            //calculate
            sha256 = CheckSumGen.getInstance().calculateSHA256(this.f);
        return sha256;
    }

    public File getThisFile() {
        return this.f;
    }

    @Override
    public boolean exists() {
        return f.exists();
    }

    @Override
    public boolean isFile() {
        return f.isFile();
    }

    @Override
    public boolean canRead() {
        return f.canRead();
    }

    @Override
    public boolean canWrite() {
        return f.canWrite();
    }

    @Override
    public InputStream getStream() throws IOException {
        return new FileInputStream(f);
    }
}
