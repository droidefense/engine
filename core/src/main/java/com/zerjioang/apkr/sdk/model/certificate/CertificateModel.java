package com.zerjioang.apkr.sdk.model.certificate;

import com.zerjioang.apkr.sdk.helpers.Util;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CertificateModel implements Serializable {

    private transient static final CertificateModel debugCertificate = new CertificateModel();
    private boolean seemsDebugCertificate;
    private int version;
    private CertificateSubject subject;
    private CertificateSubject subjectPrincipal;
    private String signatureAlgorithm;
    private String oid;
    private String type;
    private String publicKeyAlgorithm;
    private String key;
    private String certType;
    private String modulus;
    private String exponent;
    private String validity;
    private CertificateSubject issuer;
    private String serialNumber;
    private transient byte[] signatureBytes;

    private String beautifierString;

    private long start, end, validtime;

    private Date startDate;
    private Date endDate;

    public CertificateModel() {
        seemsDebugCertificate = true;
        setVersion(3);
        setSubject(new CertificateSubject("CN=Android Debug, O=Android, C=US"));
        setSignatureAlgorithm("SHA256withRSA");
        setOid("1.2.840.113549.1.1.11");
        setType("X.509");
        setPublicKeyAlgorithm("RSA");
        setExponent("65537");
        setValidity("10950 DAYS");
        setIssuer(new CertificateSubject("CN=Android Debug, O=Android, C=US"));
        setSerialNumber("1188267067");
    }

    public CertificateModel(X509Certificate c) {
        if (c != null) {
            version = c.getVersion();
            subject = new CertificateSubject(c.getSubjectDN().getName());
            issuer = new CertificateSubject(c.getIssuerDN().getName());
            subjectPrincipal = new CertificateSubject(c.getSubjectX500Principal().getName());
            signatureAlgorithm = c.getSigAlgName();
            oid = c.getSigAlgOID();
            type = c.getType();
            publicKeyAlgorithm = c.getPublicKey().getAlgorithm();
            startDate = c.getNotBefore();
            endDate = c.getNotAfter();
            start = startDate.getTime();
            end = endDate.getTime();
            validtime = end - start;
            validity = Util.calculateDateDiff(startDate, endDate, TimeUnit.DAYS) + " DAYS";
            serialNumber = c.getSerialNumber().toString();
            signatureBytes = c.getSignature();
            beautifierString = c.toString();
            try {
                certType = "Unknown";
                if (c.getPublicKey() instanceof RSAPublicKey) {
                    RSAPublicKey tempKey;
                    tempKey = (RSAPublicKey) c.getPublicKey();
                    modulus = tempKey.getModulus().toString();
                    exponent = tempKey.getPublicExponent().toString();
                    certType = "RSA Public key";
                } else if (c.getPublicKey() instanceof DSAPublicKey) {
                    DSAPublicKey tempKey;
                    tempKey = (DSAPublicKey) c.getPublicKey();
                    certType = "DSA Public key";
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            seemsDebugCertificate = seemsDebugCertificate(debugCertificate);
        }
    }

    public String toBeautyString() {
        return beautifierString;
    }

    /**
     * @return the version
     */
    public final int getVersion() {
        return version;
    }


    /**
     * @param version the version to set
     */
    public final void setVersion(int version) {
        this.version = version;
    }


    /**
     * @return the signatureAlgorithm
     */
    public final String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }


    /**
     * @param signatureAlgorithm the signatureAlgorithm to set
     */
    public final void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }


    /**
     * @return the oid
     */
    public final String getOid() {
        return oid;
    }


    /**
     * @param oid the oid to set
     */
    public final void setOid(String oid) {
        this.oid = oid;
    }


    /**
     * @return the type
     */
    public final String getType() {
        return type;
    }


    /**
     * @param type the type to set
     */
    public final void setType(String type) {
        this.type = type;
    }


    /**
     * @return the publicKeyAlgorithm
     */
    public final String getPublicKeyAlgorithm() {
        return publicKeyAlgorithm;
    }


    /**
     * @param publicKeyAlgorithm the publicKeyAlgorithm to set
     */
    public final void setPublicKeyAlgorithm(String publicKeyAlgorithm) {
        this.publicKeyAlgorithm = publicKeyAlgorithm;
    }


    /**
     * @return the key
     */
    public final String getKey() {
        return key;
    }


    /**
     * @param key the key to set
     */
    public final void setKey(String key) {
        this.key = key;
    }


    /**
     * @return the modulus
     */
    public final String getModulus() {
        return modulus;
    }


    /**
     * @param modulus the modulus to set
     */
    public final void setModulus(String modulus) {
        this.modulus = modulus;
    }


    /**
     * @return the exponent
     */
    public final String getExponent() {
        return exponent;
    }


    /**
     * @param exponent the exponent to set
     */
    public final void setExponent(String exponent) {
        this.exponent = exponent;
    }


    /**
     * @return the validity
     */
    public final String getValidity() {
        return validity;
    }


    /**
     * @param validity the validity to set
     */
    public final void setValidity(String validity) {
        this.validity = validity;
    }


    /**
     * @return the serialNumber
     */
    public final String getSerialNumber() {
        return serialNumber;
    }


    /**
     * @param serialNumber the serialNumber to set
     */
    public final void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * @return the signatureBytes
     */
    public final byte[] getSignatureBytes() {
        return signatureBytes;
    }


    /**
     * @param signatureBytes the signatureBytes to set
     */
    public final void setSignatureBytes(byte[] signatureBytes) {
        this.signatureBytes = signatureBytes;
    }


    /**
     * @return the startDate
     */
    public final Date getStartDate() {
        return startDate;
    }


    /**
     * @param startDate the startDate to set
     */
    public final void setStartDate(Date startDate) {
        this.startDate = startDate;
    }


    /**
     * @return the endDate
     */
    public final Date getEndDate() {
        return endDate;
    }


    /**
     * @param endDate the endDate to set
     */
    public final void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public CertificateSubject getSubjectPrincipal() {
        return subjectPrincipal;
    }

    public void setSubjectPrincipal(CertificateSubject subjectPrincipal) {
        this.subjectPrincipal = subjectPrincipal;
    }

    public CertificateSubject getIssuer() {
        return issuer;
    }

    public void setIssuer(CertificateSubject issuer) {
        this.issuer = issuer;
    }

    public CertificateSubject getSubject() {
        return subject;
    }

    public void setSubject(CertificateSubject subject) {
        this.subject = subject;
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getBeautifierString() {
        return beautifierString;
    }

    public void setBeautifierString(String beautifierString) {
        this.beautifierString = beautifierString;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getValidtime() {
        return validtime;
    }

    public void setValidtime(long validtime) {
        this.validtime = validtime;
    }

    @Override
    public String toString() {
        return "CertificateModel{" +
                "version=" + version +
                ", subject=" + subject +
                ", subjectPrincipal=" + subjectPrincipal +
                ", signatureAlgorithm='" + signatureAlgorithm + '\'' +
                ", oid='" + oid + '\'' +
                ", type='" + type + '\'' +
                ", publicKeyAlgorithm='" + publicKeyAlgorithm + '\'' +
                ", key='" + key + '\'' +
                ", certType='" + certType + '\'' +
                ", modulus='" + modulus + '\'' +
                ", exponent='" + exponent + '\'' +
                ", validity='" + validity + '\'' +
                ", issuer=" + issuer +
                ", serialNumber='" + serialNumber + '\'' +
                ", signatureBytes=" + Arrays.toString(signatureBytes) +
                ", beautifierString='" + beautifierString + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", validtime=" + validtime +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    public boolean seemsDebugCertificate(CertificateModel that) {
        if (version != that.version)
            return false;
        if (validtime != that.validtime)
            return false;
        if (signatureAlgorithm != null ? !signatureAlgorithm.equals(that.signatureAlgorithm) : that.signatureAlgorithm != null)
            return false;
        if (oid != null ? !oid.equals(that.oid) : that.oid != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null)
            return false;
        if (publicKeyAlgorithm != null ? !publicKeyAlgorithm.equals(that.publicKeyAlgorithm) : that.publicKeyAlgorithm != null)
            return false;
        if (key != null ? !key.equals(that.key) : that.key != null)
            return false;
        if (certType != null ? !certType.equals(that.certType) : that.certType != null)
            return false;
        if (exponent != null ? !exponent.equals(that.exponent) : that.exponent != null)
            return false;
        if (validity != null ? !validity.equals(that.validity) : that.validity != null)
            return false;
        if (subject != null ? !subject.equals(that.subject) : that.subject != null)
            return false;
        if (subjectPrincipal != null ? !subjectPrincipal.equals(that.subjectPrincipal) : that.subjectPrincipal != null)
            return false;
        return issuer != null ? issuer.equals(that.issuer) : that.issuer == null;

    }
}