import apkr.external.module.AtomManifestParser;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by sergio on 24/3/16.
 */
public class ManifestMediumTest {

    @Test
    public void parserTest() throws IOException, SAXException, ParserConfigurationException {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-medium.xml");
        parser.parse(f);
        assert parser.getManifest() != null;
    }

    @Test
    public void readPackageNameTest() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-medium.xml");
        parser.parse(f);
        assert parser.getManifest().getPackageName().equals("com.mitelelite");
    }

    @Test
    public void countPermissions() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-medium.xml");
        parser.parse(f);
        assertEquals(parser.getPermissions().size(), 12);
    }

    @Test
    public void hasMainClassTest() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-medium.xml");
        parser.parse(f);
        assert parser.getMainClass() != null;
    }

    @Test
    public void getMainClassName() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-medium.xml");
        parser.parse(f);
        assert parser.getMainClass().getName().equals("com.mitelelite.MainActivity");
    }

    @Test
    public void isDebuggeableTest() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-medium.xml");
        parser.parse(f);
        assert !parser.getManifest().getApplication().isDebuggable();
    }

    @Test
    public void activityCountTest() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-medium.xml");
        parser.parse(f);
        assert parser.getManifest().getApplication().getActivities().size() == 41;
    }

    @Test
    public void intentFilterCountTest() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-medium.xml");
        parser.parse(f);
        assertEquals(parser.getManifest().getAllFilters().size(), 6);
    }

    @Test
    public void platformBuildVersionCodeTest() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-medium.xml");
        parser.parse(f);
        assertEquals(parser.getManifest().getVersionCode(), 21);
    }

    @Test
    public void platformBuildVersionNameTest() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-medium.xml");
        parser.parse(f);
        assertEquals(parser.getManifest().getVersionName(), "5.0.1-1624448");
    }

    @Test
    public void getOtherPermissionCount() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-medium.xml");
        parser.parse(f);
        assertEquals(parser.getManifest().getPermissionList().size(), 1);
    }

    @Test
    public void readAppNameTest() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-medium.xml");
        parser.parse(f);
        assertEquals(parser.getManifest().getApplication().getName(), "com.internal.bootstrap.ApplicationManager");
    }

    @Test
    public void notMappedValuesCountTest() throws Exception {
        AtomManifestParser parser = new AtomManifestParser();
        //load xml file
        File f = new File(new File("").getAbsolutePath() + File.separator + "res" + File.separator + "AndroidManifest-medium.xml");
        parser.parse(f);
        ArrayList<Object> list = new ArrayList<>(parser.getManifest().getNotMapped().values());
        list.forEach(System.out::println);
        assertEquals(parser.getManifest().getNotMapped().size(), 1);
    }
}
