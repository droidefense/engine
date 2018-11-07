package droidefense.worker.parser;

import android.content.res.AXMLResource;
import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.vfs.model.impl.VirtualFile;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by .local on 21/06/2017.
 */
public class InMemoryAXMLParser {

    private VirtualFile inputFile;

    public void decode() {
        //todo decode virtual file, which represent a binary xml to readable xml
        if (inputFile != null && inputFile.getContent() != null) {
            try {
                String decoded = getDecodedXML();
                if (decoded != null) {
                    this.inputFile.setContent(decoded);
                }
            } catch (XmlPullParserException e) {
                Log.write(LoggerType.ERROR, "Could not decode axml file", e.getLocalizedMessage());
            } catch (IOException e) {
                Log.write(LoggerType.ERROR, "IO error when decoding axml file", e.getLocalizedMessage());
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.write(LoggerType.FATAL, "Current axml decoder is not 100% compatible with given input", e.getLocalizedMessage());
            }
        }
    }

    private String getDecodedXML() throws XmlPullParserException, IOException {
        //return new AXMLPrinter(inputFile.getContent()).getResult();
        InputStream stream = inputFile.getStream();
        if (stream != null) {
            AXMLResource axmlResource = new AXMLResource();
            try {
                axmlResource.read(stream);
                String decoded = axmlResource.toXML();
                stream.close();
                return decoded;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setInputFile(VirtualFile inputFile) {
        this.inputFile = inputFile;
    }

    public VirtualFile getDecodedFile() {
        return inputFile;
    }
}
