package atom.core.external.pscout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by B328316 on 01/03/2016.
 */
public class Demo {

    public static void main(String[] args) throws IOException {
        PSCoutModel model = new PSCoutModel("/Users/sergio/Documents/Atom-git/engine/modules-src/pscout-module/files/mapping_4.1.1-5.0.2.csv");

        String perms = model.getCallPermissions("com/android/server/ThrottleService.getByteCount(Ljava/lang/String;III)J");
        System.out.println(perms);
        perms = model.getCallPermissions("org/apache/xpath/objects/XMLStringFactoryImpl.newstr([CII)Lorg/apache/xml/utils/XMLString;");
        System.out.println(perms);

        //once loaded, save the model in hdd
        File outputDir = new File("");
        FileOutputStream fout = new FileOutputStream(outputDir.getAbsolutePath() + File.separator + "pscout.model");
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(model);
        oos.close();
        System.out.println("Model saved");
        System.out.println(model.size());
    }
}
