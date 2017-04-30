package droidefense.vfs;

import droidefense.vfs.model.impl.VirtualFile;
import droidefense.vfs.model.impl.VirtualFileSystem;
import droidefense.vfs.model.impl.VirtualFolder;

public class Main {

    public static void main(String[] args) {
        VirtualFileSystem vfs = new VirtualFileSystem();
        //create a folder called test, and inside a document called hello.txt with content 'hello world'

        //create folder called 'test'
        VirtualFolder vfolder = VirtualFolder.createFolder("test");
        VirtualFolder.createFolder(vfolder, "test1");

        //create a file called 'hello.txt'
        VirtualFile vfile = VirtualFile.createFile(vfolder, "hello.txt");

        //set file content
        vfile.setContent("hello world");

        //save folder into vfs. It automatically saves children objects
        vfs.add("test", vfolder);
        vfs.info();
        vfs.print();
        //vfs.dump("");
    }
}
