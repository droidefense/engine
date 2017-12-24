package droidefense.worker.parser;

import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.axml.AXMLPrinter;
import droidefense.axml.exception.XmlPullParserException;
import droidefense.vfs.model.impl.VirtualFile;

import java.io.IOException;

/**
 * Created by .local on 21/06/2017.
 */
public class InMemoryAXMLParser {

    private VirtualFile inputFile;

    public void decode() {
        //todo decode virtual file, which represent a binary xml to readable xml
        if (inputFile != null && inputFile.getContent() != null) {
            try {
                String decoded = new AXMLPrinter(inputFile.getContent()).getResult();
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

    public void setInputFile(VirtualFile inputFile) {
        this.inputFile = inputFile;
    }

    public VirtualFile getDecodedFile() {
        return inputFile;
    }
}
