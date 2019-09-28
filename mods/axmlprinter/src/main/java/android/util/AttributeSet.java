package android.util;

public interface AttributeSet {
    int getAttributeCount();

    String getAttributeName(int index);

    String getAttributeValue(int index);

    String getPositionDescription();

    int getAttributeNameResource(int index);

    int getAttributeListValue(int index, String options[], int defaultValue);

    boolean getAttributeBooleanValue(int index, boolean defaultValue);

    int getAttributeResourceValue(int index, int defaultValue);

    int getAttributeIntValue(int index, int defaultValue);

    int getAttributeUnsignedIntValue(int index, int defaultValue);

    float getAttributeFloatValue(int index, float defaultValue);

    String getIdAttribute();

    String getClassAttribute();

    int getIdAttributeResourceValue(int index);

    int getStyleAttribute();

    String getAttributeValue(String namespace, String attribute);

    int getAttributeListValue(String namespace, String attribute,
                              String options[], int defaultValue);

    boolean getAttributeBooleanValue(String namespace, String attribute,
                                     boolean defaultValue);

    int getAttributeResourceValue(String namespace, String attribute,
                                  int defaultValue);

    int getAttributeIntValue(String namespace, String attribute,
                             int defaultValue);

    int getAttributeUnsignedIntValue(String namespace, String attribute,
                                     int defaultValue);

    float getAttributeFloatValue(String namespace, String attribute,
                                 float defaultValue);

    // TODO: remove
    int getAttributeValueType(int index);

    int getAttributeValueData(int index);
}
