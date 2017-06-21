import droidefense.pscout.PSCoutModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class PScoutLauncher {

    public static void main(String[] args) throws IOException {
        File outputDir = new File("");
        File csv = new File(
                outputDir.getAbsolutePath()
                        +File.separator
                        +".."
                        +File.separator
                        +"pscout"
                        +File.separator
                        +"files"
                        +File.separator
                        +"mapping_4.1.1-5.0.2.csv"
        );
        PSCoutModel model = new PSCoutModel(csv.getAbsolutePath());

        String perms = model.getCallPermissions("com/android/server/ThrottleService.getByteCount");
        System.out.println(perms);
        //android/preference/PreferenceManager.getDefaultSharedPreferencesMode()
        perms = model.getCallPermissions("org/apache/xpath/objects/XMLStringFactoryImpl.newstr");
        System.out.println(perms);

        //once loaded, save the model in hdd
        File modelFile = new File(outputDir.getAbsolutePath() + File.separator + "pscout.model");
        FileOutputStream fout = new FileOutputStream(modelFile);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(model);
        oos.close();
        System.out.println("Model saved");
        System.out.println("Path: "+modelFile.getAbsolutePath());
        System.out.println(model.size());
    }
}
