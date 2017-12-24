package droidefense.sdk.model.io;

import droidefense.sdk.util.CheckSumGen;
import droidefense.sdk.util.Util;
import droidefense.ssdeep.exception.SSDeepException;
import droidefense.vfs.model.impl.VirtualFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class VirtualHashedFile extends AbstractHashedFile implements Serializable {

    private transient VirtualFile vf;

    public VirtualHashedFile(VirtualFile vf, boolean generateInformation) {
        super(generateInformation);
        this.vf = vf;
        this.stream = new ByteArrayInputStream(this.vf.getContent());
        init();
    }

    public VirtualFile getThisFile() {
        return this.vf;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public boolean canWrite() {
        //TODO add lock sync for concurrency support
        return true;
    }

    @Override
    public InputStream getStream() throws IOException {
        return new ByteArrayInputStream(vf.getContent());
    }

    @Override
    public byte[] getContent() throws IOException {
        return vf.getContent();
    }

    @Override
    protected InputStream getDataStream() {
        return stream;
    }

    @Override
    public void generateHashes() {
        //TODO POSSIBLE HASHING BOTTLENECK
        byte[] data = vf.getContent();
        crc32 = Util.toHexString(CheckSumGen.getInstance().calculateCRC32(data));
        md5 = CheckSumGen.getInstance().calculateMD5(data);
        sha1 = CheckSumGen.getInstance().calculateSHA1(data);
        sha256 = CheckSumGen.getInstance().calculateSHA256(data);
        sha512 = CheckSumGen.getInstance().calculateSHA512(data);
        try {
            ssdeep = CheckSumGen.getInstance().calculateSSDeep(data);
        } catch (SSDeepException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getContentLength() {
        return vf.getContentLength();
    }

    @Override
    public String getPath() {
        return vf.getPath();
    }

    @Override
    public String getName() {
        return vf.getName();
    }

    @Override
    public String getSha256() {
        return sha256;
    }
}
