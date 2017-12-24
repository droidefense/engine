package droidefense.vfs;

import droidefense.sdk.model.io.LocalHashedFile;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Created by .local on 29/11/2016.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VirtualFilteTest {

    @Test
    public void test() {
        File currentDir = new File("");
        System.out.println("PATH: " + currentDir.getAbsolutePath());
        LocalHashedFile localRead = new LocalHashedFile(
                new File(currentDir.getAbsolutePath() + "/src/test/resources/vfs/classes.dex"),
                true
        );

        assertEquals(
                localRead.getName(),
                "classes.dex"
        );
        assertEquals(
                localRead.getSha256(),
                "2BCAE5D275264B52B8265C8E27B0F3F5EB1AE658A310F01C5D8D9943421191AD"
        );
    }
}
