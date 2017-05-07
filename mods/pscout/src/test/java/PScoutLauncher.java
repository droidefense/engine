import droidefense.pscout.PSCoutModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class PScoutLauncher {

    public static void main(String[] args) throws IOException {
        if(args.length != 1){
            System.err.println("Please enter pscout csv file as source");
            return;
        }
        String filename_csv = args[1];
        PSCoutModel model = new PSCoutModel(filename_csv);

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
