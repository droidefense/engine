package droidefense.vfs.model.impl;

import droidefense.vfs.model.base.IVirtualNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by .local on 08/10/2016.
 */
public final class VirtualFileSystem {

    private final HashMap<String, IVirtualNode> storage;
    private int totalElements;
    private long sizeBytes;
    private IVirtualNode firstRootNode;

    public VirtualFileSystem() {
        storage = new HashMap<>();
        totalElements = 0;
        sizeBytes = 0;
        firstRootNode = null;
    }

    public boolean add(String key, IVirtualNode node) {
        storage.put(key, node);
        totalElements += node.getItemsInside();
        sizeBytes += node.estimatedInMemorySize();
        firstRootNode = node.getRootNode();
        return true;
    }

    public IVirtualNode remove(IVirtualNode node) {
        IVirtualNode removedNode = storage.remove(node);
        if (removedNode != null) {
            totalElements -= removedNode.getItemsInside();
            sizeBytes -= removedNode.estimatedInMemorySize();
        }
        return removedNode;
    }

    public IVirtualNode get(String key) {
        return storage.get(key);
    }

    public void info() {
        System.out.println("-----------VFS INFO BEGIN-----------");
        System.out.println("Total elements in VFS: \t" + totalElements);
        System.out.println("Total folders in VFS: \t" + firstRootNode.getVirtualFoldersInside());
        System.out.println("Total files in VFS: \t" + firstRootNode.getVirtualFilesInside());
        System.out.println("Estimated VFS size: \t");
        System.out.println("\t\tBytes: " + sizeBytes);
        System.out.println("\t\tKb: " + sizeBytes / 1000);
        System.out.println("\t\tMb: " + sizeBytes / 1000 / 1000);
        System.out.println("\t\tGb: " + sizeBytes / 1000 / 1000 / 1000);
        System.out.println("\t\tTb: " + sizeBytes / 1000 / 1000 / 1000 / 1000);
        System.out.println("-----------VFS INFO END-----------");
        System.out.println();
        print();
    }

    public void print() {
        System.out.println("-----------VFS FILE TREE BEGIN-----------");
        System.out.println(this.tree());
        System.out.println("-----------VFS FILE TREE END-----------");
    }

    private String tree() {
        return printItemIntree(new StringBuilder(), "  ", firstRootNode);
    }

    private String printItemIntree(StringBuilder sb, String separator, IVirtualNode v) {
        if (v.isFile()) {
            sb.append(separator);
            sb.append(v.getName());
            sb.append("\n");
            return sb.toString();
        } else {
            sb.append(separator);
            sb.append("/");
            sb.append(v.getName());
            sb.append("\n");
            //make it recursive for possible child items inside
            VirtualFolder vfolder = (VirtualFolder) v;
            ArrayList<IVirtualNode> items = vfolder.getItemList();
            for (IVirtualNode iv : items) {
                sb.append(printItemIntree(new StringBuilder(), separator + "  ", iv));
            }
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        return this.tree();
    }

    public void dump(String path) {
        System.out.println("-----------VFS DUMPING BEGIN-----------");
        path = new File(path).getAbsolutePath();
        dumpInPath(path, firstRootNode);
        System.out.println("-----------VFS DUMPING END-----------");
    }

    private void dumpInPath(String path, IVirtualNode node) {
        if (node.isFile()) {
            VirtualFile vf = (VirtualFile) node;
            try {
                Files.write(Paths.get(path + node.getPath()), vf.getContent());
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println(e.getLocalizedMessage());
            }
        } else {
            //create that folder
            String absolutePath = path + node.getPath();
            boolean success = new File(absolutePath).mkdirs();
            //make it recursive for possible child items inside
            VirtualFolder vfolder = (VirtualFolder) node;
            ArrayList<IVirtualNode> items = vfolder.getItemList();
            for (IVirtualNode iv : items) {
                dumpInPath(path, iv);
            }
        }
    }
}
