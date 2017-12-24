package droidefense.sdk.model.dex;

/**
 * Created by .local on 23/11/2016.
 */
public class DalvikDexModel {

    private byte[] magic;
    private byte[] checksum;
    private byte[] hash;
    private byte[] fileSize;
    private byte[] headerSize;
    private byte[] endian_tag;
    private byte[] link_size;
    private byte[] link_offset;
    private byte[] map_offset;
    private byte[] string_ids;
    private byte[] string_table;
    private byte[] type_ids;
    private byte[] type_table;
    private byte[] proto_ids;
    private byte[] proto_table;
    private byte[] field_ids;
    private byte[] field_offset;
    private byte[] methods_ids;
    private byte[] methods_offset;
    private byte[] class_def;
    private byte[] class_offset;
    private byte[] data_size;
    private byte[] data_offset;
    private String endianString;

    public byte[] getMagic() {
        return magic;
    }

    public void setMagic(byte[] magic) {
        this.magic = magic;
    }

    public byte[] getChecksum() {
        return checksum;
    }

    public void setChecksum(byte[] checksum) {
        this.checksum = checksum;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getFileSize() {
        return fileSize;
    }

    public void setFileSize(byte[] fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getHeaderSize() {
        return headerSize;
    }

    public void setHeaderSize(byte[] headerSize) {
        this.headerSize = headerSize;
    }

    public byte[] getEndian_tag() {
        return endian_tag;
    }

    public void setEndian_tag(byte[] endian_tag) {
        this.endian_tag = endian_tag;
    }

    public byte[] getLink_size() {
        return link_size;
    }

    public void setLink_size(byte[] link_size) {
        this.link_size = link_size;
    }

    public byte[] getLink_offset() {
        return link_offset;
    }

    public void setLink_offset(byte[] link_offset) {
        this.link_offset = link_offset;
    }

    public byte[] getMap_offset() {
        return map_offset;
    }

    public void setMap_offset(byte[] map_offset) {
        this.map_offset = map_offset;
    }

    public byte[] getString_ids() {
        return string_ids;
    }

    public void setString_ids(byte[] string_ids) {
        this.string_ids = string_ids;
    }

    public byte[] getString_table() {
        return string_table;
    }

    public void setString_table(byte[] string_table) {
        this.string_table = string_table;
    }

    public byte[] getType_ids() {
        return type_ids;
    }

    public void setType_ids(byte[] type_ids) {
        this.type_ids = type_ids;
    }

    public byte[] getType_table() {
        return type_table;
    }

    public void setType_table(byte[] type_table) {
        this.type_table = type_table;
    }

    public byte[] getProto_ids() {
        return proto_ids;
    }

    public void setProto_ids(byte[] proto_ids) {
        this.proto_ids = proto_ids;
    }

    public byte[] getProto_table() {
        return proto_table;
    }

    public void setProto_table(byte[] proto_table) {
        this.proto_table = proto_table;
    }

    public byte[] getField_ids() {
        return field_ids;
    }

    public void setField_ids(byte[] field_ids) {
        this.field_ids = field_ids;
    }

    public byte[] getField_offset() {
        return field_offset;
    }

    public void setField_offset(byte[] field_offset) {
        this.field_offset = field_offset;
    }

    public byte[] getMethods_ids() {
        return methods_ids;
    }

    public void setMethods_ids(byte[] methods_ids) {
        this.methods_ids = methods_ids;
    }

    public byte[] getMethods_offset() {
        return methods_offset;
    }

    public void setMethods_offset(byte[] methods_offset) {
        this.methods_offset = methods_offset;
    }

    public byte[] getClass_def() {
        return class_def;
    }

    public void setClass_def(byte[] class_def) {
        this.class_def = class_def;
    }

    public byte[] getClass_offset() {
        return class_offset;
    }

    public void setClass_offset(byte[] class_offset) {
        this.class_offset = class_offset;
    }

    public byte[] getData_size() {
        return data_size;
    }

    public void setData_size(byte[] data_size) {
        this.data_size = data_size;
    }

    public byte[] getData_offset() {
        return data_offset;
    }

    public void setData_offset(byte[] data_offset) {
        this.data_offset = data_offset;
    }

    public String getEndianString() {
        return endianString;
    }

    public void setEndianString(String endianString) {
        this.endianString = endianString;
    }
}
