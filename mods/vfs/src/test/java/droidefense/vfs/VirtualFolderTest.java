package droidefense.vfs;

import droidefense.vfs.model.base.IVirtualNode;
import droidefense.vfs.model.impl.VirtualFileSystem;
import droidefense.vfs.model.impl.VirtualFolder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VirtualFolderTest {

    private VirtualFileSystem vfs;

    @Before
    public void before(){
        vfs = new VirtualFileSystem();
    }

    @Test
    public void t00_vfs_creation() {
        Assert.assertNotNull(vfs);
    }

    @Test
    public void t01_create_folder() {
        if(this.vfs!=null){
            String folderName = "root";
            VirtualFolder folder = VirtualFolder.createFolder(folderName);

            Assert.assertNotNull(folder);

            boolean result = this.vfs.add(folderName, folder);

            Assert.assertEquals(result, true);
        }
    }

    @Test
    public void t02_get_folder() {
        this.t01_create_folder();
        if(this.vfs!=null){
            String folderName = "root";
            IVirtualNode folder = this.vfs.get(folderName);

            Assert.assertNotNull(folder);
        }
    }

    @Test
    public void t03_get_element_count() {

        if(this.vfs!=null){
            int count =this.vfs.getTotalElements();
            Assert.assertEquals(count, 0);
        }

        this.t01_create_folder();

        if(this.vfs!=null){
            int count =this.vfs.getTotalElements();
            Assert.assertEquals(count, 1);
        }
    }

    @Test
    public void t04_get_folder() {
        if(this.vfs!=null){
            String folderName = "root2";
            IVirtualNode folder = this.vfs.get(folderName);
            Assert.assertNull(folder);
        }
    }


    @Test
    public void test() {
        File currentDir = new File("");
        System.out.println("PATH: "+currentDir.getAbsolutePath());
    }
}
