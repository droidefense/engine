import apkr.external.module.AtomManifestParser;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by sergio on 24/3/16.
 */
public class ManifestSmallTest {

    @org.junit.Test
    public void parserTest() throws IOException, SAXException, ParserConfigurationException {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-small.xml");
        parser.parse(f);
        assert parser.getManifest() != null;
    }

    @Test
    public void readPackageNameTest() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-small.xml");
        parser.parse(f);
        assert parser.getManifest().getPackageName().equals("com.metasploit.stage");
    }

    @Test
    public void countPermissions() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-small.xml");
        parser.parse(f);
        assertEquals(parser.getPermissions().size(), 14);
    }

    @Test
    public void hasMainClassTest() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-small.xml");
        parser.parse(f);
        assert parser.getMainClass() != null;
    }

    @Test
    public void getMainClassName() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-small.xml");
        parser.parse(f);
        assert parser.getMainClass().getName().equals(".MainActivity");
    }

    @Test
    public void isDebuggeableTest() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-small.xml");
        parser.parse(f);
        assert parser.getManifest().getApplication().isDebuggable();
    }

    @Test
    public void activityCountTest() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-small.xml");
        parser.parse(f);
        assert parser.getManifest().getApplication().getActivities().size() == 1;
    }

    @Test
    public void intentFilterCountTest() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-small.xml");
        parser.parse(f);
        assertEquals(parser.getManifest().getAllFilters().size(), 1);
    }

    @Test
    public void notMappedValuesCountTest() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-small.xml");
        parser.parse(f);
        assertEquals(parser.getManifest().getNotMapped().size(), 1);
    }
}
