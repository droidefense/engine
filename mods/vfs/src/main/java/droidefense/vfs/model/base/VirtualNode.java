package droidefense.vfs.model.base;

import java.io.File;
import java.io.Serializable;

/**
 * Created by .local on 08/10/2016.
 */
public abstract class VirtualNode implements IVirtualNode {

    protected static final int NEW_CLASS_ALLOCATION_HEAP_SIZE = 8;
    private static final String DEFAULT_INODE_NAME = "undefined";
    private static final String NO_PATH = "";
    private static final String FILE_SEPARATOR = "/";

    protected final IVirtualNode parentNode;
    protected final String name;
    protected int virtualFoldersInside;
    protected int virtualFilesInside;
    private String precalculatedPath;

    public VirtualNode(VirtualNode parentNode, String name) {
        this.name = name;
        this.parentNode = parentNode;
        this.virtualFilesInside=0;
        this.virtualFoldersInside=0;
        this.precalculatedPath = NO_PATH;
    }

    public VirtualNode(String name) {
        this(null, name);
    }

    public final boolean hasParentNode() {
        return this.parentNode != null;
    }

    public final IVirtualNode getParentNode() {
        return this.parentNode;
    }

    //TODO Add cache in next version

    /**
     * Escalate until top level inode.
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
        if(this.name == null){
            return DEFAULT_INODE_NAME;
        }
        return this.name;
    }

    @Override
    public String getPath() {
        if(this.precalculatedPath.equals(NO_PATH)){
            if (parentNode == null)
                this.precalculatedPath = this.name;
            else
                this.precalculatedPath = parentNode.getPath() + FILE_SEPARATOR + name;
        }
        return this.precalculatedPath;
    }

    public int getVirtualFoldersInside() {
        return this.virtualFoldersInside;
    }

    public void setVirtualFoldersInside(int virtualFoldersInside) {
        this.virtualFoldersInside = virtualFoldersInside;
    }

    public int getVirtualFilesInside() {
        return this.virtualFilesInside;
    }

    public void setVirtualFilesInside(int virtualFilesInside) {
        this.virtualFilesInside = virtualFilesInside;
    }

    public abstract long estimatedInMemorySize();

    public abstract int getItemsInside();

    public abstract boolean equals(Object o);

    public abstract int hashCode();
}
