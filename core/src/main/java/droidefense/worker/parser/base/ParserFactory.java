package droidefense.worker.parser.base;

import droidefense.exception.UnknownParserException;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.LocalApkFile;
import droidefense.worker.parser.*;

/**
 * Created by r00t on 24/10/15.
 */
public abstract class ParserFactory {

    public static final byte CERTIFICATE_PARSER = 0x0;
    public static final byte MANIFEST_PARSER = 0x1;
    public static final byte RESOURCE_PARSER = 0x2;
    public static final byte STEGANOS = 0x4;
    public static final byte STATIC_META = 0x5;
    public static final byte CODE_DECOMPILER = 0x6;

    public static AbstractFileParser getParser(byte id, DroidefenseProject p, LocalApkFile a) throws UnknownParserException {
        switch (id) {
            case CERTIFICATE_PARSER:
                return new AndroidCertParser(a, p);
            case MANIFEST_PARSER:
                return new AndroidManifestParser(a, p);
            case RESOURCE_PARSER:
                return new AndroidResourcesParser(a, p);
            case STATIC_META:
                return new APKMetaParser(a, p);
            case STEGANOS:
                return new SteganosParser(a, p);
            case CODE_DECOMPILER:
                return new ApkDecompilerParser(a, p);
        }
        throw new UnknownParserException("An parser with id " + Integer.toHexString(id) + " was requested but it does not exists");
    }

}
