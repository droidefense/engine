package com.zerjioang.apkr.sdk.model.signature;

import java.io.Serializable;
import java.util.Arrays;

public class Signature implements Serializable {

    private String extension;
    private int[] signatureBytes;
    private String filetypeInfo;
    private int signatureLength;

    /**
     * @param extension
     * @param signatureBytes
     * @param filetypeInfo
     */
    public Signature(String extension, int[] signatureBytes, String filetypeInfo) {
        this.extension = extension;
        this.signatureBytes = signatureBytes;
        this.filetypeInfo = filetypeInfo;
        this.signatureLength = signatureBytes.length;
    }

    /**
     * @return the extension
     */
    public final String getExtension() {
        return extension;
    }

    /**
     * @param extension the extension to set
     */
    public final void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * @return the signatureBytes
     */
    public final int[] getSignatureBytes() {
        return signatureBytes;
    }

    /**
     * @param signatureBytes the signatureBytes to set
     */
    public final void setSignatureBytes(int[] signatureBytes) {
        this.signatureBytes = signatureBytes;
    }

    /**
     * @return the filetypeInfo
     */
    public final String getFiletypeInfo() {
        return filetypeInfo;
    }

    /**
     * @param filetypeInfo the filetypeInfo to set
     */
    public final void setFiletypeInfo(String filetypeInfo) {
        this.filetypeInfo = filetypeInfo;
    }

    /**
     * @return the signatureLength
     */
    public final int getSignatureLength() {
        return signatureLength;
    }

    /**
     * @param signatureLength the signatureLength to set
     */
    public final void setSignatureLength(int signatureLength) {
        this.signatureLength = signatureLength;
    }

    @Override
    public String toString() {
        return "Signature{" +
                "extension='" + extension + '\'' +
                ", signatureBytes=" + Arrays.toString(signatureBytes) +
                ", filetypeInfo='" + filetypeInfo + '\'' +
                ", signatureLength=" + signatureLength +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Signature signature = (Signature) o;

        if (signatureLength != signature.signatureLength) return false;
        if (extension != null ? !extension.equals(signature.extension) : signature.extension != null) return false;
        if (!Arrays.equals(signatureBytes, signature.signatureBytes)) return false;
        return !(filetypeInfo != null ? !filetypeInfo.equals(signature.filetypeInfo) : signature.filetypeInfo != null);

    }

    @Override
    public int hashCode() {
        int result = extension != null ? extension.hashCode() : 0;
        result = 31 * result + (signatureBytes != null ? Arrays.hashCode(signatureBytes) : 0);
        result = 31 * result + (filetypeInfo != null ? filetypeInfo.hashCode() : 0);
        result = 31 * result + signatureLength;
        return result;
    }
}
