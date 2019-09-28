package droidefense.axml;

public class StringItem {

    public String data;
    public int dataOffset;
    public int index;

    public StringItem() {
        super();
    }

    public StringItem(String data) {
        super();
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StringItem other = (StringItem) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        return result;
    }

    public String toString() {
        return String.format("S%04d %s", index, data);
    }

}
