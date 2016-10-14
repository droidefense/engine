package droidefense.parser;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.handler.FileIOHandler;
import droidefense.parser.base.AbstractFileParser;
import droidefense.sdk.model.base.APKFile;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.certificate.CertificateModel;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.ParsingException;

import java.io.*;
import java.security.cert.X509Certificate;

/**
 * Created by r00t on 24/10/15.
 */
public class AndroidCertParser extends AbstractFileParser {

    private CertificateModel certInfo;

    @Override
    public void parserCode() {
        Log.write(LoggerType.INFO, "\nParsing Android Certificate...\n");
        String certpath = "";
        //TODO fix
        try {
            String apktoolCertPath = FileIOHandler.getUnpackOutputPath(apk) + File.separator + "original" + File.separator + "META-INF" + File.separator + "CERT.RSA";
            String axmlCertPath = FileIOHandler.getUnpackOutputPath(apk) + File.separator + "META-INF" + File.separator + "CERT.RSA";
            if (apk.getTechnique() == APKFile.APKTOOL) {
                certpath = apktoolCertPath;
                InputStream in = new FileInputStream(apktoolCertPath);
                extractCertInfo(in);
            } else {
                certpath = axmlCertPath;
                InputStream in = new FileInputStream(axmlCertPath);
                extractCertInfo(in);
            }
        } catch (FileNotFoundException e) {
            // if CERT.RSA file not found, check other .RSA files
            File[] list;
            File parent = new File(certpath).getParentFile();
            if (parent.exists()) {
                list = parent.listFiles();
                for (File f : list) {
                    if (!f.getName().toLowerCase().endsWith(".mf") && !f.getName().toLowerCase().endsWith(".sf")) {
                        //check if the file is a certificate
                        try {
                            extractCertInfo(new FileInputStream(f));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        } catch (ParsingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void extractCertInfo(InputStream in) throws IOException {
        //Link data to appropiate currentProject
        DroidefenseProject lProject = DroidefenseProject.getProject(apk);
        PKCS7 p7 = new PKCS7(in);
        X509Certificate[] cert = p7.getCertificates();
        lProject.setCertNumber(cert.length);
        for (X509Certificate c : cert) {
            System.out.println(c.toString());
            certInfo = new CertificateModel(c);
            lProject.addCertInfo(certInfo);
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
