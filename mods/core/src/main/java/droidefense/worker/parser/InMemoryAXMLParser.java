package droidefense.worker.parser;

import droidefense.axml.axml.AXMLPrinter;
import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.vfs.model.impl.VirtualFile;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by .local on 21/06/2017.
 */
public class InMemoryAXMLParser {

    private VirtualFile inputFile;

    public void decode() {
        //todo decode virtual file, which represent a binary xml to readable xml
        if (inputFile != null && inputFile.getContent().length > 0) {
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
        byte[] content = inputFile.getContent();
        if (content.length > 0) {
            return AXMLPrinter.GetManifestXMLFromBytes(content);
        }
        return "";
    }

    public void setInputFile(VirtualFile inputFile) {
        this.inputFile = inputFile;
    }

    public VirtualFile getDecodedFile() {
        return inputFile;
    }
}
