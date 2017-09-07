package droidefense.om.machine.base.struct.model;

public class AndroidRField {

    private String owner, name;
    private int value;

    public AndroidRField(String owner, String name, int value) {
        this.owner = owner;
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String reverseName(){
        return "android.R."+this.owner+"."+this.name;
    }

    @Override
    public String toString() {
        return "AndroidRField{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", owner='" + owner + '\'' +
                ", reversed='" + reverseName() + '\'' +
                '}';
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
