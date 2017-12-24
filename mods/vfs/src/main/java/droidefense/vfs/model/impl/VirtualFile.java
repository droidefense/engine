package droidefense.vfs.model.impl;

import droidefense.vfs.model.base.IVirtualNode;
import droidefense.vfs.model.base.VirtualNode;
import droidefense.vfs.model.base.VirtualNodeType;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by .local on 08/10/2016.
 */
public final class VirtualFile extends VirtualNode {

    private transient ArrayList<Byte> content;
    private String charset;

    private static byte[] toByteArray(List<Byte> in) {
        final int n = in.size();
        byte ret[] = new byte[n];
        for (int i = 0; i < n; i++) {
            ret[i] = in.get(i);
        }
        return ret;
    }

    public static VirtualFile createFile(VirtualFolder parentFolder, String name){
        //1 check if that virtual folder already exists on parentFolder
        VirtualFile item = parentFolder.getFile(name);
        if(item==null){
            return new VirtualFile(parentFolder, name);
        }
        return item;
    }

    public static VirtualFile createFile(String name){
        //TODO check if this new virtual file exists already
        return new VirtualFile(name);
    }

    private VirtualFile(String name) {
        super(name);
        if(parentNode!=null){
            parentNode.setVirtualFilesInside(parentNode.getVirtualFilesInside()+1);
        }
        else{
            this.setVirtualFilesInside(1);
        }
        content = new ArrayList<>();
        charset = "";
    }

    private VirtualFile(VirtualFolder parent, String name) {
        super(parent, name);
        parent.addAsFolderFile(this);
        parent.setVirtualFilesInside(parent.getVirtualFilesInside()+1);
        content = new ArrayList<>();
        charset = "";
    }

    public boolean hasContent() {
        return content != null && content.size()>0;
    }

    //CLASS METHODS

    public void setContent(String content) {

        //remove old indicator value
        this.updateParentContentLength(-this.content.size());

        //clear old content
        this.content.clear();

        for(byte b : content.getBytes()){
            this.content.add(b);
        }
        //update virtualFile size indicator too

        //add new indicator
        this.updateParentContentLength(content.length());
    }

    private void updateParentContentLength(int length) {
        if(this.parentNode!=null){
            parentNode.updateItemsInsideSize(length);
        }
    }

    public void setContent(String content, String charset) {
        try {
            //remove old indicator value
            this.updateParentContentLength(-this.content.size());

            //clear old content
            this.content.clear();

            for(byte b : content.getBytes(charset)){
                this.content.add(b);
            }
            this.charset = charset;
            //update virtualFile size indicator too

            //add new indicator
            this.updateParentContentLength(content.length());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isFile() {
        return true;
    }

    //IMPLEMENTED METHODS

    @Override
    public boolean isFolder() {
        return false;
    }

    public void addContent(byte[] buf, int len, int off) {

        if (buf == null) {
            throw new NullPointerException();
        }
        int endoff = off + len;
        if (off < 0 || len < 0 || endoff > buf.length || endoff < 0) {
            throw new IndexOutOfBoundsException();
        }

        for(int i = 0; i < endoff; i++){
            this.content.add(buf[i]);
        }
        this.updateParentContentLength(len);
    }

    //HELPER METHODS
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VirtualFile{");
        sb.append("name=").append(getName());
        sb.append(", ");
        if (hasContent()) {
            sb.append(", ");
            sb.append("content length=").append(content.size());
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public long estimatedInMemorySize() {
        return NEW_CLASS_ALLOCATION_HEAP_SIZE + getContentLength();
    }

    @Override
    public int getItemsInside() {
        //the file itself
        return 1;
    }

    @Override
    public void updateItemsInsideSize(int length) {
        //not applicable here
    }

    @Override
    public VirtualNodeType getType() {
        return VirtualNodeType.FILE;
    }

    public int getContentLength() {
        return (content == null) ? 0 : content.size();
    }

    public byte[] getContent() {
        byte[] data = new byte[content.size()];
        for(int i =0; i< content.size();i++)
            data[i]=content.get(i);
        return data;
    }

    public void write(String data) {
        if(data!=null){
            byte[] byteData = data.getBytes();
            this.addContent(byteData, byteData.length, 0);
        }
    }

    public void append(String data) {
        if(data!=null){
            byte[] byteData = data.getBytes();
            this.addContent(byteData, byteData.length, this.getContentLength());
        }
    }

    @Override
    public IVirtualNode getItem(String name) {
        if(name!=null && this.name!=null && this.name.equals(name)){
            return this;
        }
        return null;
    }

    @Override
    public boolean isRootNode() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VirtualFile that = (VirtualFile) o;

        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        return charset != null ? charset.equals(that.charset) : that.charset == null;

    }

    @Override
    public int hashCode() {
        int result = content != null ? content.hashCode() : 0;
        result = 31 * result + (charset != null ? charset.hashCode() : 0);
        return result;
    }
}
