package droidefense.pscout;

import java.io.*;
import java.util.HashMap;

public final class PSCoutModel extends HashMap<String, PScoutItem> implements Serializable {

    private static final String CVS_SPLIT_BY = ",";
    private final String file;

    public PSCoutModel(String file) {
        this.file = file;
        load(file);
    }

    private void load(String filePath) {
        BufferedReader br = null;
        String line = "";

        try {

            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] data = line.split(CVS_SPLIT_BY);
                PScoutItem item = new PScoutItem(data[0], data[1], data[2], data[3], data[4]);
                this.put(item.getKey(), item);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done");
    }

    public synchronized String getCallPermissions(String s) {
        PScoutItem ret = this.get(s);
        if (ret != null)
            return ret.getPermission();
        return null;
    }
}
