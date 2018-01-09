package droidefense.vfs.model.impl;

import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.vfs.model.base.IVirtualNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by .local on 08/10/2016.
 */
public final class VirtualFileSystem {

    private ArrayList<VirtualFolder> folderList;
    private ArrayList<VirtualFile> fileList;

    private final HashMap<String, IVirtualNode> storage;
    private IVirtualNode firstRootNode;

    private int totalElements;
    private long sizeBytes;

    public VirtualFileSystem() {
        this.storage = new HashMap<>();
        this.folderList = new ArrayList<>();
        this.fileList = new ArrayList<>();
        this.totalElements = 0;
        this.sizeBytes = 0;
        this.firstRootNode = null;
    }

    public boolean setRootFolder(VirtualFolder rootFolder) {
        return this.add("", rootFolder);
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
        if (key != null) {
            return storage.get(key);
        }
        return null;
    }

    public void info() {
        Log.write(LoggerType.DEBUG, "-----------VFS INFO BEGIN-----------");
        Log.write(LoggerType.DEBUG,"Total elements in VFS: \t" + totalElements);
        Log.write(LoggerType.DEBUG,"Total folders in VFS: \t" + folderList.size());
        Log.write(LoggerType.DEBUG,"Total files in VFS: \t" + fileList.size());
        Log.write(LoggerType.DEBUG,"Estimated VFS size: \t");
        Log.write(LoggerType.DEBUG,"\t\tBytes: " + sizeBytes);
        Log.write(LoggerType.DEBUG,"\t\tKb: " + sizeBytes / 1000);
        Log.write(LoggerType.DEBUG,"\t\tMb: " + sizeBytes / 1000 / 1000);
        Log.write(LoggerType.DEBUG,"\t\tGb: " + sizeBytes / 1000 / 1000 / 1000);
        Log.write(LoggerType.DEBUG,"\t\tTb: " + sizeBytes / 1000 / 1000 / 1000 / 1000);
        Log.write(LoggerType.DEBUG,"-----------VFS INFO END-----------");
        print();
    }

    public void print() {
        Log.write(LoggerType.DEBUG,"-----------VFS FILE TREE BEGIN-----------");
        Log.write(LoggerType.DEBUG,this.tree());
        Log.write(LoggerType.DEBUG,"-----------VFS FILE TREE END-----------");
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
            HashMap<String, IVirtualNode> items = vfolder.getItemList();
            for (Object o : items.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                //Log.write(LoggerType.DEBUG,pair.getKey() + " = " + pair.getValue());
                IVirtualNode iv = (IVirtualNode) pair.getValue();
                sb.append(printItemIntree(new StringBuilder(), separator + "  ", iv));
                //it.remove(); // avoids a ConcurrentModificationException
            }
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        return "VFS: elements -> " + this.totalElements + " and bytes -> " + this.sizeBytes;
    }

    public void dump(String path) {
        Log.write(LoggerType.DEBUG,"-----------VFS DUMPING BEGIN-----------");
        path = new File(path).getAbsolutePath();
        dumpInPath(path, firstRootNode);
        Log.write(LoggerType.DEBUG,"-----------VFS DUMPING END-----------");
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

            HashMap<String, IVirtualNode> items = vfolder.getItemList();
            for (Object o : items.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                //Log.write(LoggerType.DEBUG,pair.getKey() + " = " + pair.getValue());
                IVirtualNode iv = (IVirtualNode) pair.getValue();
                dumpInPath(path, iv);
                //it.remove(); // avoids a ConcurrentModificationException
            }
        }
    }

    public IVirtualNode getFirstRootNode() {
        return firstRootNode;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public ArrayList<VirtualFolder> getFolderList() {
        return this.folderList;
    }

    public ArrayList<VirtualFile> getFileList() {
        return this.fileList;
    }

    public boolean addFolder(VirtualFolder parentNode) {
        return this.folderList.add(parentNode);
    }

    public boolean addFile(VirtualFile virtualFile) {
        return this.fileList.add(virtualFile);
    }
}
