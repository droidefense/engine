package droidefense.vfs.model.base;

import java.io.File;
import java.io.Serializable;

/**
 * Created by .local on 08/10/2016.
 */
public abstract class VirtualNode implements IVirtualNode, Serializable {

    protected static final int NEW_CLASS_ALLOCATION_HEAP_SIZE = 8;

    protected final IVirtualNode parentNode;
    protected final String name;
    protected int virtualFoldersInside;
    protected int virtualFilesInside;

    public VirtualNode(VirtualNode parentNode, String name) {
        this.name = name;
        this.parentNode = parentNode;
        virtualFilesInside=0;
        virtualFoldersInside=0;
    }

    public VirtualNode(String name) {
        this.name = name;
        this.parentNode = null;
    }

    public final boolean hasParentNode() {
        return parentNode != null;
    }

    public final IVirtualNode getParentNode() {
        return parentNode;
    }

    //TODO Add cache in next version

    /**
     * Escalate until top domain inode.
     *
     * @return top level IVirtualNode. something similar to / on linux o C:\ in Windows
     */
    public final IVirtualNode getRootNode() {
        IVirtualNode parent = getParentNode();
        while (parent != null) {
            IVirtualNode superioNode = parent.getParentNode();
            if (superioNode != null) {
                parent = superioNode;
            } else {
                return parent;
            }
        }
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    //TODO Add cache in next version
    @Override
    public String getPath() {
        if (parentNode == null)
            return File.separator + name;
        return parentNode.getPath() + File.separator + name;
    }

    public int getVirtualFoldersInside() {
        return virtualFoldersInside;
    }

    public void setVirtualFoldersInside(int virtualFoldersInside) {
        this.virtualFoldersInside = virtualFoldersInside;
    }

    public int getVirtualFilesInside() {
        return virtualFilesInside;
    }

    public void setVirtualFilesInside(int virtualFilesInside) {
        this.virtualFilesInside = virtualFilesInside;
    }

    public abstract long estimatedInMemorySize();

    public abstract int getItemsInside();

    public abstract boolean equals(Object o);

    public abstract int hashCode();
}
