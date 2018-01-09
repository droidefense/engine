package droidefense.vfs.model.impl;

import droidefense.vfs.model.base.IVirtualNode;
import droidefense.vfs.model.base.VirtualNode;
import droidefense.vfs.model.base.VirtualNodeType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by .local on 08/10/2016.
 */
public class VirtualFolder extends VirtualNode {

    private HashMap<String, IVirtualNode> itemsInside;
    private int itemsInsideSize;

    private VirtualFolder(VirtualFolder parentFolder, String name) {
        super(parentFolder, name);
        this.itemsInside = new HashMap<>();
        if(parentFolder!=null){
            //execute this if current object is not root node
            parentFolder.addAsFileToParentFolder(this);
            parentFolder.setVirtualFoldersInside(parentFolder.getVirtualFoldersInside() + 1);
        }
    }

    private VirtualFolder(String name) {
        this(null, name);
        if (parentNode != null) {
            parentNode.setVirtualFoldersInside(parentNode.getVirtualFoldersInside() + 1);
        } else {
            this.setVirtualFoldersInside(1);
        }
    }

    public static VirtualFolder createFolder(VirtualFolder parentFolder, String name) {
        //1 check if that virtual folder already exists on parentFolder
        VirtualFolder item = parentFolder.getFolder(name);
        if (item == null) {
            return new VirtualFolder(parentFolder, name);
        }
        return item;
    }

    public static VirtualFolder createFolder(String name) {
        //TODO check if this new virtual folder does not already exist
        return new VirtualFolder(name);
    }
    public static VirtualFolder createFolder() {
        //TODO check if this new virtual folder does not already exist
        return new VirtualFolder("");
    }


    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isFolder() {
        return true;
    }

    @Override
    public String toString() {
        return "VirtualNode{" + "parentNode=" + parentNode +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VirtualNode that = (VirtualNode) o;

        if (parentNode != null ? !parentNode.equals(that.getParentNode()) : that.getParentNode() != null) return false;
        return (name != null) ? name.equals(that.getName()) : (that.getName() == null);

    }

    @Override
    public int hashCode() {
        int result = parentNode != null ? parentNode.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    /**
     * This is just an estimation. Precision depends on the complexity of estimatedInMemorySize() method
     *
     * @return an estimation of the size in memory of this object
     */
    @Override
    public long estimatedInMemorySize() {
        return NEW_CLASS_ALLOCATION_HEAP_SIZE + itemsInsideSize;
    }

    @Override
    public int getItemsInside() {
        //return folder itselft plus all items inside it
        return 1 + itemsInside.size();
    }

    public void addAsFileToParentFolder(IVirtualNode node) {
        if (node != null) {
            this.itemsInside.put(node.getPath(), node);
            this.itemsInsideSize += node.estimatedInMemorySize();
            //increment also parent node
            if (this.parentNode != null && this.parentNode.isFolder()) {
                ((VirtualFolder) this.parentNode).addAsFileToParentFolder(node);
            }
        }
    }

    public void updateItemsInsideSize(int size) {
        this.itemsInsideSize += size;
    }

    @Override
    public VirtualNodeType getType() {
        return VirtualNodeType.FOLDER;
    }

    @Override
    public byte[] getContent() {
        return null;
    }

    @Override
    public IVirtualNode getItem(String name) {
        return this.itemsInside.get(name);
    }

    @Override
    public boolean isRootNode() {
        return getPath().equals(NO_PATH);
    }

    public HashMap<String, IVirtualNode> getItemList() {
        return itemsInside;
    }

    public VirtualFile getFile(String name) {
        Object o = this.itemsInside.get(name);
        if(o instanceof VirtualFile){
            return (VirtualFile) o;
        }
        return null;
    }

    public VirtualFolder getFolder(String name) {
        Object o = this.itemsInside.get(name);
        if(o instanceof VirtualFolder){
            return (VirtualFolder) o;
        }
        return null;
    }
}
