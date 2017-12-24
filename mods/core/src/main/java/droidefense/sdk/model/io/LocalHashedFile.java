package droidefense.sdk.model.io;

import com.droidefense.log4j.Log;
import com.droidefense.log4j.LoggerType;
import droidefense.sdk.helpers.CheckSumGen;
import droidefense.sdk.helpers.Util;
import droidefense.ssdeep.exception.SSDeepException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LocalHashedFile extends AbstractHashedFile implements Serializable {

    protected transient File f;

    public LocalHashedFile(File f, boolean generateInformation) {
        super(generateInformation);
        this.f = f;
        try {
            Log.write(LoggerType.DEBUG, "Loading sample bytedata stream...");
            this.stream = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            Log.write(LoggerType.ERROR, "Could not find the requested file");
        }
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
            Log.write(LoggerType.ERROR, e.getLocalizedMessage());
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

    @Override
    public byte[] getContent() throws IOException {
        return Files.readAllBytes(Paths.get(f.getAbsolutePath()));
    }
}
