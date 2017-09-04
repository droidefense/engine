package droidefense.vfs;

import droidefense.vfs.model.base.IVirtualNode;
import droidefense.vfs.model.base.VirtualNodeType;
import droidefense.vfs.model.impl.VirtualFile;
import droidefense.vfs.model.impl.VirtualFileSystem;
import droidefense.vfs.model.impl.VirtualFolder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VirtualFileTest {

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
    public void t01_create_file() {
        if(this.vfs!=null){
            System.out.println("creating file....");
            String filename = "hello.txt";
            VirtualFile file = VirtualFile.createFile(filename);

            Assert.assertNotNull(file);

            boolean result = this.vfs.add(filename, file);

            Assert.assertEquals(result, true);
        }
    }

    @Test
    public void t02_get_file() {
        this.t01_create_file();
        if(this.vfs!=null){
            String filename = "hello.txt";
            IVirtualNode file = this.vfs.get(filename);

            Assert.assertNotNull(file);
        }
    }

    @Test
    public void t03_get_element_count() {

        if(this.vfs!=null){
            int count =this.vfs.getTotalElements();
            Assert.assertEquals(count, 0);
        }

        this.t01_create_file();

        if(this.vfs!=null){
            int count =this.vfs.getTotalElements();
            Assert.assertEquals(count, 1);
        }
    }

    @Test
    public void t04_get_file() {
        if(this.vfs!=null){
            String filename = "hallo.txt";
            IVirtualNode file = this.vfs.get(filename);
            Assert.assertNull(file);
        }
    }

    @Test
    public void t05_get_file_add_content() {
        if(this.vfs!=null){
            this.t01_create_file();
            System.out.println("retrieving file....");
            String filename = "hello.txt";
            VirtualFile file = (VirtualFile) this.vfs.get(filename);
            Assert.assertNotNull(file);

            System.out.println("Adding data to file....");
            String content = "Demo content";
            file.write(content);
            Assert.assertEquals(content, new String(file.getContent()) );

            System.out.println("checking filetype....");
            Assert.assertEquals(file.getType() == VirtualNodeType.FILE, true);
        }
    }

    @Test
    public void test() {
        File currentDir = new File("");
        System.out.println("PATH: "+currentDir.getAbsolutePath());
    }
}
