package droidefense.vfs.model.base;

/**
 * Created by .local on 08/10/2016.
 */
public interface IVirtualNode {

    boolean isFile();

    boolean isFolder();

    boolean hasParentNode();

    IVirtualNode getParentNode();

    IVirtualNode getRootNode();

    String getName();

    String toString();

    boolean equals(Object o);

    int hashCode();

    long estimatedInMemorySize();

    int getItemsInside();

    void updateItemsInsideSize(int length);

    String getPath();

    VirtualNodeType getType();

    byte[] getContent();

    public int getVirtualFoldersInside();

    public void setVirtualFoldersInside(int virtualFoldersInside);

    public int getVirtualFilesInside();

    public void setVirtualFilesInside(int virtualFilesInside);


    IVirtualNode getItem(String name);

    boolean isRootNode();
}
