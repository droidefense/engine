package droidefense.parser;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.parser.base.AbstractFileParser;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.certificate.CertificateModel;
import droidefense.sdk.model.io.AbstractHashedFile;
import droidefense.sdk.model.io.LocalApkFile;
import sun.security.pkcs.PKCS7;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;

/**
 * Created by r00t on 24/10/15.
 */
public class AndroidCertParser extends AbstractFileParser {

    private CertificateModel certInfo;

    public AndroidCertParser(LocalApkFile apk, DroidefenseProject currentProject) {
        super(apk, currentProject);
    }

    @Override
    public void parserCode() {
        Log.write(LoggerType.INFO, "\n\nParsing Android Certificate...\n");
        String certpath = "";
        //TODO fix
        try {
            AbstractHashedFile certificateFile = currentProject.getStaticInfo().getCertFile();
            extractCertInfo(certificateFile.getStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void extractCertInfo(InputStream in) throws IOException {
        PKCS7 p7 = new PKCS7(in);
        X509Certificate[] cert = p7.getCertificates();
        currentProject.setCertNumber(cert.length);
        for (X509Certificate c : cert) {
            System.out.println(c.toString());
            certInfo = new CertificateModel(c);
            currentProject.addCertInfo(certInfo);
        }
    }

    public void extractCertInfo(File f) throws IOException {
        //Link data to appropiate currentProject
        PKCS7 p7 = new PKCS7(new FileInputStream(f));
        X509Certificate[] cert = p7.getCertificates();
        for (X509Certificate c : cert) {
            System.out.println(c.toString());
            certInfo = new CertificateModel(c);
        }
    }

    public CertificateModel getCertInfo() {
        return certInfo;
    }
}
